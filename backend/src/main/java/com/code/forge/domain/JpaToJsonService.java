package com.code.forge.domain;

import com.code.forge.application.request.FormatRequest;
import com.code.forge.application.response.FormatResponse;
import com.code.forge.domain.interfaces.IJpaToJsonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class JpaToJsonService implements IJpaToJsonService {


    @Override
    public FormatResponse convertJpaToSql(FormatRequest request) {
        String jpaSoruce = request.getRequest();
        String jsonResponse = convertJpaToSelect(jpaSoruce);

        byte[] fileContent;
        fileContent = jsonResponse.getBytes(StandardCharsets.UTF_8);

        return new FormatResponse(jsonResponse, fileContent);
    }

    private String convertJpaToSelect(String jpaSource)  {
        String tableName = extractTableName(jpaSource);
        List<String> fieldNames = extractFieldNames(jpaSource);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode tableNode = mapper.createObjectNode();

        for (String field : fieldNames) {
            tableNode.put(field, "");
        }

        ObjectNode root = mapper.createObjectNode();
        root.set(tableName, tableNode);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al generar JSON", e);
        }
    }

    private String extractTableName(String jpaSource) {
        Pattern tablePattern = Pattern.compile("@Table\\(name\\s*=\\s*\"(\\w+)\"\\)");
        Matcher matcher = tablePattern.matcher(jpaSource);
        if (matcher.find()) {
            return matcher.group(1);
        }
        Pattern classPattern = Pattern.compile("class\\s+(\\w+)");
        Matcher classMatcher = classPattern.matcher(jpaSource);
        if (classMatcher.find()) {
            return classMatcher.group(1).toLowerCase();
        }
        throw new IllegalArgumentException("No se pudo determinar el nombre de la tabla.");
    }

    private List<String> extractFieldNames(String jpaSource) {
        List<String> fieldNames = new ArrayList<>();
        Pattern fieldPattern = Pattern.compile("private\\s+\\w+\\s+(\\w+);");
        Matcher matcher = fieldPattern.matcher(jpaSource);

        while (matcher.find()) {
            fieldNames.add(matcher.group(1));
        }

        return fieldNames;
    }

}
