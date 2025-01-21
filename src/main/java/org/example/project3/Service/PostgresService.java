package org.example.project3.Service;

import java.sql.*;

public class PostgresService {

    private Connection connection;

    public PostgresService(String url, String user, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public void getEmployeeData() throws SQLException {
        String query = "SELECT * FROM emp";  // PostgreSQL emp tablosundaki verileri çekme
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int empno = resultSet.getInt("empno");
                String ename = resultSet.getString("ename");
                // Veriyi kullanarak harcama hesaplamalarını yapabilirsiniz
                System.out.println("Employee No: " + empno + ", Name: " + ename);
            }
        }
    }

    public void close() throws SQLException {
        connection.close();
    }
}
