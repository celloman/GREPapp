#include <iostream>
#include <cstring>
#include <curl/curl.h>
#include <json/json.h>

using namespace std;

bool find_in_str(const char *needle, const char *haystack)
{
  // TODO: should only return true if the 'needle' (and any uppercase variations)
  // can be found in 'haystack'
  return true;
}

size_t callback(char *data, size_t size, size_t nmemb, void *usrdata)
{
  static int romney = 0, obama = 0;

  Json::Value root;
  Json::Reader reader;

  if(!reader.parse(data, root))
    cout << "JSON fail!\n";

  //cout << root["text"] << "\n";

  if(find_in_str(root["text"].asCString(), "obama"))
    obama++;
  if(find_in_str(root["text"].asCString(), "romney"))
    romney++;

  cout << "Romney: " << romney << ", Obama: " << obama << "\n";

  return size*nmemb;
}

int main(int argc, char **argv)
{
  CURL *curl;
  CURLcode res;

  curl = curl_easy_init();

  if(curl) {
    curl_easy_setopt(curl, CURLOPT_URL, "https://stream.twitter.com/1/statuses/filter.json");
    curl_easy_setopt(curl, CURLOPT_POST, 1);
    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, "track=obama,romney");
    curl_easy_setopt(curl, CURLOPT_USERPWD, "vikings383:383vikings");
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, callback);
    // Perform the request, res will get the return code
    res = curl_easy_perform(curl);
    // Check for errors
    if(res != CURLE_OK)
      fprintf(stderr, "curl_easy_perform() failed: %s\n", curl_easy_strerror(res));

    curl_easy_cleanup(curl);
  }

  return 0;
}
