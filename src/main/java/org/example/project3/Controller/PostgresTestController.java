package org.example.project3.Controller;

import lombok.extern.slf4j.Slf4j;
import org.example.project3.Entity.Department;
import org.example.project3.Entity.Employee;
import org.example.project3.Repository.DepartmentRepository;
import org.example.project3.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@Slf4j
public class PostgresTestController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/postgres/employees")
    public ResponseEntity<?> testEmployees() {
        try {
            List<Employee> employees = employeeRepository.findAll();
            log.info("Found {} employees", employees.size());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count", employees.size(),
                    "employees", employees
            ));
        } catch (Exception e) {
            log.error("Error fetching employees: ", e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/postgres/departments")
    public ResponseEntity<?> testDepartments() {
        try {
            List<Department> departments = departmentRepository.findAll();
            log.info("Found {} departments", departments.size());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "count", departments.size(),
                    "departments", departments
            ));
        } catch (Exception e) {
            log.error("Error fetching departments: ", e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/postgres/connection")
    public ResponseEntity<?> testConnection() {
        try {
            // Test basic query
            long employeeCount = employeeRepository.count();
            long departmentCount = departmentRepository.count();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "employeeCount", employeeCount,
                    "departmentCount", departmentCount,
                    "message", "PostgreSQL connection is working"
            ));
        } catch (Exception e) {
            log.error("Database connection error: ", e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", "Database connection failed: " + e.getMessage()
            ));
        }
    }
}