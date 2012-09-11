#include <iostream>
#include <cstring>
#include <curl/curl.h>
#include <json/json.h>

using namespace std;

string to_lowercase(string text) //convert a string to lowercase
{
  for(int i = 0; i < text.length(); ++i)
    text[i] = tolower(text[i]);

  return text;
}

size_t callback(char *data, size_t size, size_t nmemb, void *usrdata)
{
  static int republican = 0, democrat = 0;

  Json::Value root;
  Json::Reader reader;

  if(!reader.parse(data, root))
    cout << "JSON fail!\n";

  cout << root["text"];
  
  //convert tweet to lowercase so lowercase keywords can be searched for
  string tweet_in_lowercase = to_lowercase(root["text"].asString());
  
  cout << tweet_in_lowercase << endl; 

  //TODO: once we add more keywords we may want to move this functionality to a separate function
  //search the lowercase tweet for the following strings
  if (string::npos != tweet_in_lowercase.find("obama"))
    democrat++;
  if (string::npos != tweet_in_lowercase.find("romney"))
    republican++;

  cout << "Romney: " << republican << ", Obama: " << democrat << "\n\n";

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
