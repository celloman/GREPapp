package com.grep.database;

public class Credentials {
	
	// Private variables
	// Table key id
	private int id;
	
	// OAuth consumer key and secret
	private String consumerKey;
	private String consumerSecret;
	
	//constructors
	public Credentials() {
		//empty
	}
	
	public Credentials(String key, String secret) {
		this.consumerKey = key;
		this.consumerSecret = secret;
	}
	
	public Credentials(int id, String key, String secret) {
		this.id = id;
		this.consumerKey = key;
		this.consumerSecret = secret;
	}
}
