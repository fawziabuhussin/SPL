#pragma once

#include <thread>
#include <mutex>
#include <condition_variable>
#include "../include/ConnectionHandler.h"
#include "StompProtocol.h"
#include "GameUser.h"


class InputTask {
private:
    ConnectionHandler& connectionHandler;
    StompProtocol& protocol;
    bool& connected ;
    std::vector<std::string> command;
    std::map<std::string, std::map<std::string, GameUser*>>& gameManager; // <user_name, gameManager>

    int& threadAdder; // reseting the thread using it.
public:
    InputTask(ConnectionHandler& cHan, StompProtocol& pro, bool &connected,
              std::map<std::string, std::map<std::string, GameUser*>>& gamMan, int& threadAdder);
    void setCommand(std::vector<std::string>);
    void run();
    bool shouldTerminate();
    std::vector<std::string> split(const std::string &str, char delimiter);
    void process(std::string frame);



};
