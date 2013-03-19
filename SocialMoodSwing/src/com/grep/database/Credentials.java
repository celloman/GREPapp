/**
 * Credentials.Java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */

package com.grep.database;

/**
 *  public class Credentials 
 *  <br><br>
 *  Utility class containing OAuth credentials used in SQLite database
 */
public class Credentials {
	
	// Local private variables
	// Table key id
	/**
	 * private int id
	 */
	private int id;
	
	// OAuth consumer key and secret
	/**
	 * private String consumerKey
	 */
	private String consumerKey;
	
	/**
	 * private String consumerSecret
	 */
	private String consumerSecret;
	
	/**
	 * Constructor
	 */
	public Credentials() {
		//empty
	}
	
	/**
	 * Constructor
	 * @param key 	(String)
	 * @param secret 	(String)
	 */
	public Credentials(String key, String secret) {
		this.consumerKey = key;
		this.consumerSecret = secret;
	}
	
	/**
	 * Constructor
	 * @param id	(int)
	 * @param key	(String)
	 * @param secret	(String)
	 */
	public Credentials(int id, String key, String secret) {
		this.id = id;
		this.consumerKey = key;
		this.consumerSecret = secret;
	}
	
	// Get and set methods
	
	/**
	 * public int getId
	 * @return id
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * public void setId
	 * @param id	(int)
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * public String getConsumerKey
	 * @return consumerKey
	 */
	public String getConsumerKey() {
		return this.consumerKey;
	}
	
	/**
	 * public void setConsumerKey
	 * @param key	(String)
	 */
	public void setConsumerKey(String key) {
		this.consumerKey = key;
	}
	
	/**
	 * public String getConsumerSecret
	 * @return consumerSecret
	 */
	public String getConsumerSecret() {
		return this.consumerSecret;
	}
	
	/**
	 * setConsumerSecret
	 * @param secret	(String)
	 */
	public void setConsumerSecret(String secret) {
		this.consumerSecret	= secret;
	}
}
