package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MySQL{
    
    private static Connection connection;
    
    static {
        try {
           Class.forName("com.mysql.cj.jdbc.Driver");
           connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nextgen", "root", "Pasindu328@Bhathiya");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static ResultSet execute(String query) throws Exception {
        if (query.startsWith("SELECT")) {
            return connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(query);
        }else{
            connection.createStatement().executeUpdate(query);
            return null;
        }
    }
    
}