#include "Simulation.h"

Simulation::Simulation(Graph graph, vector<Agent> agents) : mGraph(graph), mAgents(agents),
    coalitions( vector<vector<int>>(agents.size(), vector<int>(1))),mandates_(vector<int>(agents.size())),
    nextFreeAgentId(agents.size()) {
    int sizeOfAgents = agents.size();
    for (int i = 0; i < sizeOfAgents; i++) {
        int partyIdTemp = mAgents[i].getPartyId();
        mGraph.getParty(partyIdTemp).setCoalition(i);
    }
    // [[agent0.partyId], [agent1.partyId], ...]
    int sizeOfAgent = agents.size();
    for (int i = 0; i < sizeOfAgent; i++) {
        coalitions[i][0] = agents[i].getPartyId();
        mandates_[i] = graph.getMandates(agents[i].getPartyId());
    }

}

void Simulation::step() {
    // This method will run over the vertices and will call method party step.
    // then it will run over the agents vertex and will call step method .
    for (int i = 0; i < mGraph.getNumVertices(); i++) {
        Party &party = mGraph.getParty(i); 
        party.step(*this);
    }
    int sizeOfAgentsVector = mAgents.size();
    for (int i = 0; i < sizeOfAgentsVector; i++) {
        mAgents[i].step(*this);
    }

}

bool Simulation::shouldTerminate() const {
    // this method will return true if:
    // 1- All the parties are in Joined state.
    // 2- Or one of the parties has 61 mandates.
    int sizeOfVectorMandates = mandates_.size();
    for (int i = 0; i < sizeOfVectorMandates; i++) {
        if (mandates_[i] > 60)
            return true;
    }
    int numOfVertices = mGraph.getNumVertices();
    if (nextFreeAgentId == numOfVertices) {
        return true;
    }
    for (int i = 0; i < numOfVertices; i++) {
        if (getParty(i).getState() != Joined)
            return false;
    }

    return false;
}

const Graph &Simulation::getGraph() const {
    return mGraph;
}

Graph &Simulation::getGraph() {
    return mGraph;
}

const vector<Agent> &Simulation::getAgents() const {
    return mAgents;
}

const Party &Simulation::getParty(int partyId) const {
    return mGraph.getParty(partyId);
}

/// This method returns a "coalition" vector, where each element is a vector of party IDs in the coalition.
/// At the simulation initialization - the result will be [[agent0.partyId], [agent1.partyId], ...]
const vector<vector<int>> Simulation::getPartiesByCoalitions() const {
    // returns a 2D matrix that each row represents a coalition of parties. (
    return coalitions;
}

vector<Agent> &Simulation::getAgents() {
    return mAgents;
}

void Simulation::setCoalition(vector<int> newCoal, int coalId) {
    coalitions[coalId] = newCoal;
}

vector<int> &Simulation::getCoalition(int coalId) {
    return coalitions[coalId];
}

void Simulation::setMandatesForCoalition(int coalId, int newMandateNum) {
    mandates_[coalId] = newMandateNum;
}

int Simulation::getMandatesForCoalition(int coalId) {
    return mandates_[coalId];
}

int Simulation::getNextAgentId() {
    return nextFreeAgentId++; // add once you return.
}

void Simulation::addAgent(Agent newAgent) {
    mAgents.push_back(newAgent);
}

