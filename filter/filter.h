#ifndef FILTER_H
#define FILTER_H

#include <string>
#include <deque>
#include <stdint.h>

#include "keywords/keywords.h"
#include "config/config.h"

using namespace std;

namespace filter {
	bool has_one_keyword(string text);
	bool in_deque(int64_t id, deque<int64_t> &ids);
	void store_in_deque(int64_t id, deque<int64_t> &ids);
}

#endif