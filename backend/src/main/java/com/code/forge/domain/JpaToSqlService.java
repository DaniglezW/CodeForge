package com.code.forge.domain;

import com.code.forge.application.common.SqlOperationType;
import com.code.forge.application.common.Utils;
import com.code.forge.application.dto.FieldInfo;
import com.code.forge.application.response.FormatResponse;
import com.code.forge.domain.interfaces.IJpaToSqlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
@Service
public class JpaToSqlService implements IJpaToSqlService {

    @Override
    public FormatResponse convertJpaToSql(String jpaModel, SqlOperationType sqlOperationType) {
        log.info("Entering convertJpaToSql method with input:\n{}", jpaModel);

        String cleanedJpaModel = Utils.cleanAllOptionsSqlQuery(jpaModel);

        String entityName = extractEntityName(cleanedJpaModel);
        List<FieldInfo> attributes = extractFieldInfo(cleanedJpaModel);
        log.info("Detected JPA entity: {}", entityName);
        log.info("Detected attributes: {}", attributes);

        String sqlQuery = generateSqlQuery(sqlOperationType, entityName, attributes);

        byte[] fileContent = sqlQuery.getBytes(StandardCharsets.UTF_8);
        log.info("Generated SQL: {}", sqlQuery);

        return new FormatResponse(sqlQuery, fileContent);
    }

    private List<FieldInfo> extractFieldInfo(String jpaModel) {
        List<FieldInfo> fieldInfos = new ArrayList<>();
        Pattern pattern = Pattern.compile("private\\s+(\\w+)\\s+(\\w+);");
        Matcher matcher = pattern.matcher(jpaModel);

        while (matcher.find()) {
            String dataType = convertJavaTypeToSql(matcher.group(1));
            String columnName = matcher.group(2);
            fieldInfos.add(new FieldInfo(columnName, dataType, null, null));
        }
        return fieldInfos;
    }

    private String generateSqlQuery(SqlOperationType sqlOperationType, String entityName, List<FieldInfo> attributes) {
        String tableName = entityName.toLowerCase();
        String columns = attributes.stream().map(FieldInfo::getColumnName).collect(Collectors.joining(", "));

        return switch (sqlOperationType) {
            case CREATE -> "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    attributes.stream()
                            .map(attr -> attr.getColumnName() + " " + attr.getDataType())
                            .collect(Collectors.joining(", ")) +
                    ")";
            case SELECT -> "SELECT * FROM " + tableName;
            case INSERT -> "INSERT INTO " + tableName + " (" + columns + ") VALUES (" +
                    attributes.stream()
                            .map(attr -> "'" + attr.getColumnName().toUpperCase() + "_VALUE'")
                            .collect(Collectors.joining(", ")) +
                    ")";
            case UPDATE -> "UPDATE " + tableName + " SET " +
                    attributes.stream()
                            .map(attr -> attr.getColumnName() + " = '" + attr.getColumnName().toUpperCase() + "_VALUE'")
                            .collect(Collectors.joining(", ")) +
                    " WHERE id = ?";
            case DELETE -> "DELETE FROM " + tableName + " WHERE id = ?";
            default -> throw new IllegalArgumentException("Operación SQL no soportada");
        };
    }

    /**
     * Extrae el nombre de la entidad JPA desde el código fuente, ignorando los imports.
     */
    private String extractEntityName(String input) {
        // Eliminar líneas de import
        String cleanedInput = input.replaceAll("(?m)^import\\s+.*;\\s*", "").trim();

        // Buscar el nombre de la clase con `class NombreEntidad {` o `public class NombreEntidad {`
        Pattern pattern = Pattern.compile("\\bclass\\s+(\\w+)\\s*\\{");
        Matcher matcher = pattern.matcher(cleanedInput);

        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new IllegalArgumentException("Invalid JPA entity input: Unable to detect class name.");
    }

    private String convertJavaTypeToSql(String javaType) {
        return switch (javaType) {
            case "String" -> "VARCHAR(255)";
            case "int", "Integer" -> "INT";
            case "long", "Long" -> "BIGINT";
            case "boolean", "Boolean" -> "BOOLEAN";
            case "double", "Double" -> "DOUBLE";
            case "float", "Float" -> "FLOAT";
            case "LocalDate" -> "DATE";
            case "LocalDateTime" -> "TIMESTAMP";
            default -> "TEXT";
        };
    }

}
