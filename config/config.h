#ifndef CONFIG_H
#define CONFIG_H

#include <iostream>

#ifndef DEBUG
	#define DEBUG false
#endif

#ifndef INFO
	#define INFO false
#endif

#define ERROR_LOG std::cerr
#define INFO_LOG if(INFO) std::cerr
#define DEBUG_LOG if(DEBUG) std::cerr

#define ALCHEMY_KEY "3ab48469cc894436ef2ea8bf8f7bf76d8abb7cab"
#define TWITTER_ACCOUNT "vikings383:383vikings"

#define FAKE_SENTIMENT true

#define TWEET_CAP 200
#define AVG_FOLLOWERS 208

#endif