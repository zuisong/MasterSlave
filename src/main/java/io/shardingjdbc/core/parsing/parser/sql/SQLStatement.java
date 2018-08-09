package io.shardingjdbc.core.parsing.parser.sql;

import io.shardingjdbc.core.constant.SQLType;

public class SQLStatement {

    public SQLType sqlType;

    public SQLStatement(SQLType sqlType) {
        this.sqlType = sqlType;
    }
}
