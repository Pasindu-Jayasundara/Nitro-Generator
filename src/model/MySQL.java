package model;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {
    private static Connection connection;

    private static void createConnection() throws Exception {
        if (connection == null) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nextgen", "root", "Pasindu328@Bhathiya");
        }
    }

    public static ResultSet executeSearch(String query, Object... params) throws Exception {
        createConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        setParameters(preparedStatement, params);
        return preparedStatement.executeQuery();
    }

    public static Integer executeIUD(String query, Object... params) throws Exception {
        createConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        setParameters(preparedStatement, params);
        preparedStatement.executeUpdate();

        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            int generatedId = generatedKeys.getInt(1);
            return generatedId;
        }

        return null;
    }

    private static void setParameters(PreparedStatement preparedStatement, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
        }
    }
}
