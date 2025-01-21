package org.example.project3.Repository;

import org.example.project3.Entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Department findByDeptno(int deptno);
}
