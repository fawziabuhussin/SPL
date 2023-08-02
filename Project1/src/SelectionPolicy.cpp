#include "SelectionPolicy.h"

string MandatesSelectionPolicy::getType() {
    return "M";
}

int MandatesSelectionPolicy::select(Graph &mGraph, int partyId) {
    vector<int> myNeighbors = mGraph.getEdges()[partyId];
    int maxMandates = 0;
    int maxId = -1;
    int neighborsSize = myNeighbors.size();
    for (int i = 0; i < neighborsSize; i++) { // Will check if there is a party to choose.
        Party &current = mGraph.getParty(i); // get party with ID myNeighbors[i].
        State partyState = current.getState();
        if (myNeighbors[i] != 0 && partyState != State::Joined) { // there is Edge
            int offerStatus = mGraph.getOffer(partyId, i); // getOffer is 2D Matrix for the offers of the parties.
            if (offerStatus == 0) // Coalition has not given an offer yet.
            {
                if (current.getMandates() > maxMandates) {
                    maxMandates = current.getMandates();
                    maxId = i;
                }
            }
        }
    }
    return maxId;
}

SelectionPolicy* MandatesSelectionPolicy::constructSelectionPolicy(){ // clone the selection policy.
    return new MandatesSelectionPolicy();
};



int EdgeWeightSelectionPolicy::select(Graph &mGraph, int partyId) {
    vector<int> myNeighbors = mGraph.getEdges()[partyId];
    int maxEdge = 0;
    int maxId = -1;
    int neighborsSize = myNeighbors.size();
    for (int i = 0; i < neighborsSize; i++) { // Will check if there is a party to choose.
        Party &current = mGraph.getParty(i); // get party with ID myNeighbors[i].
        State partyState = current.getState();
        if (myNeighbors[i] != 0 && partyState != State::Joined) { // there is Edge
            int offerStatus = mGraph.getOffer(partyId, i); // getOffer is 2D Matrix for the offers of the parties.
            if (offerStatus == 0) // Coalition has not given an offer yet.
            {
                if (mGraph.getEdgeWeight(partyId, i) > maxEdge) {
                    maxEdge = mGraph.getEdgeWeight(partyId, i);
                    maxId = i;
                }
            }
        }
    }
    return maxId;
}
SelectionPolicy* EdgeWeightSelectionPolicy::constructSelectionPolicy(){
    return new EdgeWeightSelectionPolicy();
};


string EdgeWeightSelectionPolicy::getType() {
    return "E";
}

SelectionPolicy::~SelectionPolicy() = default;
