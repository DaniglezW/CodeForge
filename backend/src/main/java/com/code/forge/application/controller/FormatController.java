package com.code.forge.application.controller;

import com.code.forge.application.controller.api.IFormatController;
import com.code.forge.application.response.SqlToJpaResponse;
import com.code.forge.domain.interfaces.IFormatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FormatController implements IFormatController {

    private final IFormatService ijpaToSqlService;

    public FormatController(IFormatService ijpaToSqlService) {
        this.ijpaToSqlService = ijpaToSqlService;
    }

    @Override
    public ResponseEntity<String> convertJpaToSql(String request) {
        return ResponseEntity.ok(ijpaToSqlService.convertJpaToSql(request));
    }

    @Override
    public ResponseEntity<SqlToJpaResponse> convertSqlToJpa(String sqlQuery) {
        return ResponseEntity.ok(ijpaToSqlService.convertSqlToJpa(sqlQuery));
    }

}
