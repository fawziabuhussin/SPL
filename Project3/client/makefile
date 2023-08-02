CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lpthread

all: StompWCIClient

EchoClient: bin/ConnectionHandler.o bin/echoClient.o
	g++ -o bin/EchoClient bin/ConnectionHandler.o bin/echoClient.o $(LDFLAGS)

StompWCIClient: bin/ConnectionHandler.o bin/StompClient.o bin/event.o bin/StompFrame.o bin/StompProtocol.o bin/InputTask.o bin/GameUser.o
	g++ -o bin/StompWCIClient bin/ConnectionHandler.o bin/StompClient.o bin/event.o bin/StompFrame.o bin/StompProtocol.o bin/InputTask.o bin/GameUser.o $(LDFLAGS)

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/event.o: src/event.cpp
	g++ $(CFLAGS) -o bin/event.o src/event.cpp

bin/StompClient.o: src/StompClient.cpp
	g++ $(CFLAGS) -o bin/StompClient.o src/StompClient.cpp 

bin/StompProtocol.o: src/StompProtocol.cpp
	g++ $(CFLAGS) -o bin/StompProtocol.o src/StompProtocol.cpp

bin/StompFrame.o: src/StompFrame.cpp
	g++ $(CFLAGS) -o bin/StompFrame.o src/StompFrame.cpp

bin/InputTask.o: src/InputTask.cpp
	g++ $(CFLAGS) -o bin/InputTask.o src/InputTask.cpp


bin/GameUser.o: src/GameUser.cpp
	g++ $(CFLAGS) -o bin/GameUser.o src/GameUser.cpp


.PHONY: clean
clean:
	rm -f bin/*
	