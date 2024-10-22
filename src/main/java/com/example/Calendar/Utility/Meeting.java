package com.example.Calendar.Utility;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import java.util.List;

@Entity
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean confirmed = false;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private Employee owner;

    @ManyToMany(fetch = FetchType.EAGER) 
    @JoinTable(
        name = "meeting_participants",
        joinColumns = @JoinColumn(name = "meeting_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> participants;
    

   
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public List<Employee> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Employee> participants) {
        this.participants = participants;
    }

    public Employee getOwner() {
        return owner;
    }

    public void setOwner(Employee owner) {
        this.owner = owner;
    }

    
    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }

 
    public Meeting() {
        super();
    }

    public Meeting(String title, LocalDateTime startTime, LocalDateTime endTime, List<Employee> participants, Employee owner) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = participants;
        this.owner = owner; 
    }

    public Meeting(Long id, boolean confirmed) {
        this.id = id;
        this.confirmed = confirmed;
    }

    
    @Override
    public String toString() {
        return "Meeting [id=" + id + ", title=" + title + ", startTime=" + startTime 
                + ", endTime=" + endTime + ", confirmed=" + confirmed + "]";
    }
}
