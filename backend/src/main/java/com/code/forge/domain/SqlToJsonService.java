package com.code.forge.domain;

import com.code.forge.application.common.SqlOperationType;
import com.code.forge.application.request.FormatRequest;
import com.code.forge.application.response.FormatResponse;
import com.code.forge.domain.interfaces.ISqlToJsonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class SqlToJsonService implements ISqlToJsonService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public FormatResponse convertSqlToJson(FormatRequest formatRequest) {
        String sqlQuery = formatRequest.getRequest();
        SqlOperationType sqlOperationType = formatRequest.getSqlOperationType();

        String jsonResponse;
        byte[] fileContent;

        jsonResponse = switch (sqlOperationType) {
            case SELECT -> convertSelectToJson(sqlQuery);
            case INSERT -> convertInsertToJson(sqlQuery);
            case UPDATE -> convertUpdateToJson(sqlQuery);
            case DELETE -> convertDeleteToJson(sqlQuery);
            case CREATE -> convertCreateToJson(sqlQuery);
            default -> throw new IllegalArgumentException("Unsupported SQL operation type: " + sqlOperationType);
        };

        fileContent = jsonResponse.getBytes(StandardCharsets.UTF_8);

        return new FormatResponse(jsonResponse, fileContent);
    }

    private String convertSelectToJson(String sqlQuery) {
        String cleanedQuery = sqlQuery.replaceAll("(?i)SELECT\\s+", "").replaceAll("(?i)FROM\\s+", "").trim();
        String columnsPart = cleanedQuery.split("FROM")[0].trim();

        String[] columns;
        if (columnsPart.equals("*")) {
            columns = new String[] {};
        } else {
            columns = columnsPart.split(",");
        }

        StringBuilder jsonResponse = new StringBuilder("[\n");
        for (int i = 0; i < 2; i++) {
            jsonResponse.append("  { ");
            for (int j = 0; j < columns.length; j++) {
                String column = columns[j].trim();
                jsonResponse.append("\"").append(column).append("\": \"\"");
                if (j < columns.length - 1) {
                    jsonResponse.append(", ");
                }
            }
            jsonResponse.append(" }");
            if (i < 1) {
                jsonResponse.append(",\n");
            }
        }

        jsonResponse.append("\n]");
        return jsonResponse.toString();
    }

    private String convertInsertToJson(String sqlQuery) {
        String cleanedQuery = sqlQuery.replaceAll("(?i)INSERT INTO\\s+", "").replaceAll("(?i)VALUES\\s+", "").trim();

        String columnsPart = cleanedQuery.split("\\(")[1].split("\\)")[0].trim();
        String[] columns = columnsPart.split(",");

        StringBuilder jsonResponse = new StringBuilder("[\n");

        jsonResponse.append("  {\n");

        for (int i = 0; i < columns.length; i++) {
            String columnName = columns[i].trim();

            jsonResponse.append("    \"").append(columnName).append("\": \"\"");

            if (i < columns.length - 1) {
                jsonResponse.append(",\n");
            }
        }

        jsonResponse.append("\n  }\n]");
        return jsonResponse.toString();
    }

    private String convertUpdateToJson(String sqlQuery) {
        String cleanedQuery = sqlQuery.replaceAll("(?i)UPDATE\\s+", "").replaceAll("(?i)SET\\s+", "").trim();

        String setPart = cleanedQuery.split("SET")[1].split("WHERE")[0].trim();

        String[] setColumns = setPart.split(",");

        StringBuilder jsonResponse = new StringBuilder("[\n");
        jsonResponse.append("  {\n");

        for (int i = 0; i < setColumns.length; i++) {
            String columnDefinition = setColumns[i].trim();
            String columnName = columnDefinition.split("=")[0].trim();

            jsonResponse.append("    \"").append(columnName).append("\": \"\"");

            if (i < setColumns.length - 1) {
                jsonResponse.append(",\n");
            }
        }

        jsonResponse.append("\n  }\n]");
        return jsonResponse.toString();
    }

    private String convertDeleteToJson(String sqlQuery) {
        String cleanedQuery = sqlQuery.replaceAll("(?i)DELETE FROM\\s+", "").trim();

        int whereIndex = cleanedQuery.toUpperCase().indexOf("WHERE");
        if (whereIndex == -1) {
            throw new IllegalArgumentException("La consulta DELETE no contiene una cláusula WHERE");
        }

        String whereCondition = cleanedQuery.substring(whereIndex + 5).trim();

        String columnName = whereCondition.split("=")[0].trim();

        return  "[\n" + "  {\n" +
                "    \"" + columnName + "\": \"\"" +
                "\n  }\n]";
    }

    private String convertCreateToJson(String sqlQuery) {
        String[] columns = getColumns(sqlQuery);

        StringBuilder jsonResponse = new StringBuilder("{\n");

        for (int i = 0; i < columns.length; i++) {
            String columnDefinition = columns[i].trim();

            String[] parts = columnDefinition.split("\\s+");
            if (parts.length > 0) {
                String columnName = parts[0];

                jsonResponse.append("  \"").append(columnName).append("\": \"\"");

                if (i < columns.length - 1) {
                    jsonResponse.append(",\n");
                }
            }
        }

        jsonResponse.append("\n}");
        return jsonResponse.toString();
    }

    private static String[] getColumns(String sqlQuery) {
        String cleanedQuery = sqlQuery.replaceAll("(?i)CREATE TABLE\\s+", "").replaceAll("(?i)IF EXISTS\\s+", "").trim();

        int startIndex = cleanedQuery.indexOf("(");
        int endIndex = cleanedQuery.lastIndexOf(")");

        if (startIndex == -1 || endIndex == -1) {
            throw new IllegalArgumentException("Formato de consulta CREATE inválido");
        }

        String columnsPart = cleanedQuery.substring(startIndex + 1, endIndex).trim();
        return columnsPart.split(",");
    }


}
