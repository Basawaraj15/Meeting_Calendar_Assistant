package com.example.Calendar.Service;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import com.example.Calendar.Repository.MeetingRepository;
import com.example.Calendar.Utility.Employee;
import com.example.Calendar.Utility.Meeting;


@SpringBootTest
public class MeetingserviceTest {



	    @Autowired
	    private MeetingService meetingService;

	    @MockBean
	    private MeetingRepository meetingRepository;

	    @Test
	    public void testFindFreeSlots() {
	        
	        Employee emp1 = new Employee(1L, "John Doe");
	        Employee emp2 = new Employee(2L, "Jane Smith");

	       
	        Employee owner = new Employee(3L, "Owner Name");

	        // Mock meetings for emp1
	        List<Meeting> meetings1 = Arrays.asList(
	            new Meeting("Meeting1", LocalDateTime.of(2024, 10, 20, 10, 0), 
	                LocalDateTime.of(2024, 10, 20, 11, 0), Arrays.asList(emp1), owner),
	            new Meeting("Meeting2", LocalDateTime.of(2024, 10, 20, 13, 0), 
	                LocalDateTime.of(2024, 10, 20, 14, 0), Arrays.asList(emp1), owner)
	        );

	        
	        Mockito.when(meetingRepository.findByOwner(owner)).thenReturn(meetings1);

	       
	        LocalDateTime startTime = LocalDateTime.of(2024, 10, 20, 9, 0);
	        LocalDateTime endTime = LocalDateTime.of(2024, 10, 20, 15, 0);

	        
	        ResponseEntity<?> response = meetingService.findFreeSlots(owner.getId(), startTime, endTime);
	        List<LocalDateTime> freeSlots = (List<LocalDateTime>) response.getBody();

	        
	        Assertions.assertEquals(2, freeSlots.size());

	        
	        Assertions.assertEquals(LocalDateTime.of(2024, 10, 20, 9, 0), freeSlots.get(0));
	        
	        
	        Assertions.assertEquals(LocalDateTime.of(2024, 10, 20, 11, 0), freeSlots.get(1));
	    }

	}
