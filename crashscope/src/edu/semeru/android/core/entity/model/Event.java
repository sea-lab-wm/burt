package edu.semeru.android.core.entity.model;

import java.util.ArrayList;
import java.util.List;

public class Event {

	private Coords start_location; // Start x, y position of an event
	private Coords end_location; // End x, y position of an event
	private double pause_duration; // If event is a delay, how long to wait before continuing
	private double last_event; // How long the previous event took
	private String event_label; // What the event is (i.e. SWIPE, CLICK, LONG CLICK, etc)
	private boolean is_delay = false; // Used to determine if event is a delay event
	private double wait_per_command = 0.0; // How long to wait before executing the next sendevent command 

	private List<String> raw_commands;
    
	// Constructor for non delay events
    public Event(Coords start_location, Coords end_location, double last_event, String event_label, double wait_per_command) {
    	this.start_location = start_location;
    	this.end_location = end_location;
    	this.last_event = last_event;
    	this.event_label = event_label;
    	this.wait_per_command = wait_per_command;
    }
    
 // Constructor for delay events
    public Event(double pause_duration) {
    	this.pause_duration = pause_duration;
    	this.is_delay = true;
    	this.event_label = "DELAY";
    	this.raw_commands = new ArrayList<>();
    	this.raw_commands.add("sleep " + this.pause_duration);
    }

	public Coords getStart_location() {
		return start_location;
	}

	public void setStart_location(Coords start_location) {
		this.start_location = start_location;
	}

	public Coords getEnd_location() {
		return end_location;
	}

	public void setEnd_location(Coords end_location) {
		this.end_location = end_location;
	}

	public double getPause_duration() {
		return pause_duration;
	}

	public void setPause_duration(double pause_duration) {
		this.pause_duration = pause_duration;
	}

	public double getLast_event() {
		return last_event;
	}

	public void setLast_event(double last_event) {
		this.last_event = last_event;
	}

	public String getEvent_label() {
		return event_label;
	}

	public void setEvent_label(String event_label) {
		this.event_label = event_label;
	}

	public boolean is_delay() {
		return is_delay;
	}

	public void set_is_delay(boolean is_delay) {
		this.is_delay = is_delay;
	}
	
	public double getWait_per_command() {
		return wait_per_command;
	}
	
	public void setWait_per_command(double wait_per_command) {
		this.wait_per_command = wait_per_command;
	}
	
	public List<String> getRaw_commands() {
		return raw_commands;
	}

	public void setRaw_commands(List<String> raw_commands) {
		this.raw_commands = raw_commands;
	}
	
	public String toString() {
		String event = this.event_label + ": ";
		if (this.event_label.equals("DELAY"))
			event += this.pause_duration;
		else
			event += "start_loc = " + this.start_location + " end_loc = " + this.end_location;
		return event;
	}
}
