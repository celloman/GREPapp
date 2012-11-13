#ifndef CONFIG_H
#define CONFIG_H

#include <iostream>

#ifndef DEBUG
	#define DEBUG (false)
#endif

#ifndef INFO
	#define INFO (false)
#endif

#define ERROR_LOG std::cerr << "ERROR " << __FILE__ << ":" << __LINE__ << ": "
#define INFO_LOG if(INFO) std::cerr << "INFO " << __FILE__ << ":" << __LINE__ << ": "
#define DEBUG_LOG if(DEBUG) std::cerr << "DEBUG " << __FILE__ << ":" << __LINE__ << ": "

#define ALCHEMY_KEY "3ab48469cc894436ef2ea8bf8f7bf76d8abb7cab"
#define TWITTER_ACCOUNT "vikings383:383vikings"

#define FAKE_SENTIMENT (false)

#define USE_SENTIMENT140 (true)

#define TWEET_CAP (2000)
#define AVG_FOLLOWERS (208)

// offset from GMT; -8 for Pacific Time
#define TIME_ZONE_OFFSET (-8)

#define CSV_OUTPUT (true)

#endif