#pragma once

#include <vector>

#include "Graph.h"
#include "Agent.h"

using std::string;
using std::vector;

class Simulation
{
public:
    Simulation(Graph g, vector<Agent> agents);
    bool shouldTerminate() const;
    const Graph &getGraph() const;
    Graph &getGraph();
    const vector<Agent> &getAgents() const;
    vector<Agent> &getAgents();
    const Party &getParty(int partyId) const;
    /// Should Implement ///
    void step();
    const vector<vector<int>> getPartiesByCoalitions() const;
    /// NEW METHODS ///
    void setCoalition(vector<int> newCoal, int coalId);
    vector<int>& getCoalition(int coalId);
    void setMandatesForCoalition(int coalId, int newMandateNum);
    int getMandatesForCoalition(int coalId);
    int getNextAgentId();
    void addAgent(Agent newAgent);

private:
    Graph mGraph;
    vector<Agent> mAgents;
    /// NEW MEMBERS ///
    vector<vector<int>> coalitions;
    vector<int> mandates_;
    int nextFreeAgentId;
};
