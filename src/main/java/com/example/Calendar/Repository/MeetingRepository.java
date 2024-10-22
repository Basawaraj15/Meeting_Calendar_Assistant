package com.example.Calendar.Repository;



import com.example.Calendar.Utility.Meeting;
import com.example.Calendar.Utility.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
	List<Meeting> findAllByParticipantsContains(Employee participant);
	List<Meeting> findByOwner(Employee owner);
   
	

	
}


