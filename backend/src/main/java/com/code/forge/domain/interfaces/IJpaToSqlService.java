package com.code.forge.domain.interfaces;

import com.code.forge.application.common.SqlOperationType;
import com.code.forge.application.response.FormatResponse;

public interface IJpaToSqlService {

    FormatResponse convertJpaToSql(String jpaModel, SqlOperationType sqlOperationType);

}
