package org.example.project3.Service;

import java.sql.*;



public class ExpenseCalculationService {

    private PostgresService postgresService;

    public ExpenseCalculationService(PostgresService postgresService) {
        this.postgresService = postgresService;
    }

    // Harcama hesaplama işlemi
    public void calculateTotalExpense(int empno, double payment) {
        System.out.println("Calculating total expense for employee: " + empno);
        // Burada empno'yu kullanarak harcama hesaplaması yapabilirsiniz
        // Örneğin, veritabanında çalışan harcamalarını güncelleyebilirsiniz
    }

    // Harcama verilerini işleme
    public void processExpenses() {
        try {
            postgresService.getEmployeeData();
            // Cassandra'dan gelen her harcama kaydını burada işleyebilirsiniz
            // Her bir harcama için calculateTotalExpense çağırarak hesaplama yapabilirsiniz
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
