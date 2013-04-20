/**
 * Credentials.Java
 * 
 * @author Gresham, Ryan, Everett, Pierce
 *
 */

package com.grep.database;

/**
 *  {@code public class Credentials}
 *  <br><br>
 *  Utility class containing OAuth credentials used in SQLite database
 */
public class Credentials {
	
	// Local private variables
	// Table key id
	private int id;
	
	// OAuth user key and secret
	private String userKey;
	private String userSecret;
	
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
		this.userKey = key;
		this.userSecret = secret;
	}
	
	/**
	 * Constructor
	 * @param id	(int)
	 * @param key	(String)
	 * @param secret	(String)
	 */
	public Credentials(int id, String key, String secret) {
		this.id = id;
		this.userKey = key;
		this.userSecret = secret;
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
	 * {@code public String getUserKey}
	 * @return userKey
	 */
	public String getUserKey() {
		return this.userKey;
	}
	
	/**
	 * {@code public void setUserKey}
	 * @param key	(String)
	 */
	public void setUserKey(String key) {
		this.userKey = key;
	}
	
	/**
	 * {@code public String getUserSecret}
	 * @return userSecret
	 */
	public String getUserSecret() {
		return this.userSecret;
	}
	
	/**
	 * {@code setUserSecret}
	 * @param secret	(String)
	 */
	public void setUserSecret(String secret) {
		this.userSecret	= secret;
	}
}
