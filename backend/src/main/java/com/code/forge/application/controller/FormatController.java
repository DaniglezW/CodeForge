package com.code.forge.application.controller;

import com.code.forge.application.controller.api.IFormatController;
import com.code.forge.application.request.FormatRequest;
import com.code.forge.application.response.FormatResponse;
import com.code.forge.domain.interfaces.IJpaToSqlService;
import com.code.forge.domain.interfaces.ISqlToJpaService;
import com.code.forge.domain.interfaces.ISqlToJsonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
public class FormatController implements IFormatController {

    private final ISqlToJpaService iSqlToJpaService;

    private final IJpaToSqlService iJpaToSqlService;

    private final ISqlToJsonService iSqlToJsonService;

    public FormatController(ISqlToJpaService iSqlToJpaService, IJpaToSqlService iJpaToSqlService, ISqlToJsonService iSqlToJsonService) {
        this.iSqlToJpaService = iSqlToJpaService;
        this.iJpaToSqlService = iJpaToSqlService;
        this.iSqlToJsonService = iSqlToJsonService;
    }

    @Override
    public ResponseEntity<FormatResponse> convertJpaToSql(FormatRequest request) {
        return ResponseEntity.ok(iJpaToSqlService.convertJpaToSql(request.getRequest(), request.getSqlOperationType()));
    }

    @Override
    public ResponseEntity<FormatResponse> convertSqlToJpa(String sqlQuery) {
        return ResponseEntity.ok(iSqlToJpaService.convertSqlToJpa(sqlQuery));
    }

    @Override
    public ResponseEntity<FormatResponse> convertSqlToJson(FormatRequest request) {
        return ResponseEntity.ok(iSqlToJsonService.convertSqlToJson(request));
    }

}
