#include "../include/InputTask.h"

#include <vector>
#include <sstream>
#include <thread>
#include <map>
#include <chrono>
#include <ctime>
using std::cout;
// TODO: implement the STOMP client
/*
 ******* Client will have two threads. *******
 * the main function and another thread that will process the output that the server sends to the client.
 * first the client will check if we send login {port} {username} {password}
 * if yes, it will connect and will make a thread that will read.
 * the main function will repeatedly get lines from the client and sends to the server.
 * the second thread will use function process in StompProtocol.h, and will send message, or it will save a message(Summary/Report).
 */

int main(int argc, char *argv[]) {
    bool connected = false;
    std::string username = "";
    std::string password = "";
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl
                  << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    std::string portString = argv[2];
    std::map<std::string, std::map<std::string, GameUser*>> GameAndUsers; // <gameName : < Username, GameUser>

    int len = 0;
    const short bufsize = 1024;
    std::vector<std::string> strSplited;
    ConnectionHandler connectionHandler(host, port);
    int ThreadAdder = 0;
    StompProtocol processFrame(connectionHandler, strSplited, connected, host, portString, GameAndUsers, ThreadAdder);
    InputTask inputManager(connectionHandler, processFrame, connected, GameAndUsers, ThreadAdder);
    std::thread thd;
    std::map<std::string, std::string> join_receipt; // receipt_join
    std::map<std::string, std::string> exit_receipt; // receipt_e
    // checks if we are right.
    while (1) {
        if(connected && ThreadAdder == 0) {
            thd = std::thread(&InputTask::run, &inputManager);
            ThreadAdder++;
        }
        char buf[bufsize];
        if(!processFrame.gotMoreEvents() || !connected) {
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            strSplited = inputManager.split(line, ' ');
        }

        std::string processed = processFrame.process(strSplited); // will return the frame.
        if (processed != "no output" && processed!= "InValid command") {
            inputManager.setCommand(strSplited);
                if (!connectionHandler.sendLine(processed)) {
                    std::cout << "Disconnected. Exiting...\n" << std::endl;
                    break;
                }
        }
    }
}
