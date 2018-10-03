package io.github.yuokada.samples.jpa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JpaMain {

    public static void main(String[] args) throws SQLException {
        // jdbc:h2:mem:default
        String dsn = "jdbc:h2:mem:default";
        System.out.println(dsn);

        Connection con = DriverManager.getConnection(dsn);
        PreparedStatement statement = con.prepareStatement("SHOW TABLES");
        statement.execute();
        ResultSet result = statement.executeQuery();
        System.out.println(result);

        con.close();
    }
}
