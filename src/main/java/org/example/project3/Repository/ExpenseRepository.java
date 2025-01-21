package org.example.project3.Repository;

import org.example.project3.Entity.Expense;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends CassandraRepository<Expense, String> {
    List<Expense> findByEmpnoIn(List<String> empnos);
}
