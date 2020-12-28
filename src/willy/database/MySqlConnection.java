/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package willy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Willy
 */
public class MySqlConnection {

    public static final String LOCALHOST = "jdbc:mysql://localhost:3306/";
    private final Connection con;

    public MySqlConnection(final String URL, final String username, final String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        this.con = DriverManager.getConnection(URL, username, password);
    }

    public void disconnect() throws SQLException {
        con.close();
    }
    
    public ResultSet executeQuery(final String query) throws SQLException {
        PreparedStatement p = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return p.executeQuery();
    }

    public boolean executeUpdate(final String query, final MySqlParam... params) throws SQLException {
        PreparedStatement p = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        int i = 0;
        for (MySqlParam param : params) {
            i++;
            p.setObject(i, param.getValue(), param.getType());
        }
        return p.execute();
    }

    public ResultSet executeQuery(final String query, final MySqlParam... params) throws SQLException {
        PreparedStatement p = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        int i = 0;
        for (MySqlParam param : params) {
            i++;
            p.setObject(i, param.getValue(), param.getType());
        }
        return p.executeQuery();
    }

}
