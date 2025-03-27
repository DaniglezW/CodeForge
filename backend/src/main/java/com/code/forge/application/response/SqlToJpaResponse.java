package com.code.forge.application.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlToJpaResponse {

    private String jpaModel;
    private byte[] fileContent;

}