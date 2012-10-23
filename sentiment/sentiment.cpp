/******************************************************
*
* CS383 Project -- Social Media and Political Mood
* Team Vikings -- Gresham, Serendel, and Ryan
*
* Sentiment analysis powered by AlchemyAPI
* Learn more about this service at www.alchemyapi.com
*
* sentiment.cpp
*
******************************************************/

#include "sentiment.h"

size_t alchemy_write_function_liberal(char *data, size_t size, size_t nmemb, void *usrdata)
{
	Json::Value root;
	Json::Reader reader;
	if(data == NULL || !reader.parse(data, root))
	{
		ERROR_LOG << "failed to decode alchemy JSON!\n" << reader.getFormattedErrorMessages() << data << "\n\n";
	}
	else if(root["status"].asString() == "ERROR")
	{
		ERROR_LOG << "alchemy returned error status\n"; 
	}
	else
	{
		tweet *t  = (tweet*)usrdata;
		double sentiment = atof(root["docSentiment"]["score"].asString().c_str());
		if(sentiment > 0)
			t->m_liberal = sentiment;
		else
			t->m_conservative = -sentiment;
		t->print(tweet::LIBERAL | tweet::CONSERVATIVE);
	}

	return size * nmemb;
}

size_t alchemy_write_function_conservative(char *data, size_t size, size_t nmemb, void *usrdata)
{
	Json::Value root;
	Json::Reader reader;
	if(data == NULL || !reader.parse(data, root))
	{
		ERROR_LOG << "failed to decode alchemy JSON!\n" << reader.getFormattedErrorMessages() << data << "\n\n";
	}
	else if(root["status"].asString() == "ERROR")
	{
		ERROR_LOG << "alchemy returned error status\n";
	}
	else
	{
		tweet *t  = (tweet*)usrdata;
		double sentiment = atof(root["docSentiment"]["score"].asString().c_str());
		if(sentiment > 0)
			t->m_conservative = sentiment;
		else
			t->m_liberal = -sentiment;
		t->print(tweet::LIBERAL | tweet::CONSERVATIVE);
	}

	return size * nmemb;
}


sentiment::sentiment()
{
	m_curl = curl_multi_init();
	m_requests = 0;
}

void sentiment::get_liberal(tweet t, string subject)
{
	get(t, subject, alchemy_write_function_liberal);
}

void sentiment::get_conservative(tweet t, string subject)
{
	get(t, subject, alchemy_write_function_conservative);
}

void sentiment::get(tweet t, string subject, size_t (*write_function)(char*, size_t, size_t, void *))
{
	CURL* curl = curl_easy_init();

	if(curl)
	{
		string url = "http://access.alchemyapi.com/calls/text/TextGetTargetedSentiment?apikey=3ab48469cc894436ef2ea8bf8f7bf76d8abb7cab&outputMode=json&text=";
		url += curl_easy_escape(curl, t.m_text.c_str(), t.m_text.length());
		url += "&target=";
		url += subject;

		curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, &t);
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_function);
		curl_multi_add_handle(m_curl, curl);
		curl_multi_perform(m_curl, &m_requests);
	}
}
