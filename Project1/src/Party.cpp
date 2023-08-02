#include "Party.h"

#include "JoinPolicy.h"
#include "Simulation.h"

Party::Party(int id, string name, int mandates, JoinPolicy *jp) : mId(id), mName(name), mMandates(mandates), mJoinPolicy(jp), mState(Waiting)
,iterNum(0), coalId(id), lastOffer(-1){}
void Party::link(const Party& other){
    this->mId = other.mId;
    this->mMandates = other.mMandates;
    this->mName = other.mName;
    this->mState = other.mState;
    this->iterNum = other.iterNum;
    this->coalId = other.coalId;
    this->lastOffer = other.lastOffer;
}
Party::Party(const Party &other)
        : mId(other.mId), mName(other.mName), mMandates(other.mMandates), mJoinPolicy(other.mJoinPolicy), mState(other.mState)
       ,iterNum(other.iterNum), coalId(other.coalId), lastOffer(other.lastOffer)
      { // copy constructor
        mJoinPolicy= other.mJoinPolicy->constructJoinPolicy();

}

Party &Party::operator=(const Party &other) { // assignment operator
    if(this!= &other) {
        link(other);
        mJoinPolicy= other.mJoinPolicy->constructJoinPolicy();
    }
    return *this;
}

Party &Party::operator=(Party &&other) noexcept  { // move assignment
    link(other);
    mJoinPolicy = (other.mJoinPolicy); 
    other.mJoinPolicy = nullptr;
    return *this;
}

Party::Party(Party &&other)  : mId(other.mId), mName(other.mName), mMandates(other.mMandates),mJoinPolicy(other.mJoinPolicy), mState(other.mState)
        ,iterNum(other.iterNum), coalId(other.coalId), lastOffer( other.lastOffer) { // move constructor

    other.mJoinPolicy = nullptr;
}

Party::~Party() { // destructor.
   if(mJoinPolicy) delete(mJoinPolicy); 
}


State Party::getState() const
{
    return mState;
}

void Party::setState(State state)
{
    mState = state;
}

int Party::getMandates() const
{
    return mMandates;
}

const string & Party::getName() const
{
    return mName;
}

void Party::step(Simulation &s)
{
    // i = iteration.
    // i = 0; this method will check the mState and will change it accordingly.
    // i = 0; Waiting->CollectingOffer. Is done by Agent->mSelectionPolicy::select(..) method.
    // 0 > i < 4 and will start a timer and each iteration will update the timer.
    // i = 4 after three iterations, the mState will be Joined.
    // i = 4 agent with the joined coalition will be our agent.
    Graph& mGraph = s.getGraph();
    if(mState == CollectingOffers && iterNum!=3)
        iterNum++;
    else if(iterNum == 3) { // Join now.
        ///
        /// * If Joined:
        /// * 1) update the status.
        /// * 2) Clone the Agent and update its id with the new free agent Id.
        /// * 3) Add the party to the coalition, update the coalition[coalId][mId] = 1.
        /// * 4) Update the coalitions vector. mandates_[mId] = mandates_[mId] + mandates.
        ///
        mState = Joined;
        // clone the agent to the agent vector in simulation.
        coalId = mGraph.getParty(mJoinPolicy->join(mGraph, mId)).getCoalition();
        vector<Agent> vAgents = s.getAgents();
        string type = vAgents[coalId].getSelectionType();
        vector<int> temp = s.getCoalition(coalId);
        temp.push_back(mId);
        s.setCoalition(temp,coalId);
        s.setMandatesForCoalition(coalId, s.getMandatesForCoalition(coalId) + mMandates);
        s.addAgent(Agent(s.getNextAgentId(),mId,vAgents[coalId].constructPolicy()));
        iterNum++;
    } // if we still in CollectingOffers, iterNum++.

}

void Party::setLastOffer(int pId) {
    lastOffer = pId;
}

int Party::getLastOffer() const {
    return lastOffer;
}

void Party::addIter() {
    iterNum++;
}

void Party::setCoalition(int coalId) {
    this->coalId = coalId;
}

int Party::getCoalition() {
    return coalId;
}
/*
 * the members of class Party:
    int mId;
    string mName;
    int mMandates;
    JoinPolicy *mJoinPolicy;
    State mState;
 */
