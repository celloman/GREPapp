#include "twitter_stream.h"

size_t twitter_write_function(char *data, size_t size, size_t nmemb, void *usrdata)
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

// constructor (takes callback function and keywords)
twitter_stream::twitter_stream(void (*callback)(tweet), vector<string> keywords)
{
	m_callback = callback;
	m_keywords = keywords;
}

// start the curl request
bool twitter_stream::start()
{
	CURLcode res;

	m_curl = curl_easy_init();

	if(m_curl)
	{
		string fields;
		
		string keywords = "track=" + m_keywords[0];
		for(int i = 1; i < m_keywords.size(); i++)
			keywords += ',' + m_keywords[i];
		
		fields += keywords;

		curl_easy_setopt(m_curl, CURLOPT_URL, "https://stream.twitter.com/1/statuses/filter.json");
		curl_easy_setopt(m_curl, CURLOPT_POST, 1);
		curl_easy_setopt(m_curl, CURLOPT_POSTFIELDS, fields.c_str());
		curl_easy_setopt(m_curl, CURLOPT_USERPWD, "vikings383:383vikings");
		curl_easy_setopt(m_curl, CURLOPT_WRITEDATA, this);
		curl_easy_setopt(m_curl, CURLOPT_WRITEFUNCTION, twitter_write_function);
		// Perform the request, res will get the return code
		res = curl_easy_perform(m_curl);
		// Check for errors
		if(res != CURLE_OK)
		{
			cerr << "curl_easy_perform() failed: " << curl_easy_strerror(res) << "\n";
			return false;
		}
		
		curl_easy_cleanup(m_curl);
	}
	else
	{
		return false;
	}
}