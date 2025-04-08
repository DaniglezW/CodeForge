package com.code.forge.application.common;

public class Utils {

    private Utils() {}

    public static String cleanSqlQuery(String sql) {
        return sql.replace("\\n", " ");
    }

    public static String cleanAllOptionsSqlQuery(String request) {
        return request.replace("\r\n", " ").replace("\n", " ");
    }

}
