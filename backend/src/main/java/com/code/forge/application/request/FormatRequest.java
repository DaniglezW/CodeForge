package com.code.forge.application.request;

import com.code.forge.application.common.SqlOperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormatRequest {

    private String request;
    private SqlOperationType sqlOperationType;

}
