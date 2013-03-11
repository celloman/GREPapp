package com.grep.database;

public class Topic {
	
	// Local private variables
	// Table key id
	private int id;
	
	private String topic_name;
	
	// constructor
	public Topic() {
		//empty
	}
	
	// constructor
	public Topic(String name) {
		this.topic_name = name;
	}
	
	// constructor
	public Topic(int id, String name) {
		this.id = id;
		this.topic_name = name;
	}
	
	// Get and set methods
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTopicName() {
		return this.topic_name;
	}
	
	public void setTopicName(String name) {
		this.topic_name = name;
	}
}