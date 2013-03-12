package com.grep.database;

public class Session {
	
	// Local private variables
	// Table key id
	private int id;
	
	// Foreign topic id
	private int t_id;
	
	// Time session was started
	private String start_time;
	
	// Duration of session
	private String duration;
	
	// Tweet processing statistics
	private int tweets_processed;
	private int avg_pos_sentiment;
	private int avg_neg_sentiment;
	
	// constructor
	public Session() {
		this.id = 0;
		this.t_id = 0;
		this.start_time = null;
		this.duration = null;
		this.tweets_processed = 0;
		this.avg_pos_sentiment = 0;
		this.avg_neg_sentiment = 0;
	}
	
	// constructor, sets all fields but id and t_id
	public Session(String time, String duration, int num_tweets, int pos, int neg) {
		this.start_time = time;
		this.duration = duration;
		this.tweets_processed = num_tweets;
		this.avg_pos_sentiment = pos;
		this.avg_neg_sentiment = neg;
	}
	
	// constructor, sets all fields
	public Session(int id, int topic_id, String time, String duration, int num_tweets, int pos, int neg) {
		this.id = id;
		this.t_id = topic_id;
		this.start_time = time;
		this.duration = duration;
		this.tweets_processed = num_tweets;
		this.avg_pos_sentiment = pos;
		this.avg_neg_sentiment = neg;
	}
	
	// Get and set methods
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getSessionTopicId() {
		return this.t_id;
	}
	
	public void setSessionTopicId(int topic_id) {
		this.t_id = topic_id;
	}
	
	public String getStartTime() {
		return this.start_time;
	}
	
	public void setStartTime(String time) {
		this.start_time = time;
	}
	
	public String getDuration() {
		return this.duration;
	}
	
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public int getNumTweetsProcessed() {
		return this.tweets_processed;
	}
	
	public void setNumTweetsProcessed(int num_tweets) {
		this.tweets_processed = num_tweets;
	}
	
	public int getAvgPosSentiment() {
		return this.avg_pos_sentiment;
	}
	
	public void setAvgPosSetniment(int pos) {
		this.avg_pos_sentiment = pos;
	}
	
	public int getAvgNegSentiment() {
		return this.avg_neg_sentiment;
	}
	
	public void setAvgNegSentiment(int neg) {
		this.avg_neg_sentiment = neg;
	}
}
