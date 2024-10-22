package com.example.Calendar.Controller;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Calendar.Repository.EmployeeRepository;
import com.example.Calendar.Repository.MeetingRepository;
import com.example.Calendar.Service.MeetingService;
import com.example.Calendar.Utility.Employee;
import com.example.Calendar.Utility.Meeting;
import com.example.Calendar.Utility.MeetingRequest;


@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MeetingRepository meetingRepository; 

    
    @GetMapping("/free-slots")
    public ResponseEntity<?> findFreeSlots(
            @RequestParam Long ownerId,
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        return meetingService.findFreeSlots(ownerId, startTime, endTime);
    }

    
    @PostMapping("/book")
    public ResponseEntity<?> bookMeeting(@RequestBody Meeting meetingRequest) {
        // Validate the owner ID
        if (meetingRequest.getOwnerId() == null) {
            return ResponseEntity.badRequest().body("Owner ID must not be null");
        }

       
        Employee owner = employeeRepository.findById(meetingRequest.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        
        if (meetingRequest.getTitle() == null || meetingRequest.getStartTime() == null || meetingRequest.getEndTime() == null) {
            return ResponseEntity.badRequest().body("Meeting title, start time, and end time must not be null");
        }

        
        List<Employee> participants = meetingRequest.getParticipants();

        
        return meetingService.bookMeeting(
                meetingRequest.getTitle(),
                meetingRequest.getStartTime(),
                meetingRequest.getEndTime(),
                participants,
                owner 
        );
    }

   
    @PostMapping("/confirm")
    public ResponseEntity<Meeting> confirmMeeting(@RequestParam Long meetingId) {
        Meeting confirmedMeeting = meetingService.confirmMeeting(meetingId);
        return ResponseEntity.ok(confirmedMeeting);
    }

    
    @PostMapping("/conflicts")
    public ResponseEntity<List<Employee>> findConflicts(@RequestBody Meeting newMeeting) {
        List<Employee> conflicts = meetingService.findConflicts(newMeeting);
        return ResponseEntity.ok(conflicts);
    }

    
    @PostMapping
    public ResponseEntity<?> createMeeting(@RequestBody MeetingRequest meetingRequest) {
        // Validate the owner ID
        if (meetingRequest.getOwnerId() == null) {
            return ResponseEntity.badRequest().body("Owner ID must not be null");
        }

        
        Employee owner = employeeRepository.findById(meetingRequest.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        
        List<Employee> participants = new ArrayList<>();
        for (Long participantId : meetingRequest.getParticipantIds()) {
            if (participantId == null) {
                return ResponseEntity.badRequest().body("Participant ID must not be null");
            }
            Employee participant = employeeRepository.findById(participantId)
                    .orElseThrow(() -> new RuntimeException("Participant not found"));
            participants.add(participant);
        }

        
        LocalDateTime startTime = meetingRequest.getStartTime();
        LocalDateTime endTime = meetingRequest.getEndTime();

        if (isMeetingOverlapping(owner, startTime, endTime)) {
            return ResponseEntity.badRequest().body("Meeting time overlaps with an existing meeting.");
        }

       
        Meeting meeting = new Meeting(meetingRequest.getTitle(), startTime, endTime, participants, owner);
        meetingRepository.save(meeting);

        return ResponseEntity.ok(meeting);
    }

    
    private boolean isMeetingOverlapping(Employee owner, LocalDateTime startTime, LocalDateTime endTime) {
        List<Meeting> existingMeetings = meetingRepository.findByOwner(owner);
        for (Meeting meeting : existingMeetings) {
            // Check if the new meeting overlaps with any existing meeting
            if (startTime.isBefore(meeting.getEndTime()) && endTime.isAfter(meeting.getStartTime())) {
                return true; // Overlap detected
            }
        }
        return false; 
    }


}
