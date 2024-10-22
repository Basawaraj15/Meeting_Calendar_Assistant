package com.example.Calendar.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.Calendar.Repository.MeetingRepository;
import com.example.Calendar.Repository.EmployeeRepository;
import com.example.Calendar.Utility.Employee;
import com.example.Calendar.Utility.Meeting;
import com.example.Calendar.Utility.MeetingRequest;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import com.example.Calendar.Exception.ConflictException;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    
    public ResponseEntity<?> findFreeSlots(Long ownerId, LocalDateTime startTime, LocalDateTime endTime) {
        Employee owner = employeeRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        List<Meeting> existingMeetings = meetingRepository.findByOwner(owner);

        List<LocalDateTime> freeSlots = new ArrayList<>();

        
        Duration meetingDuration = Duration.ofMinutes(30);
        LocalDateTime currentTime = startTime;

        while (currentTime.plus(meetingDuration).isBefore(endTime)) {
            boolean isFree = true;

            for (Meeting meeting : existingMeetings) {
                if (currentTime.isBefore(meeting.getEndTime()) && currentTime.plus(meetingDuration).isAfter(meeting.getStartTime())) {
                    isFree = false;
                    break;
                }
            }

            if (isFree) {
                freeSlots.add(currentTime);
            }

            currentTime = currentTime.plusMinutes(30); // Increment by 30 minutes
        }

        return ResponseEntity.ok(freeSlots);
    }


    
    public List<Employee> findConflicts(Meeting newMeeting) {
        List<Employee> conflictingEmployees = new ArrayList<>();

        
        List<Meeting> existingMeetings = meetingRepository.findAll();

        
        for (Meeting existingMeeting : existingMeetings) {
            
            if (isMeetingOverlapping(newMeeting, existingMeeting)) {
                
                conflictingEmployees.addAll(existingMeeting.getParticipants());
            }
        }

        return conflictingEmployees;
    }
    private boolean isMeetingOverlapping(Meeting meeting1, Meeting meeting2) {
        // Check if the start time of one meeting is before the end time of the other meeting
        // and vice versa
        return (meeting1.getStartTime().isBefore(meeting2.getEndTime()) && meeting1.getEndTime().isAfter(meeting2.getStartTime())) ||
               (meeting2.getStartTime().isBefore(meeting1.getEndTime()) && meeting2.getEndTime().isAfter(meeting1.getStartTime()));
    }

    // Book a new meeting
    public ResponseEntity<?> bookMeeting(String title, LocalDateTime startTime, LocalDateTime endTime, List<Employee> participants, Employee owner) {

        // Check for meeting conflicts
        for (Meeting existingMeeting : meetingRepository.findAll()) {
            if (isMeetingOverlapping(existingMeeting, startTime, endTime)) {
                // Throw custom ConflictException if a conflict is found
                throw new ConflictException("A meeting is already scheduled at this time.");
            }
        }

        // If no conflicts, create and save the meeting
        Meeting meeting = new Meeting(title, startTime, endTime, participants, owner); // Set owner

        // Save the meeting
        Meeting savedMeeting = meetingRepository.save(meeting);

        // Return a successful response with the saved meeting
        return ResponseEntity.ok(savedMeeting);
    }


    


	// Confirm a meeting by ID
    public Meeting confirmMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid Meeting ID: " + meetingId));
        return meeting;
    }
    public ResponseEntity<?> createMeeting(MeetingRequest meetingRequest) {
        // Validate input fields
        if (meetingRequest.getTitle() == null || meetingRequest.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body("Meeting title must not be empty");
        }

        if (meetingRequest.getStartTime() == null || meetingRequest.getEndTime() == null) {
            return ResponseEntity.badRequest().body("Start time and end time must not be null");
        }

        if (meetingRequest.getStartTime().isAfter(meetingRequest.getEndTime())) {
            return ResponseEntity.badRequest().body("Start time must be before end time");
        }

        // Fetch owner
        Employee owner = employeeRepository.findById(meetingRequest.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + meetingRequest.getOwnerId()));

        // Prepare participants
        List<Employee> participants = new ArrayList<>();
        for (Long participantId : meetingRequest.getParticipantIds()) {
            if (participantId == null) {
                return ResponseEntity.badRequest().body("Participant ID must not be null");
            }

            Employee participant = employeeRepository.findById(participantId)
                    .orElseThrow(() -> new RuntimeException("Participant not found with ID: " + participantId));
            participants.add(participant);
        }

        // Check for overlapping meetings for owner
        if (isOverlappingWithExistingMeetings(owner, meetingRequest.getStartTime(), meetingRequest.getEndTime())) {
            return ResponseEntity.badRequest().body("Meeting time overlaps with an existing meeting for the owner: " + owner.getId());
        }

        // Check for overlapping meetings for participants
        for (Employee participant : participants) {
            if (isOverlappingWithExistingMeetings(participant, meetingRequest.getStartTime(), meetingRequest.getEndTime())) {
                return ResponseEntity.badRequest().body("Meeting time overlaps with an existing meeting for participant: " + participant.getId());
            }
        }

        // Call the existing bookMeeting method
        return bookMeeting(meetingRequest.getTitle(), meetingRequest.getStartTime(), meetingRequest.getEndTime(), participants, owner);
    }

    private boolean isOverlappingWithExistingMeetings(Employee employee, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        List<Meeting> existingMeetings = meetingRepository.findAllByParticipantsContains(employee);
        for (Meeting existingMeeting : existingMeetings) {
            if (isMeetingOverlapping(existingMeeting, newStartTime, newEndTime)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMeetingOverlapping(Meeting existingMeeting, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        // Check if the new meeting's time overlaps with the existing meeting's time
        return (newStartTime.isBefore(existingMeeting.getEndTime()) && newEndTime.isAfter(existingMeeting.getStartTime()));
    }
	



}


