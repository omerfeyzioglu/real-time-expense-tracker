package org.example.project3.Controller;

import org.example.project3.Repository.DepartmentRepository;
import org.example.project3.Repository.EmployeeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/test")
    public String test() {
        return "Test successful!";
    }

    @GetMapping("/db/employees")
    public ResponseEntity<?> testEmployees() {
        try {
            var employees = employeeRepository.findAll();
            log.info("Found {} employees", employees.size());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", employees.size());
            response.put("employees", employees);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching employees: ", e);

            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());

            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/db/departments")
    public ResponseEntity<?> testDepartments() {
        try {
            var departments = departmentRepository.findAll();
            log.info("Found {} departments", departments.size());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", departments.size());
            response.put("departments", departments);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching departments: ", e);

            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());

            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/db/connection")
    public ResponseEntity<?> testConnection() {
        try {
            long employeeCount = employeeRepository.count();
            long departmentCount = departmentRepository.count();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "PostgreSQL connection is working");
            response.put("employeeCount", employeeCount);
            response.put("departmentCount", departmentCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Database connection error: ", e);

            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Database connection failed: " + e.getMessage());

            return ResponseEntity.status(500).body(error);
        }
    }
}
