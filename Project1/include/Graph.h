#pragma once
#include <vector>
#include "Party.h"

using std::vector;

class Graph
{
public:
    Graph(vector<Party> vertices, vector<vector<int>> edges);
    int getMandates(int partyId) const;
    int getEdgeWeight(int v1, int v2) const;
    int getNumVertices() const;
    const Party &getParty(int partyId) const;

    /// NEW METHODS ///
    Party &getParty(int partyId);
    const vector<vector<int>>& getEdges() const;
    vector<Party>& getVertices();
    int getOffer(int offeringParty, int offeredParty) const;
    void setOffer(int offeringParty, int offeredParty);

private:
    vector<Party> mVertices;
    vector<vector<int>> mEdges;
    vector<vector<int>> mOffers;
};
