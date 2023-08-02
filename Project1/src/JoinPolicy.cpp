#include "JoinPolicy.h"

int MandatesJoinPolicy::join(Graph &mGraph, int myParty) { // myParty represents the current party we used Party::step(..) on it.
    // This function will override the join() function in JoinPolicy, which will run over all
    // parties and check the highest mandates and will Join it.
    int partyId = -1;
    int maxMandates = 0;
    for(int i = 0 ; i < mGraph.getNumVertices(); i++){
        int offerStatus = mGraph.getOffer(myParty, i);
        // getOffer method enters the matrix of offers.
        // each party has vertex of current offers. If there is offer of party "i" mOffer[myParty][i] == 1, otherwise,
        // mOffers[myParty][i] == 0.
        if(offerStatus == 1) {
            int curMandate = mGraph.getParty(i).getMandates();
            if (curMandate > maxMandates) {
                partyId = i;
                maxMandates = curMandate;
            }
        }
    }

    return partyId;
}

JoinPolicy* MandatesJoinPolicy::constructJoinPolicy() {
    return new MandatesJoinPolicy();
}

int LastOfferJoinPolicy::join(Graph& mGraph, int myParty)     { // enters only when iter == 3
    // This function will override the join() function in JoinPolicy, which will run over all
    // parties and check the highest mandates and will Join it.
    return mGraph.getParty(myParty).getLastOffer();
}
string MandatesJoinPolicy::getType() {
    return "M";
}

string LastOfferJoinPolicy::getType() {
    return "L";
}

JoinPolicy* LastOfferJoinPolicy::constructJoinPolicy() { // clone the policy.
    return new LastOfferJoinPolicy();
}

JoinPolicy::~JoinPolicy() = default;