package org.example.project3.Service;

import lombok.Data;
import org.example.project3.Entity.Expense;

import java.util.HashMap;
import java.util.Map;

@Data
public class ExpenseSummary {
    private double totalAmount = 0.0;
    private Map<String, Double> expensesByType = new HashMap<>();
    private int totalTransactions = 0;

    public void addExpense(Expense expense) {
        totalAmount += expense.getPayment();
        totalTransactions++;

        expensesByType.merge(expense.getType(), expense.getPayment(), Double::sum);
    }
}
