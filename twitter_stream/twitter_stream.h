#ifndef SENTIMENT_H
#define SENTIMENT_H

#include <iostream>
#include <string>
#include <vector>

#include <curl/curl.h>
#include <jsoncpp/json/json.h>

#include "tweet/tweet.h"

using namespace std;

size_t twitter_write_function(char *data, size_t size, size_t nmemb, void *usrdata);

class twitter_stream {

private:
	CURL *m_curl;

public:
	void (*m_callback)(tweet);
	vector<string> m_keywords;

	twitter_stream(void (*callback)(tweet), vector<string> keywords);

	bool start();
};

#endif