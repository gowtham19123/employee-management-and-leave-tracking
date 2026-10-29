
package com.employee.employee_management;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

	List<Leave> findByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
	        Long employeeId,
	        List<String> statuses,
	        LocalDate endDate,
	        LocalDate startDate
	);
}
