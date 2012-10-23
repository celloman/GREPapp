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
#include <stdlib.h>

#include <curl/curl.h>
#include <jsoncpp/json/json.h>

#include "tweet/tweet.h"

using namespace std;

size_t alchemy_write_function_liberal(char *data, size_t size, size_t nmemb, void *usrdata);
size_t alchemy_write_function_conservative(char *data, size_t size, size_t nmemb, void *usrdata);


class sentiment {

private:
	CURLM *m_curl;
	int m_requests;
	void get(tweet t, string subject, size_t (*write_function)(char*, size_t, size_t, void *));

public:
	void get_liberal(tweet t, string subject);
	void get_conservative(tweet t, string subject);
	sentiment();
};

#endif
