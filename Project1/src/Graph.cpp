#include "Graph.h"


Graph::Graph(vector<Party> vertices, vector<vector<int>> edges) : mVertices(vertices), mEdges(edges),
mOffers(vector<vector<int>>(vertices.size(), vector<int>(vertices.size())))
{
    // You can change the implementation of the constructor, but not the signature!
}

int Graph::getMandates(int partyId) const
{
    return mVertices[partyId].getMandates();
}
int Graph::getOffer(int offeringParty, int offeredParty) const{
    return mOffers[offeringParty][offeredParty];
}
int Graph::getEdgeWeight(int v1, int v2) const{
    return mEdges[v1][v2];
}

int Graph::getNumVertices() const{
    return mVertices.size();
}

const Party &Graph::getParty(int partyId) const{
    return mVertices[partyId];
}

const vector<vector<int>> &Graph::getEdges() const {
    return mEdges;
}

// vector<Party>& Graph::getVertices() {
// return mVertices;
// }

Party &Graph::getParty(int partyId) {
    return mVertices[partyId];
}

void Graph::setOffer(int offeringParty, int offeredParty) {
    mOffers[offeringParty][offeredParty] = 1;
    mOffers[offeredParty][offeringParty] = 1; // Symetric matrix
}

vector<Party> & Graph::getVertices() {
    return mVertices;
}

/*
 * private members of the Graph:
    vector<Party> mVertices;
    vector<vector<int>> mEdges;
 */


