package com.code.forge.application.controller.api;

import com.code.forge.application.response.SqlToJpaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.code.forge.application.utils.ApiDocumentation.*;
import static com.code.forge.application.utils.Constants.*;


@Tag(name = FORMAT_TAG, description = FORMAT_TAG_DESCRIPTION)
@RequestMapping(API_BASE + FORMAT_URL)
public interface IFormatController {


    @PostMapping("/jpa-to-sql")
    @Operation(summary = "Convierte un modelo JPA a SQL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversión exitosa",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Entrada no válida",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = {@Content(mediaType = "application/json")})
    })
    ResponseEntity<String> convertJpaToSql(@RequestBody String request);

    @PostMapping("/sql-to-jpa")
    @Operation(summary = "Convierte una consulta SQL en un modelo JPA y devuelve el resultado como texto y archivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversión exitosa",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SqlToJpaResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Entrada no válida",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = {@Content(mediaType = "application/json")})
    })
    ResponseEntity<SqlToJpaResponse> convertSqlToJpa(@RequestBody String sqlQuery) throws IOException;

}
