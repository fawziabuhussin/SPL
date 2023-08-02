
#include "../include/StompFrame.h"
#include <map>
//#include "../src/event.cpp"
//#include <map>

StompFrame::StompFrame() : command(""), body(""), headers(){};

 std::string StompFrame::login(string username, string password)
{
    command = "CONNECT";
    headers["accept-version"] = "1.2";
    headers["host"] = "stomp.cs.bgu.ac.il";
    headers["login"] = username;
    headers["passcode"] = password;
    // body is empty.
    return this->toString();
}
 std::string StompFrame::logout(int receipt)
{
    command = "DISCONNECT";
    headers["receipt"] = std::to_string(receipt);

    // body is empty.
    return this->toString();
}

std::string StompFrame::join(string destination, int subId, int receipt)
{
    command = "SUBSCRIBE";
    headers["destination"] = "/" + destination;
    headers["id"] = std::to_string(subId);
    headers["receipt"] = std::to_string(receipt);
    // body is empty.
    return this->toString();
}

std::string StompFrame::exit(int subId, int receipt)
{
    command = "UNSUBSCRIBE";
    headers["id"] = std::to_string(subId);
    headers["receipt"] = std::to_string(receipt);
    // body is empty.
    return this->toString();
}
std::string getStringUpdate(std::map<string,string> updatesMap){
    string res;
    for (auto it = updatesMap.begin(); it != updatesMap.end(); it++) {
        // Get the key and value of the current element
        std::string key = it->first;
        std::string value = it->second;
        res += "\t" + key + ": " + value + "\n";
        // Print the key and value
    }
    return res;
 }
std::string StompFrame::report(string username, string destination,  Event file)
{
     command = "SEND";
     headers["destination"] = "/" + destination;
     std::string str[] = {"user: " + username,
                           "team a: " +  file.get_team_a_name(),
                           "team b: " +file.get_team_b_name(),
                           "event name: " + file.get_name(),
                           "time: " + std::to_string(file.get_time()) + "\n",
                           "general game updates: \n" + getStringUpdate(file.get_game_updates()),
                           "team a updates: \n"  + getStringUpdate(file.get_team_a_updates()),
                           "team b updates: \n" + getStringUpdate(file.get_team_b_updates()),
                           "description: " + file.get_discription()};
    bool jumpNow = false;
    for(string s: str){
        if(split(s,':')[0] == "time")
            jumpNow = true;
        if(jumpNow)
            body += s ;
        else
            body += s + "\n";
    }
    return this->toString();
}
std::string StompFrame::toString() {
    std::string frameSent = command + "\n";
    for (auto it = headers.begin(); it != headers.end(); it++) {
        // Get the key and value of the current element
        std::string key = it->first;
        std::string value = it->second;
        frameSent += key + ":" + value + "\n";
        // Print the key and value
    }

    bool jumpNow = false;
    if (!body.empty()) {
        frameSent += "\n" + body;
    }
//    frameSent += "\n" + body;
    return frameSent;
}

 std::vector<std::string> StompFrame::split(const std::string &str, char delimiter)
 {
     std::vector<std::string> tokens;
     std::string token;
     std::size_t start = 0, end = 0;
     while ((start = str.find_first_not_of(delimiter, end)) != std::string::npos)
     {
         end = str.find_first_of(delimiter, start);
         token = str.substr(start, end - start);
         tokens.push_back(token);
     }
     return tokens;
 }
