package com.employee.employee_management;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // 1. Read all employees
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // 2. Create an employee



@PostMapping
public EmployeeDTO addEmployee(@Valid @RequestBody EmployeeDTO dto) {

    if (employeeRepository.existsByEmail(dto.getEmail())) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Email already exists"
        );
    }

    Employee employee = new Employee();
    employee.setName(dto.getName());
    employee.setEmail(dto.getEmail());
    employee.setDepartment(dto.getDepartment());
    employee.setSalary(dto.getSalary());

    Employee savedEmployee = employeeRepository.save(employee);

    EmployeeDTO response = new EmployeeDTO();
    response.setName(savedEmployee.getName());
    response.setEmail(savedEmployee.getEmail());
    response.setDepartment(savedEmployee.getDepartment());
    response.setSalary(savedEmployee.getSalary());

    return response;
}




    // 3. Read one employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(
            @PathVariable Long id) {

        return employeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 4. Update an employee
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @PathVariable Long id,
            @RequestBody Employee updatedEmployee) {

        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(updatedEmployee.getName());
                    employee.setEmail(updatedEmployee.getEmail());
                    employee.setDepartment(updatedEmployee.getDepartment());
                    employee.setSalary(updatedEmployee.getSalary());

                    Employee savedEmployee =
                            employeeRepository.save(employee);

                    return ResponseEntity.ok(savedEmployee);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 5. Delete an employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id) {

        if (!employeeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        employeeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
