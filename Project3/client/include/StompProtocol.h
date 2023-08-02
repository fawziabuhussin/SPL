#pragma once
#include <iostream>
#include <vector>
#include <map>
#include <cstdio>
#include <fstream>
#include "../include/event.h"
#include "../include/ConnectionHandler.h"
#include "GameUser.h"
// TODO: implement the STOMP protocol
using std::string;

class StompProtocol
{
private:
    ConnectionHandler& connectionHandler;
    std::vector<std::string> command;
    bool& connectionStatus;
    std::string host;
    std::string portString;
    int nextSubId; //to be updated
    int nextReceipt; //to be updated
    std::string username;
    std::string password;
    std::map<std::string,int> topicsForId; // <game_name, subId>
    names_and_events nne;
    std::map<std::string, std::map<std::string, std::ofstream>> game_file;
    std::map<std::string, std::map<std::string, GameUser*>>& gameManager;
    int numOfEvents = 0;
    public:

    StompProtocol(ConnectionHandler& connectionHandler, std::vector<std::string> msg,
                  bool& connectionStatus , std::string host, std::string portString,
                  std::map<std::string, std::map<std::string, GameUser*>>& gameMana, int&len);
    std::string process(std::vector<std::string> msg);
    std::string login();
    std::string subscribe();
    std::string unsubscribe();
    std::string disconnect();
    std::string report();
    std::map<int,std::string> receiptForJoin; // <game_Name, ReceiptID>
    std::map<int,std::string> receiptForExit; // <game_Name, ReceiptID>
    void updateUser(string username, string password);
    bool gotMoreEvents();
    int& len;
    bool reported = false;

    string summary();
};
