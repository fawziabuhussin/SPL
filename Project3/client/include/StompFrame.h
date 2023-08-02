#pragma once

#include "../include/event.h"

#include <string>
#include <map>
#include <vector>

using std::string;

// TODO: implement the STOMP protocol
class StompFrame
{
private:
    string command;
    std::string body;
    std::map<std::string, std::string> headers;

public:
    StompFrame();
    string login(string username, string password);
    string logout(int receipt);
    string join(string destination, int subId, int receipt);
    string exit(int subid, int receipt);
    string toString();
    string report(string username ,string destination, Event file);
    std::vector<string> split(const string &str, char delimiter);

};
