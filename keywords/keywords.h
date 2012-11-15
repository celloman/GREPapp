#ifndef KEYWORDS_H
#define KEYWORDS_H

#include <iostream>
#include <fstream>
#include <string>
#include <vector>

#include "config/config.h"

using namespace std;

namespace keywords {
	void load_conservative(vector<string> &keywords);
	void load_liberal(vector<string> &keywords);

	void load(char *filename, vector<string> &keywords);
}

#endif