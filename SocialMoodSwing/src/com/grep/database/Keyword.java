package com.grep.database;

public class Keyword {
	
	// Local private variables
	// Table key id
	private int id;
	
	private String keyword;
	
	// Foreign topic id for keyword
	private int t_id;
	
	// constructor
	public Keyword() {
		//empty
	}
	
	// constructor, sets keyword field
	public Keyword(int topic_id, String keyword) {
		this.keyword = keyword;
		this.t_id = topic_id;
	}
	
	// constructor, sets all fields
	public Keyword(int id, int topic_id, String keyword) {
		this.id = id;
		this.keyword = keyword;
		this.t_id = topic_id;
	}
	
	// Get and set methods
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getKeyword() {
		return this.keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public int getKeywordTopicId() {
		return this.t_id;
	}
	
	public void setKeywordTopicId(int id) {
		this.t_id = id;
	}
}
