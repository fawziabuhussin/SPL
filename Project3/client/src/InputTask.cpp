
#include "../include/InputTask.h"


InputTask::InputTask(ConnectionHandler& cHan, StompProtocol& pro, bool &cs,
                     std::map<std::string, std::map<std::string, GameUser*>>& gamMan,  int& threadAdder) :
                     connectionHandler(cHan), protocol(pro), connected(cs), command(), threadAdder(threadAdder), gameManager(gamMan){}
void InputTask::run(){
        while(1)
        {
            std::string answer;
            if(connected) {
                if (!connectionHandler.getLine(answer)) {
                    std::cout << "INPUT: GET line Disconnected. Exiting...\n" << std::endl;
                }
                if(!answer.empty()){
                    process(answer);

                    int len = answer.length();
                    answer.resize(len-1);
                    if ((command.size() == 1 && command[0] == "logout") ||
                        !(answer.empty()) && (split(answer, '\n')[0] == "ERROR")) {
                        connectionHandler.close();
                        connected = false;
                    }
                }
            }
        }
};
void InputTask::setCommand(std::vector<std::string> cmd){
    this->command = cmd;
};
bool InputTask::shouldTerminate(){
    return connected;
}

std::vector<std::string> InputTask::split(const std::string &str, char delimiter)
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
void InputTask::process(std::string frame){
    std::vector<std::string> str = split(frame,'\n');
    if(str[0] == "ERROR"){
        if(str.size() == 2)
            std::cout << split(str[1],':')[1] << "\n"; // print the error message.
        if(str.size() == 3)
            std::cout << split(str[2],':')[1] << "\n"; // print the error message.
    }
    else if(str[0] == "CONNECTED"){
        std::cout << "Login Successful\n";
    }
    else if(str[0] =="RECEIPT"){
        std::string recID = split(str[1],':')[1];
        if(protocol.receiptForJoin.count(std::stoi(recID)) == 1){ // join
            std::string game_name = protocol.receiptForJoin[std::stoi(recID)];
            protocol.receiptForJoin.erase(std::stoi(recID));
            std::cout << "Joined channel " + game_name + "\n" ;
        }else if (protocol.receiptForExit.count(std::stoi(recID)) == 1){ // exit
            std::string game_name = protocol.receiptForExit[std::stoi(recID)];
            protocol.receiptForExit.erase(std::stoi(recID));
            std::cout << "Exited channel " + game_name + "\n";
        }
        else { // logout
            std::cout << "logout Successful\n";
        }
    }
    else if(str[0] == "MESSAGE"){
        int indexOf = frame.find("\n\n");
        std::string msg = frame.substr(indexOf+2, frame.length()); // TODO
        std::string game_name = split(str[3], ':')[1];
        std::string user = split(str[4], ':')[1];

        if(gameManager[game_name].count(user) == 0) {
            GameUser* gu = new GameUser(game_name);
            gameManager[game_name][user] =  gu;
        }
        gameManager[game_name][user]->addMessage(msg);

    }
}




