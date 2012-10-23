#ifndef LOGGER_H
#define LOGGER_H

#include <iostream>

#ifndef DEBUG
	#define DEBUG false
#endif

#define ERROR_LOG std::cerr
#define INFO_LOG if(DEBUG) std::cerr

#endif