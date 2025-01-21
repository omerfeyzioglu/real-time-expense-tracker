package org.example.project3.Controller;

import lombok.extern.slf4j.Slf4j;
import org.example.project3.Entity.Department;
import org.example.project3.Entity.Employee;
import org.example.project3.Entity.Expense;
import org.example.project3.Service.ExpenseSummary;
import org.example.project3.Repository.DepartmentRepository;
import org.example.project3.Repository.EmployeeRepository;
import org.example.project3.Service.CassandraService;
import org.example.project3.Service.DataGeneratorService;
import org.example.project3.Service.HdfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository; // Department repository

    @Autowired
    private CassandraService cassandraService;

    @Autowired
    private HdfsService hdfsService;

    @Autowired
    private DataGeneratorService dataGeneratorService;

    @PostMapping("/employee/update")
    public String updateEmployee(@RequestParam Integer empno,
                                 @RequestParam String ename,
                                 @RequestParam String job,
                                 @RequestParam Integer deptno,
                                 @RequestParam Double sal,
                                 @RequestParam Double comm,
                                 @RequestParam(required = false) MultipartFile imageFile,
                                 Model model) {
        try {
            // Find existing employee
            Employee employee = employeeRepository.findByEmpno(empno);
            if (employee == null) {
                return "redirect:/employee?message=Employee not found";
            }

            // Update employee details
            employee.setEname(ename);
            employee.setJob(job);
            employee.setDeptno(deptno);
            employee.setSal(sal);
            employee.setComm(comm);

            // Handle new image upload if provided
            if (imageFile != null && !imageFile.isEmpty()) {
                // Delete old image if exists
                if (employee.getImg() != null && !employee.getImg().isEmpty()) {
                    try {
                        hdfsService.deleteImage(employee.getImg());
                    } catch (Exception e) {
                        log.error("Error deleting old image for employee {}: {}", empno, e.getMessage());
                    }
                }

                // Upload new image
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                hdfsService.uploadImage(fileName, imageFile.getInputStream());
                employee.setImg(fileName);
            }

            // Validate department
            Department dept = departmentRepository.findByDeptno(deptno);
            if (dept == null) {
                return "redirect:/employee?message=Invalid department number";
            }

            // Save updated employee
            employeeRepository.save(employee);

            return "redirect:/employee?message=Employee updated successfully";
        } catch (Exception e) {
            log.error("Error updating employee {}: {}", empno, e.getMessage());
            return "redirect:/employee?message=Error updating employee: " + e.getMessage();
        }
    }
    @PostMapping("/employee/delete")
    public String deleteEmployee(@RequestParam Integer empno, Model model) {
        try {
            // Check if employee exists
            Employee employee = employeeRepository.findByEmpno(empno);
            if (employee == null) {
                return "redirect:/employee?message=Employee not found";
            }

            // Delete employee image from HDFS if exists
            if (employee.getImg() != null && !employee.getImg().isEmpty()) {
                try {
                    hdfsService.deleteImage(employee.getImg());
                } catch (Exception e) {
                    log.error("Error deleting image for employee {}: {}", empno, e.getMessage());
                    // Continue with deletion even if image deletion fails
                }
            }


            // Delete expenses from Cassandra
            try {
                cassandraService.deleteExpensesByEmpno(empno);
            } catch (Exception e) {
                log.error("Error deleting expenses for employee {}: {}", empno, e.getMessage());
                return "redirect:/employee?message=Error deleting employee expenses: " + e.getMessage();
            }

            // Delete employee from database
            employeeRepository.delete(employee);

            return "redirect:/employee?message=Employee and related data deleted successfully";
        } catch (Exception e) {
            log.error("Error deleting employee {}: {}", empno, e.getMessage());
            return "redirect:/employee?message=Error deleting employee: " + e.getMessage();
        }
    }

    @GetMapping("/employee")
    public String getEmployeeData(@RequestParam(required = false) List<Integer> empnos, Model model) {
        try {
            List<Employee> employees;
            List<Expense> allExpenses;
            Map<String, ExpenseSummary> expenseSummaries;

            if (empnos != null && !empnos.isEmpty()) {
                log.info("Fetching specific employees: {}", empnos);
                employees = employeeRepository.findByEmpnoIn(empnos);
                if (employees.isEmpty()) {
                    log.warn("No employees found for IDs: {}", empnos);
                    return "redirect:/employee?message=No employees found";
                }
            } else {
                log.info("Fetching all employees");
                employees = employeeRepository.findAll();
                if (employees.isEmpty()) {
                    log.warn("No employees found in database");
                    model.addAttribute("message", "No employees found");
                    return "employee";
                }
            }

            try {
                List<Integer> allEmpnos = employees.stream()
                        .map(Employee::getEmpno)
                        .collect(Collectors.toList());

                log.info("Fetching expenses for {} employees", allEmpnos.size());
                allExpenses = cassandraService.getExpensesByEmpno(allEmpnos);
                expenseSummaries = cassandraService.getExpenseSummaryByEmpno(allEmpnos);
            } catch (Exception e) {
                log.error("Error fetching expense data: {}", e.getMessage(), e);
                model.addAttribute("error", "Unable to fetch expense data. System will display employee information only.");
                model.addAttribute("employees", employees);
                return "employee";
            }

            Map<Integer, List<Expense>> expensesByEmployee = allExpenses.stream()
                    .collect(Collectors.groupingBy(expense -> Integer.parseInt(expense.getEmpno())));

            Map<Integer, String> managerNames = new HashMap<>();
            for (Employee employee : employees) {
                try {
                    String managerName = "No Manager";
                    if (employee.getMgr() != null) {
                        managerName = employeeRepository.findManagerNameByEmpno(employee.getMgr());
                        if (managerName == null) {
                            managerName = "Unknown Manager";
                        }
                    }
                    managerNames.put(employee.getEmpno(), managerName);
                } catch (Exception e) {
                    log.error("Error fetching manager name for employee {}: {}", employee.getEmpno(), e.getMessage());
                    managerNames.put(employee.getEmpno(), "Error fetching manager");
                }
            }

            model.addAttribute("managerNames", managerNames);
            model.addAttribute("employees", employees);
            model.addAttribute("expensesByEmployee", expensesByEmployee);
            model.addAttribute("expenseSummaries", expenseSummaries);

            return "employee";
        } catch (Exception e) {
            log.error("Unexpected error in getEmployeeData: {}", e.getMessage(), e);
            model.addAttribute("error", "An unexpected error occurred. Please try again later.");
            return "error";
        }
    }


    @PostMapping("/employee/add")
    public String addEmployee(@ModelAttribute Employee employee,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Model model) {
        try {
            // Validate department first
            Integer deptno = employee.getDeptno();
            if (deptno == null) {
                model.addAttribute("error", "Department number is required");
                return "error";
            }

            Department dept = departmentRepository.findByDeptno(deptno);
            if (dept == null) {
                model.addAttribute("error", "Invalid department number");
                return "error";
            }

            // Generate next employee number
            Integer maxEmpno = employeeRepository.findMaxEmpno();
            employee.setEmpno(maxEmpno == null ? 7000 : maxEmpno + 1);

            // Handle image upload
            if (!imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                hdfsService.uploadImage(fileName, imageFile.getInputStream());
                employee.setImg(fileName);
            }

            // Set defaults and validate
            if (employee.getComm() == null) {
                employee.setComm(0.0);
            }

            // Basic validation
            if (employee.getEname() == null || employee.getJob() == null || employee.getSal() == null) {
                model.addAttribute("error", "Missing required fields");
                return "error";
            }

            // Save employee
            Employee savedEmployee = employeeRepository.save(employee);

            // Generate initial expense data for new employee
            dataGeneratorService.generateAndSendDataForEmployee(savedEmployee.getEmpno());

            return "redirect:/employee";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/employee/image")
    public ResponseEntity<InputStreamResource> getEmployeeImage(@RequestParam String imageName) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        hdfsService.downloadImage(imageName, outputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray())));
    }

}
