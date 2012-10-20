#ifndef LOGGER_H
#define LOGGER_H

#include <iostream>

#ifndef DEBUG
	#define DEBUG false
#endif

#define LOGGER if(DEBUG) std::cerr

#endif