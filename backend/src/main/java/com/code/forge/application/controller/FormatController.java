package com.code.forge.application.controller;

import com.code.forge.application.controller.api.IFormatController;
import com.code.forge.application.request.FormatRequest;
import com.code.forge.application.response.FormatResponse;
import com.code.forge.domain.interfaces.IJpaToJsonService;
import com.code.forge.domain.interfaces.IJpaToSqlService;
import com.code.forge.domain.interfaces.ISqlToJpaService;
import com.code.forge.domain.interfaces.ISqlToJsonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FormatController implements IFormatController {

    private final ISqlToJpaService iSqlToJpaService;

    private final ISqlToJsonService iSqlToJsonService;

    private final IJpaToSqlService iJpaToSqlService;

    private final IJpaToJsonService iJpaToJsonService;

    public FormatController(ISqlToJpaService iSqlToJpaService, IJpaToSqlService iJpaToSqlService, ISqlToJsonService iSqlToJsonService, IJpaToJsonService iJpaToJsonService) {
        this.iSqlToJpaService = iSqlToJpaService;
        this.iJpaToSqlService = iJpaToSqlService;
        this.iSqlToJsonService = iSqlToJsonService;
        this.iJpaToJsonService = iJpaToJsonService;
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

    @Override
    public ResponseEntity<FormatResponse> convertJpaToJson(FormatRequest request) {
        return ResponseEntity.ok(iJpaToJsonService.convertJpaToSql(request));
    }

}
