#include <cstdio>
#include "../include/StompProtocol.h"
#include "../include/StompFrame.h"

std::vector<std::string> split(const std::string &str, char delimiter)
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

StompProtocol::StompProtocol(ConnectionHandler& cH, std::vector<std::string> stdSplited,
                             bool& connection , std::string ht, std::string port,
                             std::map<std::string, std::map<std::string, GameUser*>>& gameMana, int& threadAdder):
                             connectionHandler(cH), command(stdSplited),
                             connectionStatus(connection),
                             host(ht),portString(std::move(port)), nextSubId(0), nextReceipt(0),
                             username(), password(), topicsForId(),
                             nne(), game_file(),
                             gameManager(gameMana),
                             receiptForJoin(), receiptForExit(), len(threadAdder){}
string StompProtocol::process(std::vector<std::string> msg)
{
    command = msg;
    std::string res = "process didn't recognize the input";
    int len = command.size();
    if (len == 0)
        return "process didn't recognize the input";
    if (len >= 1 && command[0] == "login") // if he tried to login and he is connected.
    {
        res = login();
    }
    else {
        if (connectionStatus) {
            if (len == 2 && command[0] == "join") {
                res = subscribe();
            } else if (len == 2 && command[0] == "exit") {
                res = unsubscribe();
            } else if (len == 1 && command[0] == "logout") {
                res = disconnect();
            } else if (len == 2 && command[0] == "report") {
                res = report();
            } else if (len == 4 && command[0] == "summary") {
                res = summary();

            } else {
                std::cout << "Could not recognize your input, please try again.\n";
                res = "InValid command";
            }
        } else {
            std::cout << "Client is not connected, please login. \n";
            res = "InValid command";
        }
    }
    return res;
}

string StompProtocol::login()
{
    if(!connectionStatus) // not connected.
    {
        const short bufsize = 1024;
        std::string commandmsg = command[0];
        while (((commandmsg != "login")) || (command.size() != 4) || (command[1] != (host + ":" + portString)))
        { // this loop checks if we get login port username passcode.
            std::cout << "Welcome, Login before you try to excuse any command\n";
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            command = split(line, ' ');
            commandmsg = command[0];
        }
        if (!connectionHandler.connect()) {
            std::cerr << "Cannot connect to " << host << ":" << portString << std::endl;
            return "Connection failed";
        }
//        len = 0;
        connectionStatus = true;
        username = command[2];
        password = command[3];
        return  StompFrame().login(username, password);

    }

    else {
        std::cout << "The client is already logged in, log out before trying again \n";
        return "no output";
    }
}



string StompProtocol::subscribe()
{
    std::string res;
    //checks if the game name is legal
    std::vector<std::string> gameEvent = split(command[1], '_');
    if(gameEvent.size() != 2){
        return "no output";
    }
    else {
        std::string gameName = command[1];
        topicsForId[gameName] = nextSubId;
        receiptForJoin[nextReceipt] = gameName;
        return StompFrame().join(gameName,nextSubId++, nextReceipt++);
    }
}



string StompProtocol::unsubscribe()
{
    std::string gameName = command[1];
    int subId;
    for (auto it = topicsForId.begin(); it != topicsForId.end(); it++)
    {
        // Get the key and value of the current element
        std::string key = it->first;
        if(key == gameName)
            subId = it->second;
    }
    receiptForExit[nextReceipt] = gameName;
    return StompFrame().exit(subId, nextReceipt++);
}



string StompProtocol::disconnect(){
    connectionStatus = false;
    return StompFrame().logout(nextReceipt++);
}



string StompProtocol::report(){
    if(!reported) {
        std::string str = "./data/" + command[1];
        nne = parseEventsFile(str);
        reported = true;
    }
    // to check the end of the file that we have.
    string gameName = nne.team_a_name + "_" + nne.team_b_name;
    return StompFrame().report(username, gameName ,nne.events[numOfEvents++]);
}



void StompProtocol::updateUser(string name, string passcode){
    username = name;
    password = passcode;
}
bool StompProtocol:: gotMoreEvents(){
    if(nne.events.size() == numOfEvents) {
        reported = false;
        numOfEvents = 0;
    }
    return reported;
}

string StompProtocol::summary() {
    string username = " " + command[2];
    string game_name = command[1];
    string file_name = command[3];

    string file = command[3];
    if(game_file[game_name].count(username) == 0){
        game_file[game_name][username] = std::ofstream(file_name + ".txt");
    }
    else {
        const char* fileName = (file_name + ".txt").c_str();
        remove(fileName);
        game_file[game_name][username] = std::ofstream(file_name + ".txt");

    }
    GameUser* gu = gameManager[game_name][username];
    if(gu!= nullptr) {
        game_file[game_name][username] << gu->parseToString();
    }
    game_file[game_name][username].close();
    return "no output";
}

