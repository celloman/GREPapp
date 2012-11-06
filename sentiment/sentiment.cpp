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
	double sentiment = sentiment::parse_json(data);

	if(sentiment != 0)
	{
		tweet *t  = (tweet*)usrdata;
		if(sentiment > 0)
			t->m_liberal = sentiment;
		else
			t->m_conservative = -sentiment;

		t->m_liberal *= t->m_weight;
		t->m_conservative *= t->m_weight;

		DEBUG_LOG << "liberal: (" << t->m_weight << ", " << sentiment << ")\n";

		t->print(tweet::LIBERAL | tweet::CONSERVATIVE);
	}
	else
	{
		INFO_LOG << "neutral tweet\n";
	}

	return size * nmemb;
}

size_t alchemy_write_function_conservative(char *data, size_t size, size_t nmemb, void *usrdata)
{
	double sentiment = sentiment::parse_json(data);

	if(sentiment != 0)
	{
		tweet *t  = (tweet*)usrdata;
		if(sentiment > 0)
			t->m_conservative = sentiment;
		else
			t->m_liberal = -sentiment;

		t->m_liberal *= t->m_weight;
		t->m_conservative *= t->m_weight;

		DEBUG_LOG << "conservative: (" << t->m_weight << ", " << sentiment << ")\n";
		
		t->print(tweet::LIBERAL | tweet::CONSERVATIVE);
	}
	else
	{
		INFO_LOG << "neutral tweet\n";
	}

	return size * nmemb;
}

double sentiment::parse_json(char *data)
{
	Json::Value root;
	Json::Reader reader;
	if(data == NULL || !reader.parse(data, root))
	{
		ERROR_LOG << "failed to decode alchemy JSON!\n" << reader.getFormattedErrorMessages() << data << "\n\n";
		return 0;
	}
	else if(root["status"].asString() == "ERROR")
	{
		if(root["statusInfo"].asString() == "unsupported-text-language")
			ERROR_LOG << "alchemy error: unsupported-text-language (" << root["language"].asString() << ")\n";
		else
			ERROR_LOG << "alchemy error: " << root["statusInfo"].asString() << "\n";
		return 0;
	}
	else
	{
		INFO_LOG << "alchemy success\n";
		//DEBUG_LOG << atof(root["docSentiment"]["score"].asString().c_str()) << "\n";
		return atof(root["docSentiment"]["score"].asString().c_str());
	}
}


sentiment::sentiment()
{
	m_curl = curl_multi_init();
	m_requests = 0;
}

void sentiment::get_liberal(tweet &t, string subject)
{
	#if FAKE_SENTIMENT
		double sentiment = (double) ((rand() % 200000) - 100000) / 100000.0;

		if(sentiment > 0)
			t.m_liberal = sentiment;
		else
			t.m_conservative = -sentiment;

		t.m_liberal *= t.m_weight;
		t.m_conservative *= t.m_weight;
	#else
		get(t, subject, alchemy_write_function_liberal);
	#endif
}

void sentiment::get_conservative(tweet &t, string subject)
{
	#if FAKE_SENTIMENT
		double sentiment = ((double) ((rand() % 200000) - 100000)) / 100000.0;

		if(sentiment > 0)
			t.m_conservative = sentiment;
		else
			t.m_liberal = -sentiment;

		t.m_liberal *= t.m_weight;
		t.m_conservative *= t.m_weight;
	#else
		get(t, subject, alchemy_write_function_conservative);
	#endif
}

void sentiment::get(tweet &t, string subject, size_t (*write_function)(char*, size_t, size_t, void *))
{
	CURL* curl = curl_easy_init();

	if(curl)
	{
		string url = "http://access.alchemyapi.com/calls/text/TextGetTargetedSentiment?apikey="ALCHEMY_KEY"&outputMode=json&text=";
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
