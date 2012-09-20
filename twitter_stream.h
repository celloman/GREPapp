#include <iostream>
#include <string>
#include <stdlib.h>

#include <curl/curl.h>
#include <json/json.h>

using namespace std;

size_t write_function(char *data, size_t size, size_t nmemb, void *usrdata);

class tweet {

private:

	string to_lowercase(string text) //convert a string to lowercase
	{
		for(int i = 0; i < text.length(); ++i)
			text[i] = tolower(text[i]);

		return text;
	}

public:

	string m_text;

	tweet(char *json_data){
		Json::Value root;
		Json::Reader reader;
		if(json_data == NULL || !reader.parse(json_data, root))
		{
			cerr << "failed to decode tweet JSON!\n";
			exit(-1);
		}
		else
		{
			m_text = to_lowercase(root["text"].asString());
		}
	}
};

class twitter_stream {

private:

	CURL *m_curl;

public:

	void (*m_callback)(tweet);

	twitter_stream(void (*callback)(tweet))
	{
		m_callback = callback;
		//m_keywords = keywords;
	}

	bool start()
	{
		CURLcode res;

		m_curl = curl_easy_init();

		if(m_curl)
		{
			curl_easy_setopt(m_curl, CURLOPT_URL, "https://stream.twitter.com/1/statuses/filter.json");
			curl_easy_setopt(m_curl, CURLOPT_POST, 1);
			curl_easy_setopt(m_curl, CURLOPT_POSTFIELDS, "track=obama,romney");
			curl_easy_setopt(m_curl, CURLOPT_USERPWD, "vikings383:383vikings");
			curl_easy_setopt(m_curl, CURLOPT_WRITEDATA, this);
			curl_easy_setopt(m_curl, CURLOPT_WRITEFUNCTION, write_function);
			// Perform the request, res will get the return code
			res = curl_easy_perform(m_curl);
			// Check for errors
			if(res != CURLE_OK)
			{
				cerr << "curl_easy_perform() failed: " << curl_easy_strerror(res);
				return false;
			}
			
			curl_easy_cleanup(m_curl);
		}
		else
		{
			return false;
		}
	}

	// void pause();
	// void stop();

};

size_t write_function(char *data, size_t size, size_t nmemb, void *usrdata)
{
	if(data != NULL)
	{
		twitter_stream *ts = (twitter_stream*)(usrdata);
		tweet t = tweet(data);
		ts->m_callback(t);
	}
	else
	{
		cerr << "null json string returned from twitter...\n";
	}
	
	return size * nmemb;
}