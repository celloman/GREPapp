package com.grep.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * This classes handles all SQLite database functionality. 
 * 
 * It contains CRUD operations for each table in the 
 * database.
 * 
 * @author Everett
 *
 */
public class DatabaseHandler extends SQLiteOpenHelper {
	
	//Static variables
	//Database version
	private static final int DATABASE_VERSION = 1;
	
	// Database name
	private static final String DATABASE_NAME = "";
	
	// Table names
	private static final String AUTH_TABLE = "credentials";
	private static final String TOPIC_TABLE = "topics";
	private static final String KEYWORD_TABLE = "keywords";
	private static final String SESSION_TABLE = "sessions";
	
	// Authentication Table column names
	private static final String USER_KEY_ID = "id";
	private static final String CONSUMER_KEY = "c_key";
	private static final String CONSUMER_SECRET = "c_secret";
	
	// Topic Table column names
	private static final String TOPIC_KEY_ID = "id";
	private static final String TOPIC_NAME = "topic";
	
	// Keyword Table column names
	private static final String KEYWORD_KEY_ID = "id";
	private static final String KEYWORD_TEXT = "keyword";
	private static final String KEYWORD_TOPIC_ID = "topic_id";
	
	// Session Table column names
	private static final String	SESSION_KEY_ID = "id";
	private static final String SESSION_TOPIC_ID = "topic_id";
	private static final String	SESSION_START_TIME = "start_time";
	private static final String	SESSION_DURATION = "duration";
	private static final String	SESSION_TWEETS_PROCESSED = "tweets_processed";
	private static final String	SESSION_AVG_POSITIVE = "pos_sentament";
	private static final String SESSION_AVG_NEGATIVE = "neg_sentament";
	
	// Constructor
	public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	// Create Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Authentication Table in SQLite DB
    	String CREATE_AUTH_TABLE = "CREATE TABLE " + AUTH_TABLE + " ("
                + USER_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CONSUMER_KEY + " TEXT, "
                + CONSUMER_SECRET + " TEXT" + ")";
        db.execSQL(CREATE_AUTH_TABLE);
        
        // Create Topic Table in SQLite DB
        String CREATE_TOPIC_TABLE = "CREATE TABLE " + TOPIC_TABLE + " ("
        		+ TOPIC_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TOPIC_NAME + " TEXT "  + ")";
        db.execSQL(CREATE_TOPIC_TABLE);
        
        // Create Topic Table in SQLite DB
        String CREATE_KEYWORD_TABLE = "CREATE TABLE " + KEYWORD_TABLE + " ("
        		+ KEYWORD_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEYWORD_TEXT + " TEXT,"
        		+ KEYWORD_TOPIC_ID + " INTEGER, FOREIGN KEY (" + KEYWORD_TOPIC_ID + ") REFERENCES "
        		+ TOPIC_TABLE + " (" + TOPIC_KEY_ID + ")" + ")";
        db.execSQL(CREATE_KEYWORD_TABLE);
        
        // Create Topic Table in SQLite DB
        String CREATE_SESSION_TABLE = "CREATE TABLE " + SESSION_TABLE + " ("
        		+ SESSION_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SESSION_TOPIC_ID 
        		+ " INTEGER, FOREIGN KEY (" + SESSION_TOPIC_ID + ") REFERENCES " + TOPIC_TABLE 
        		+ " (" + TOPIC_KEY_ID + "), " + SESSION_START_TIME + " TEXT, " + SESSION_DURATION 
        		+ " TEXT, " + SESSION_TWEETS_PROCESSED + " INTEGER, " + SESSION_AVG_POSITIVE
        		+ " INTEGER, " + SESSION_AVG_NEGATIVE + " INTEGER" + ")";
        db.execSQL(CREATE_SESSION_TABLE);
        
    }
 
    // Upgrade database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    	// Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + AUTH_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TOPIC_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + KEYWORD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SESSION_TABLE);
        
        // Create tables again
        onCreate(db);
    }
    
    /*
     * CRUD operations ( Create, Read, Update, Delete )
     */
    
    //
    
}
