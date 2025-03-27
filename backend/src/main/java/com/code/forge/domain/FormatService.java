package com.code.forge.domain;

import com.code.forge.application.dto.FieldInfo;
import com.code.forge.application.response.SqlToJpaResponse;
import com.code.forge.application.utils.Utils;
import com.code.forge.domain.interfaces.IFormatService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.capitalize;

@Slf4j
@Service
public class FormatService implements IFormatService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String convertJpaToSql(String jpaModel) {
        log.info("Entering into convert method on JpaToSqlService with input:\n{}", jpaModel);

        String entityName = extractEntityName(jpaModel);

        log.info("Detected JPA entity: {}", entityName);

        EntityType<?> entityType = entityManager.getMetamodel().getEntities()
                .stream()
                .filter(e -> e.getName().equalsIgnoreCase(entityName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Entity not found: " + entityName));

        String tableName = entityType.getName();

        Set<? extends SingularAttribute<?, ?>> attributes = entityType.getSingularAttributes();

        String columns = attributes.stream()
                .map(SingularAttribute::getName)
                .collect(Collectors.joining(", "));

        String sqlQuery = "SELECT " + columns + " FROM " + tableName;

        log.info("Generated SQL: {}", sqlQuery);
        return sqlQuery;
    }

    @Override
    public SqlToJpaResponse convertSqlToJpa(String sqlQuery) {

        sqlQuery = Utils.cleanSqlQuery(sqlQuery);

        String jpaModel = generateJpaModel(sqlQuery);

        byte[] fileContent = jpaModel.getBytes(StandardCharsets.UTF_8);

        return new SqlToJpaResponse(jpaModel, fileContent);
    }

    /**
     * Extrae el nombre de la entidad JPA desde el código fuente, ignorando los imports.
     */
    private String extractEntityName(String input) {
        // 1️⃣ Eliminar líneas de import
        String cleanedInput = input.replaceAll("(?m)^import\\s+.*;\\s*", "").trim();

        // 2️⃣ Buscar el nombre de la clase con `class NombreEntidad {` o `public class NombreEntidad {`
        Pattern pattern = Pattern.compile("\\bclass\\s+(\\w+)\\s*\\{");
        Matcher matcher = pattern.matcher(cleanedInput);

        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new IllegalArgumentException("Invalid JPA entity input: Unable to detect class name.");
    }

    private String generateJpaModel(String sqlQuery) {
        String tableName = extractTableName(sqlQuery);
        List<FieldInfo> fields = extractFields(sqlQuery);
        return buildJpaModel(tableName, fields);
    }

    private List<FieldInfo> extractFields(String sqlQuery) {
        List<FieldInfo> fields = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\w+)\\s+(\\w+)(?:\\((\\d+)\\))?\\s*(NOT NULL|UNIQUE|PRIMARY KEY)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sqlQuery);

        while (matcher.find()) {
            String columnName = matcher.group(1);
            String dataType = matcher.group(2);
            String length = matcher.group(3);
            String constraint = matcher.group(4);

            fields.add(new FieldInfo(columnName, dataType, length, constraint));
        }
        return fields;
    }

    private String buildJpaModel(String tableName, List<FieldInfo> fields) {
        String className = capitalize(tableName);
        StringBuilder sb = new StringBuilder();

        sb.append("@Entity\n");
        sb.append("@Table(name = \"" + tableName + "\")\n");
        sb.append("public class ").append(className).append(" {\n\n");

        for (FieldInfo field : fields) {
            if ("create".equalsIgnoreCase(field.getColumnName()) ||
                    "not".equalsIgnoreCase(field.getColumnName()) ||
                    "default".equalsIgnoreCase(field.getColumnName())) {
                continue;
            }

            if ("PRIMARY KEY".equalsIgnoreCase(field.getConstraint())) {
                sb.append("    @Id\n");
                sb.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
            }

            if ("NOT NULL".equalsIgnoreCase(field.getConstraint())) {
                sb.append("    @Column(nullable = false");
            } else {
                sb.append("    @Column(");
            }

            if (field.getLength() != null) {
                sb.append(", length = ").append(field.getLength());
            }

            sb.append(")\n");

            sb.append("    private ").append(mapSqlTypeToJava(field.getDataType()))
                    .append(" ").append(field.getColumnName().toLowerCase()).append(";\n\n");
        }
        sb.append("}\n");

        return sb.toString();
    }

    private String mapSqlTypeToJava(String sqlType) {
        return switch (sqlType.toUpperCase()) {
            case "INT", "SERIAL" -> "Integer";
            case "BIGINT" -> "Long";
            case "VARCHAR", "TEXT" -> "String";
            case "BOOLEAN" -> "Boolean";
            case "TIMESTAMP", "DATETIME" -> "LocalDateTime";
            default -> "String";
        };
    }



    private String extractTableName(String sqlQuery) {
        Pattern pattern = Pattern.compile("CREATE TABLE\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sqlQuery);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("No se pudo encontrar el nombre de la tabla en la consulta SQL.");
    }

}
