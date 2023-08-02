//
// Created by spl211 on 1/12/23.
//
#include "../include/GameUser.h"
GameUser::GameUser(std::string game_name) :
team_a_name(split(game_name,'_')[0]), team_b_name(split(game_name,'_')[1])
, eventName(), general_stats(), team_a_stats(), team_b_stats(), Game_event_reports(){
    time = "";
    team_a_name = split(game_name,'_')[0];
    team_b_name = split(game_name,'_')[1];
}


void GameUser::addMessage(std::string msg) {
    std::vector<std::string> splits = split(msg, '\n');
    bool generUp = false;
    bool teamAUp = false;
    bool teamBUp = false;

    for (string s: splits) {
        string flag = split(s, ':')[0];
        string content = split(s, ':')[1];
        if (flag == "time") {
            this->time = split(s, ':')[1];
        } else if (flag == "event name") {
            this->eventName = split(s, ':')[1];
        } else if (flag == "general game updates") {
            generUp = true;
        } else if (flag == "team a updates") {
            teamAUp = true;
        } else if (flag == "team b updates") {
            teamBUp = true;
        } else if (flag == "description") {
            std::string key = split(time,' ')[0] + " - " + eventName;
            std::string value = "\n\n" + content.substr(1) + "\n\n";
            this->Game_event_reports[key] = value;
        } else if (teamBUp == true && teamAUp == true) { // team b stats.
            this->team_b_stats[flag] = content;
        } else if (generUp == true && teamAUp == false) { // general stats
            this->general_stats[flag] = content;
        } else if (generUp == true && teamAUp == true) { // team a stats.
            this->team_a_stats[flag] = content;
        } else {
        }
    }

}
std::string GameUser::parseToString() {
        std::string retValue = "";
        retValue += team_a_name + " vs " + team_b_name + "\n";
        retValue += "Game stats:\n" + parseString(general_stats);
        retValue += team_a_name + " stats:\n"+ parseString(team_a_stats);
        retValue += team_b_name + " stats:\n"+ parseString(team_b_stats);
        retValue += "Game event reports:\n" + parseString(Game_event_reports);

        return retValue;
}

    std::string GameUser::parseString(std::map<std::string,std::string>& mapped){
        string res = "";
        for (auto it = mapped.begin(); it != mapped.end(); it++) {
            std::string key = it->first;
            std::string value = it->second;
            res += split(key,'\t')[0] + ":" + value + "\n";
        }
        return res;
    }


std::vector<std::string> GameUser::split(const std::string &str, char delimiter)
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
