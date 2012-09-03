#include <cstdio>
#include <cstdlib>
#include <curl/curl.h>

size_t callback(char *data, size_t size, size_t nmemb, void *usrdata)
{
  static int count = 0;
  count++;
  printf("%i:\n\n%s\n\n", count, data);

  if(count == 20)
    exit(0);

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
    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, "track=election");
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
