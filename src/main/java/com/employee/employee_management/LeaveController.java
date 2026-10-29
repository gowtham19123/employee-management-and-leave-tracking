
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

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveController(
            LeaveRepository leaveRepository,
            EmployeeRepository employeeRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
    }

    // Apply for leave
   
    @PostMapping
    public Leave applyLeave(@RequestBody Leave leave) {

        // Check whether employee exists
        if (leave.getEmployeeId() == null ||
                !employeeRepository.existsById(leave.getEmployeeId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Employee not found");
        }

        // Check whether dates are provided
        if (leave.getStartDate() == null ||
                leave.getEndDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Leave dates are required");
        }

        // Check whether dates are valid
        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End date cannot be before start date");
        }

        // Check for overlapping pending or approved leaves
        List<String> statuses = List.of("PENDING", "APPROVED");

        List<Leave> overlappingLeaves =
                leaveRepository
                .findByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        leave.getEmployeeId(),
                        statuses,
                        leave.getEndDate(),
                        leave.getStartDate()
                );

        if (!overlappingLeaves.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Leave dates overlap with an existing pending or approved leave");
        }

        // Save the new leave request
        leave.setId(null);
        leave.setStatus("PENDING");

        return leaveRepository.save(leave);
    }
    
    // Get all leave requests
    @GetMapping
    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    // Get one leave request by ID
    @GetMapping("/{id}")
    public Leave getLeaveById(@PathVariable Long id) {

        return leaveRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Leave request not found"));
    }
 
    @PutMapping("/{id}/status")
    public Leave updateLeaveStatus(
            @PathVariable Long id,
            @RequestBody Leave leave) {

        Leave existingLeave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Leave request not found"));

        String status = leave.getStatus();

        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status must be APPROVED or REJECTED");
        }

        existingLeave.setStatus(status);

        return leaveRepository.save(existingLeave);
    }
    
    //delete
    
  
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeave(@PathVariable Long id) {

        if (!leaveRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Leave request not found");
        }

        leaveRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
    
   

}
