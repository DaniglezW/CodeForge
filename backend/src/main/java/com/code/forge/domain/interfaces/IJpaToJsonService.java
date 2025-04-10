package com.code.forge.domain.interfaces;

import com.code.forge.application.request.FormatRequest;
import com.code.forge.application.response.FormatResponse;

public interface IJpaToJsonService {

    FormatResponse convertJpaToSql(FormatRequest request);

}
