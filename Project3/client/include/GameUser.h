#pragma once
#include <string>
#include <map>
#include <vector>
#include <iostream>
using std::cout;
using std::string;
class GameUser {
private:

public:

    string team_a_name;
    string team_b_name;
    std::string time;
    std::string eventName;
    std::map<std::string,std::string> general_stats;
    std::map<std::string,std::string> team_a_stats;
    std::map<std::string,std::string> team_b_stats;
    std::map<std::string,std::string> Game_event_reports;
    GameUser(std::string game_name);
    void addMessage(std::string msg);
    std::vector<std::string> split(const std::string &str, char delimiter);
    std::string parseToString();
    std::string parseString(std::map<std::string,std::string>& mapped);


};


