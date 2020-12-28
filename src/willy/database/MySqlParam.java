/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package willy.database;

import java.sql.SQLType;

/**
 *
 * @author Willy
 */
public class MySqlParam {

    private final SQLType type;
    private final Object value;

    public MySqlParam(SQLType type, Object value) {
        this.type = type;
        this.value = value;
    }

    Object getValue() {
        return value;
    }

    SQLType getType() {
        return type;
    }

}
