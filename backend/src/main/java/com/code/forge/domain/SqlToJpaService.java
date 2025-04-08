package com.code.forge.domain;

import com.code.forge.application.common.Constants;
import com.code.forge.application.common.Utils;
import com.code.forge.application.dto.FieldInfo;
import com.code.forge.application.response.FormatResponse;
import com.code.forge.domain.interfaces.ISqlToJpaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.capitalize;

@Slf4j
@Service
public class SqlToJpaService implements ISqlToJpaService {

    @Override
    public FormatResponse convertSqlToJpa(String sqlQuery) {
        try {
            sqlQuery = Utils.cleanSqlQuery(sqlQuery);

            String sqlType = detectSqlType(sqlQuery);

            String jpaModel = switch (sqlType) {
                case "CREATE" -> generateJpaModel(sqlQuery);
                case Constants.SELECT -> generateJpaModelFromSelect(sqlQuery);
                case "UNSUPPORTED" ->
                        throw new IllegalArgumentException("Error: Solo se permiten consultas CREATE TABLE o SELECT.");
                default -> throw new IllegalArgumentException("Consulta SQL no reconocida.");
            };

            byte[] fileContent = jpaModel.getBytes(StandardCharsets.UTF_8);
            return new FormatResponse(jpaModel, fileContent);

        } catch (IllegalArgumentException e) {
            return new FormatResponse("Error: " + e.getMessage(), new byte[0]);
        }
    }

    private String detectSqlType(String sqlQuery) {
        String upperQuery = sqlQuery.trim().toUpperCase();
        if (upperQuery.contains("CREATE TABLE")) {
            return "CREATE";
        } else if (upperQuery.contains(Constants.SELECT)) {
            return Constants.SELECT;
        } else if (upperQuery.contains("INSERT") || upperQuery.contains("UPDATE")) {
            return "UNSUPPORTED";
        } else {
            return "UNKNOWN";
        }
    }

    private String generateJpaModel(String sqlQuery) {
        String tableName = extractTableName(sqlQuery);
        List<FieldInfo> fields = extractFields(sqlQuery);
        return buildJpaModel(tableName, fields);
    }

    private String generateJpaModelFromSelect(String sqlQuery) {
        Pattern selectPattern = Pattern.compile("SELECT\\s+(.*)\\s+FROM\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = selectPattern.matcher(sqlQuery);

        if (matcher.find()) {
            String columns = matcher.group(1);
            String tableName = matcher.group(2);

            List<FieldInfo> fields = extractFieldsFromSelect(columns);
            return buildJpaModel(tableName, fields);
        } else {
            throw new IllegalArgumentException("Error: No se pudo extraer información del SELECT.");
        }
    }

    private String extractTableName(String sqlQuery) {
        Pattern pattern = Pattern.compile("CREATE TABLE\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sqlQuery);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("No se pudo encontrar el nombre de la tabla en la consulta SQL.");
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
            case "BOOLEAN" -> "Boolean";
            case "TIMESTAMP", "DATETIME" -> "LocalDateTime";
            default -> Constants.STRING;
        };
    }

    private List<FieldInfo> extractFieldsFromSelect(String columns) {
        return Arrays.stream(columns.split(","))
                .map(col -> {
                    String columnName = col.trim();
                    return new FieldInfo(columnName, Constants.STRING, "255", "");
                }).toList();
    }

}
