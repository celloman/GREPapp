#include <cstdlib>
#include <iostream>
#include <sstream>
#include <cstring>
#include <curl/curl.h>
#include <json/json.h>

using namespace std;

string buffer;

double str_to_double(string x){
  stringstream s;
  s << x;
  double res;
  s >> res;
  return res;
}

string to_lowercase(string text) //convert a string to lowercase
{
  for(int i = 0; i < text.length(); ++i)
    text[i] = tolower(text[i]);

  return text;
}

size_t write_data(char *data, size_t size, size_t nmemb, void *usrdata)
{
  buffer = "";
  buffer.append((char*) data, size * nmemb);
  return size * nmemb;
}

double get_sentiment(string text, string subject)
{
  CURL *curl;
  CURLcode res;

  curl = curl_easy_init();

  if(curl)
  {
    string url = "http://access.alchemyapi.com/calls/text/TextGetTargetedSentiment?apikey=3ab48469cc894436ef2ea8bf8f7bf76d8abb7cab&outputMode=json&text=";
    url += curl_easy_escape(curl, text.c_str(), text.length());
    url += "&target=";
    url += subject;

    curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_data);
    // Perform the request, res will get the return code
    res = curl_easy_perform(curl);
    // Check for errors
    if(res != CURLE_OK)
      fprintf(stderr, "curl_easy_perform() failed: %s\n", curl_easy_strerror(res));
    else
    {
      Json::Value root;
      Json::Reader reader;

      if(!reader.parse(buffer.c_str(), root) && root["status"].asString() == "ERROR")
        cout << "Sentiment JSON fail!\n";
      else
      {
        curl_easy_cleanup(curl);
        return str_to_double(root["docSentiment"]["score"].asString()) + 1;
      }
    }

    curl_easy_cleanup(curl);
  }
  return 1;
}

size_t callback(char *data, size_t size, size_t nmemb, void *usrdata)
{
  static double republican = 0, democrat = 0;
  double sentiment;
  Json::Value root;
  Json::Reader reader;

  if(!reader.parse(data, root))
    cout << "JSON fail!\n";

  //convert tweet to lowercase so lowercase keywords can be searched for
  string tweet_in_lowercase = to_lowercase(root["text"].asString());

  cout << tweet_in_lowercase << "\n";

  //TODO: once we add more keywords we may want to move this functionality to a separate function
  //search the lowercase tweet for the following strings
  if (string::npos != tweet_in_lowercase.find("obama"))
  {
    sentiment = get_sentiment(root["text"].asString(), "obama");
    democrat += sentiment;
  }
  if (string::npos != tweet_in_lowercase.find("romney"))
  {
    sentiment = get_sentiment(root["text"].asString(), "romney");
    republican += sentiment;
  }

  cout << "Romney: " << republican << ", Obama: " << democrat << "\n";

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
