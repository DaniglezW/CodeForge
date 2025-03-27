package com.code.forge.application.utils;

public class Utils {

    private Utils() {}

    public static String cleanSqlQuery(String sql) {
        return sql.replace("\\n", " ");
    }

}
