package com.code.forge.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldInfo {

    private String columnName;
    private String dataType;
    private String length;
    private String constraint;

}

