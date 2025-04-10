package com.code.forge.application.controller.api;

import com.code.forge.application.request.FormatRequest;
import com.code.forge.application.response.FormatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.code.forge.application.common.ApiDocumentation.*;
import static com.code.forge.application.common.Constants.*;


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
    ResponseEntity<FormatResponse> convertJpaToSql(@RequestBody FormatRequest request);

    @PostMapping("/sql-to-jpa")
    @Operation(summary = "Convierte una consulta SQL en un modelo JPA y devuelve el resultado como texto y archivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversión exitosa",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FormatResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Entrada no válida",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = {@Content(mediaType = "application/json")})
    })
    ResponseEntity<FormatResponse> convertSqlToJpa(@RequestBody String sqlQuery) throws IOException;

    @PostMapping("/sql-to-json")
    @Operation(summary = "Convierte una consulta SQL en un JSON y devuelve el resultado como texto y archivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversión exitosa",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FormatResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Entrada no válida",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = {@Content(mediaType = "application/json")})
    })
    ResponseEntity<FormatResponse> convertSqlToJson(@RequestBody FormatRequest request) throws IOException;

    @PostMapping("/jpa-to-json")
    @Operation(summary = "Convierte una Modelo JPA en un JSON y devuelve el resultado como texto y archivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversión exitosa",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FormatResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Entrada no válida",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = {@Content(mediaType = "application/json")})
    })
    ResponseEntity<FormatResponse> convertJpaToJson(@RequestBody FormatRequest request) throws IOException;

}
