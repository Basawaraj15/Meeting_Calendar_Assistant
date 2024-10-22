package com.example.Calendar.Controller;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import com.example.Calendar.Service.MeetingService;
import com.example.Calendar.Utility.Employee;
import com.example.Calendar.Utility.Meeting;
import com.example.Calendar.Exception.ConflictException;

@SpringBootTest
@AutoConfigureMockMvc
public class MeetingControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Mock
    private MeetingService meetingService;

    @InjectMocks
    private MeetingController meetingController;

    @BeforeEach
    public void setUp() {
        
    }

    @Test
    public void testBookMeetingConflict() throws Exception {
        // Create example employees
        Employee employee1 = new Employee(1L, "John Doe");
        Employee employee2 = new Employee(2L, "Jane Smith");
        Employee owner = new Employee(3L, "Owner Name"); // Owner for the meeting

        List<Employee> participants = Arrays.asList(employee1, employee2);

        
        Meeting meeting = new Meeting("Test Meeting", LocalDateTime.now(), LocalDateTime.now().plusHours(1), participants, owner);

        
        when(meetingService.bookMeeting(anyString(), any(), any(), anyList(), any(Employee.class)))
            .thenThrow(new ConflictException("Meeting conflict detected"));

        
        mockMvc.perform(post("/api/meetings/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Meeting\",\"startTime\":\"2024-10-20T10:00:00\",\"endTime\":\"2024-10-20T11:00:00\",\"participants\":[{\"id\":1},{\"id\":2}],\"ownerId\":3}"))
                .andExpect(status().isBadRequest()); 
    }

}

