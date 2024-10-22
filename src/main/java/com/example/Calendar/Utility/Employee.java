package com.example.Calendar.Utility;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "owner")
    private List<Meeting> meetings;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Meeting> getMeetings() {
		return meetings;
	}

	public void setMeetings(List<Meeting> meetings) {
		this.meetings = meetings;
	}

	 @Override
	    public String toString() {
	        
	        return "Employee [id=" + id + ", name=" + name + "]";
	    }

	    // Constructors
	    public Employee(Long id, String name, List<Meeting> meetings) {
	        super();
	        this.id = id;
	        this.name = name;
	        this.meetings = meetings;
	    }

	    public Employee() {
	        super();
	    }

	    public Employee(long id, String name) {
	        this.id = id;
	        this.name = name;
	    }
	}
	


