package org.example.project3.Repository;

import org.example.project3.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findByEmpnoIn(List<Integer> empnos);

    @Query("SELECT MAX(e.empno) FROM Employee e")
    Integer findMaxEmpno();

    Employee findByEmpno(Integer empno);

    @Query("SELECT e.ename FROM Employee e WHERE e.empno = :mgr")
    String findManagerNameByEmpno(@Param("mgr") Integer mgr);
}
