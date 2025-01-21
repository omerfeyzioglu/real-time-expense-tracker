package org.example.project3.Service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import lombok.extern.slf4j.Slf4j;
import org.example.project3.Entity.Expense;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CassandraService {
    private CqlSession session;

    public CassandraService() {
        this.session = CqlSession.builder()
                .withKeyspace(CqlIdentifier.fromCql("your_keyspace"))
                .addContactPoint(new InetSocketAddress("localhost", 9042))
                .withLocalDatacenter("datacenter1")
                .build();
    }
    public void deleteExpensesByEmpno(Integer empno) {
        String query = "DELETE FROM expenses WHERE empno = ?";

        try {
            // Convert Integer to String since empno is stored as string in Cassandra
            SimpleStatement statement = SimpleStatement.builder(query)
                    .addPositionalValue(empno.toString())
                    .build();

            ResultSet resultSet = session.execute(statement);
            log.info("Deleted expenses for employee {}", empno);

        } catch (Exception e) {
            log.error("Error deleting expenses for employee {}: {}", empno, e.getMessage(), e);
            throw new RuntimeException("Failed to delete expenses for employee " + empno, e);
        }
    }
    public List<Expense> getExpensesByEmpno(List<Integer> empnos) {
        List<Expense> expenses = new ArrayList<>();

        StringBuilder empnoList = new StringBuilder();
        for (Integer empno : empnos) {
            if (empnoList.length() > 0) {
                empnoList.append(",");
            }
            empnoList.append("'").append(empno).append("'");
        }

        String query = "SELECT * FROM expenses WHERE empno IN (" + empnoList.toString() +
                ") AND date_time >= '2024-12-08 17:00:00' AND date_time <= '2024-12-13 23:00:00' ALLOW FILTERING";

        try {
            ResultSet resultSet = session.execute(query);

            for (Row row : resultSet) {
                Expense expense = new Expense();
                expense.setEmpno(row.getString("empno"));
                expense.setDateTime(row.getString("date_time"));
                expense.setDescription(row.getString("description"));
                expense.setPayment(row.getDouble("payment"));
                expense.setType(row.getString("type"));
                expense.setCount(row.getInt("count"));
                expenses.add(expense);
            }
            log.info("Found {} expenses", expenses.size());

        } catch (Exception e) {
            log.error("Error executing query: {}", e.getMessage(), e);
        }

        return expenses;
    }

    // Yeni metod: Toplam harcamaları hesapla
    public Map<String, ExpenseSummary> getExpenseSummaryByEmpno(List<Integer> empnos) {
        List<Expense> expenses = getExpensesByEmpno(empnos);
        Map<String, ExpenseSummary> summaryMap = new HashMap<>();

        // Her çalışan için özet hesapla
        for (Expense expense : expenses) {
            String empno = expense.getEmpno();
            summaryMap.computeIfAbsent(empno, k -> new ExpenseSummary())
                    .addExpense(expense);
        }

        return summaryMap;
    }

    public void close() {
        session.close();
    }
}

