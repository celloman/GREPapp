#include <iostream>
#include <string>
#include <stdlib.h>

#include <curl/curl.h>
#include <jsoncpp/json/json.h>

using namespace std;

size_t twitter_write_function(char *data, size_t size, size_t nmemb, void *usrdata);

class tweet {

private:
	string to_lowercase(string text);

public:
	string m_text;
	tweet(char *json_data);
};


class twitter_stream {

private:
	CURL *m_curl;

public:
	void (*m_callback)(tweet);

	twitter_stream(void (*callback)(tweet));
	bool start();

	// void pause();
	// void stop();
};
