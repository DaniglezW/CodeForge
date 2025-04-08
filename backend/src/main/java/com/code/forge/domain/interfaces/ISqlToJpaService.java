package com.code.forge.domain.interfaces;

import com.code.forge.application.response.FormatResponse;

public interface ISqlToJpaService {

    FormatResponse convertSqlToJpa(String sqlQuery);

}
