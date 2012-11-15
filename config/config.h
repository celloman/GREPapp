#ifndef CONFIG_H
#define CONFIG_H

#include <iostream>
#include <fstream>
#include <string>
#include <ctime>

#ifndef DEBUG
	#define DEBUG (false)
#endif

#ifndef INFO
	#define INFO (false)
#endif

#define ERROR_LOG std::cerr << "ERROR " << __FILE__ << ":" << __LINE__ << ": "
#define INFO_LOG if(INFO) std::cerr << "INFO " << __FILE__ << ":" << __LINE__ << ": "
#define DEBUG_LOG if(DEBUG) std::cerr << "DEBUG " << __FILE__ << ":" << __LINE__ << ": "

inline void WEB_LOG(std::string title, std::string text, std::string type)
{
	static int id = 0;
	static time_t last_error_time = 0;

	time_t now;
	time(&now);

	if(type == "error")
	{
		time(&last_error_time);
	}

	if(now + 5 > last_error_time)
	{
		std::ofstream web_log;
		web_log.open("html/log.txt", std::ios::trunc);
		if(web_log.is_open())
		{
			web_log << "{\"id\":" << id++ << ", \"text\":\"" << text << "\", \"title\":\"" << title << "\", \"type\":\"" << type << "\"}";
			web_log.close();
		}
		else
		{
			ERROR_LOG << "couldn't open web log\n";
		}
	}
}

// if false, it will use Alchemy
#define USE_SENTIMENT140 (true)
// and you will need an Alchemy API key
#define ALCHEMY_KEY "3ab48469cc894436ef2ea8bf8f7bf76d8abb7cab"

// randomly generated sentement
#define FAKE_SENTIMENT (false)


#define TWITTER_ACCOUNT "vikings383:383vikings"

#define TWEET_CAP (100)
#define AVG_FOLLOWERS (208)
#define POPULAR_LIMIT (50000)

// offset from GMT; -8 for Pacific Time
#define TIME_ZONE_OFFSET (-8)

#define CSV_OUTPUT (true)
#define CSV_INTERVAL (30)

#endif