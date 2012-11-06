/******************************************************
*
* CS383 Project -- Social Media and Political Mood
* Team Vikings -- Gresham, Serendel, and Ryan
*
* Sentiment analysis powered by AlchemyAPI
* Learn more about this service at www.alchemyapi.com
*
* sentiment.h
*
******************************************************/

#ifndef SENTIMENT_H
#define SENTIMENT_H

#include <iostream>
#include <string>
#include <stdlib.h> // for atof
#include <curl/curl.h>
#include <jsoncpp/json/json.h>

#include "tweet/tweet.h"
#include "config/config.h"

using namespace std;


class sentiment {

private:

	static size_t alchemy_write_function(char *data, size_t size, size_t nmemb, void *usrdata);
	static size_t sentiment140_write_function(char *data, size_t size, size_t nmemb, void *usrdata);

	void send_request(double &sentiment, string text, string subject);

	CURLM *m_curl;
	int m_requests;
	static bool m_alchemy_available;

public:

	double get(string text, string subject);

	sentiment();
	~sentiment();
};

#endif
