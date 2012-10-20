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

using namespace std;

size_t alchemy_write_function(char *data, size_t size, size_t nmemb, void *usrdata);


class sentiment {

private:
	static CURL *m_curl;

public:
	static string m_response_buffer;
	static double get(string text, string subject);
};

#endif
