package com.grep.database;

public class Credentials {
	
	// Local private variables
	// Table key id
	private int id;
	
	// OAuth consumer key and secret
	private String consumerKey;
	private String consumerSecret;
	
	// constructor
	public Credentials() {
		this.id = 0;
		this.consumerKey = null;
		this.consumerSecret = null;
	}
	
	// constructor
	public Credentials(String key, String secret) {
		this.consumerKey = key;
		this.consumerSecret = secret;
	}
	
	// constructor
	public Credentials(int id, String key, String secret) {
		this.id = id;
		this.consumerKey = key;
		this.consumerSecret = secret;
	}
	
	// Get and set methods
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getConsumerKey() {
		return this.consumerKey;
	}
	
	public void setConsumerKey(String key) {
		this.consumerKey = key;
	}
	
	public String getConsumerSecret() {
		return this.consumerSecret;
	}
	
	public void setConsumerSecret(String secret) {
		this.consumerSecret	= secret;
	}
}
