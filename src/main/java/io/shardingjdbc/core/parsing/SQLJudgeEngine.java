/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingjdbc.core.parsing;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;
import io.shardingjdbc.core.constant.SQLType;
import lombok.RequiredArgsConstructor;

/**
 * SQL judge engine.
 *
 * @author zhangliang
 */
@RequiredArgsConstructor
public final class SQLJudgeEngine {

    private final String sql;

    public SQLType judge() {
        Lexer lexer = new Lexer(sql);
        lexer.nextToken();
        while (true) {
            Token token = lexer.token();
            if (Token.SELECT == token) {
                return SQLType.DQL;
            }
            if (Token.INSERT == token || Token.UPDATE == token || Token.DELETE == token) {
                return SQLType.DML;
            }
            if (Token.CREATE == token || Token.ALTER == token || Token.DROP == token || Token.TRUNCATE == token) {
                return SQLType.DDL;
            }
            if (lexer.isEOF()) {
                throw new SQLParsingException("sql error -> " + sql);
            }
            lexer.nextToken();
        }
    }


}

