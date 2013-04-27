/**
 * Session.java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 */

package com.grep.database;

import java.text.SimpleDateFormat;

/**
 * 	{@code public class Session}
 * 	<br><br>
 *  Utility class containing Analysis Session info used in SQLite database
 */
public class Session {
	
	// Local private variables
	// Table key id
	private int id;
	
	// Foreign topic id
	private int t_id;
	
	// Time session was started
	private String start_time;
	
	// Duration of session in seconds
	private int duration;
	
	// Tweet processing statistics
	private int tweets_processed;
	private int avg_pos_sentiment;
	private int avg_neg_sentiment;
	
	/**
	 * Constructor
	 */
	public Session() {
		//empty
	}
	
	/**
	 * Constructor
	 * <br><br>
	 * This constructor is used for creating a new session, the current time 
	 * and database id will be added automatically
	 * @param topic_id	(int)
	 * @param duration	(int)
	 * @param num_tweets	(int)
	 * @param pos	(int)
	 * @param neg	(int)
	 */
	public Session(int topic_id, int duration, int num_tweets, int pos, int neg) {
		this.t_id = topic_id;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
		this.start_time = dateFormat.format(java.util.Calendar.getInstance().getTime());
		this.duration = duration;
		this.tweets_processed = num_tweets;
		this.avg_pos_sentiment = pos;
		this.avg_neg_sentiment = neg;
	}
	
	/**
	 * Constructor
	 * <br><br>
	 * This constructor will used for retrieving a session from the database
	 * @param id	(int)
	 * @param time (String)
	 * @param topic_id	(int)
	 * @param duration	(int)
	 * @param num_tweets	(int)
	 * @param pos	(int)
	 * @param neg	(int)
	 */
	public Session(int id, int topic_id, String time, int duration, int num_tweets, int pos, int neg) {
		this.id = id;
		this.t_id = topic_id;
		this.start_time = time;
		this.duration = duration;
		this.tweets_processed = num_tweets;
		this.avg_pos_sentiment = pos;
		this.avg_neg_sentiment = neg;
	}
	
	// Get and set methods
	
	/**
	 * {@code public int getId}
	 * @return id
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * {@code public void setId}
	 * @param id	(int)
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * {@code public int getSessionTopoicId}
	 * @return t_id
	 */
	public int getSessionTopicId() {
		return this.t_id;
	}
	
	/**
	 * {@code public void setSessionTopicId}
	 * @param topic_id	(int)
	 */
	public void setSessionTopicId(int topic_id) {
		this.t_id = topic_id;
	}
	
	/**
	 * {@code public String getStartTime}
	 * @return start_time
	 */
	public String getStartTime() {
		return this.start_time;
	}
	
	/**
	 * {@code public void setStartTime}
	 * @param time	(String)
	 */
	public void setStartTime(String time) {
		this.start_time = time;
	}
	
	/**
	 * {@code public int getDuration}
	 * @return duration
	 */
	public int getDuration() {
		return this.duration;
	}
	
	/**
	 * {@code public void setDuration}
	 * @param duration	(int)
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	/**
	 * {@code public int getNumTweetsProcessed}
	 * @return tweets_processed
	 */
	public int getNumTweetsProcessed() {
		return this.tweets_processed;
	}
	
	/**
	 * {@code public void setNumTweetsProcessed}
	 * @param num_tweets	(int)
	 */
	public void setNumTweetsProcessed(int num_tweets) {
		this.tweets_processed = num_tweets;
	}
	
	/**
	 * {@code public int getAvgPosSentiment}
	 * @return avg_pos_sentiment
	 */
	public int getAvgPosSentiment() {
		return this.avg_pos_sentiment;
	}
	
	/**
	 * {@code public void setAvgPosSentiment}
	 * @param pos	(int)
	 */
	public void setAvgPosSentiment(int pos) {
		this.avg_pos_sentiment = pos;
	}
	
	/**
	 * {@code public int getAvgNegSentiment}
	 * @return avg_neg_sentiment
	 */
	public int getAvgNegSentiment() {
		return this.avg_neg_sentiment;
	}
	
	/**
	 * {@code public void setAvgNegSentiment}
	 * @param neg	(int)
	 */
	public void setAvgNegSentiment(int neg) {
		this.avg_neg_sentiment = neg;
	}
}
