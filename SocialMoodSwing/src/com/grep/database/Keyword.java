package com.grep.database;

public class Keyword {
	
	// Local private variables
	// Table key id
	private int id;
	
	// Topic name
	private String topic_name;
	
	// constructor
	public Keyword() {
		//empty
	}
	
	// constructor
	public Keyword(String name) {
		this.topic_name = name;
	}
	
	// constructor
	public Keyword(int id, String name) {
		this.id = id;
		this.topic_name = name;
	}
	
	
}
