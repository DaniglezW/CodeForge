package com.code.forge.domain.interfaces;

import com.code.forge.application.response.SqlToJpaResponse;

public interface IFormatService {

    String convertJpaToSql(String jpaModel);

    SqlToJpaResponse convertSqlToJpa(String sqlQuery);

}
