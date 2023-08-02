#include "Agent.h"
#include "Simulation.h"

Agent::Agent(int agentId, int partyId, SelectionPolicy *selectionPolicy) : mAgentId(agentId), mPartyId(partyId), mSelectionPolicy(selectionPolicy) // Constructor
{}
void Agent::link(const Agent& other){
    this->mAgentId = other.mAgentId;
    this->mPartyId = other.mPartyId;
}

Agent& Agent::operator=(const Agent& other){ // Assignment operator
    if(this!= &other){
        link(other);
        mSelectionPolicy= other.mSelectionPolicy->constructSelectionPolicy();
    }
    return *this;
}
Agent::Agent(const Agent& other) noexcept : mAgentId(other.mAgentId), mPartyId(other.mPartyId)
, mSelectionPolicy(other.mSelectionPolicy)
{ // Copy constructor
        mSelectionPolicy= other.mSelectionPolicy->constructSelectionPolicy();

}

Agent& Agent::operator=(Agent&& other) noexcept { // Move assignment
    link(other);
    mSelectionPolicy = (other.mSelectionPolicy); 
    other.mSelectionPolicy = nullptr;
    return *this;
}
Agent::~Agent() {
    delete(mSelectionPolicy);
} // Destructor

Agent::Agent(Agent&& other) : mAgentId(other.mAgentId), mPartyId(other.mPartyId), mSelectionPolicy(other.mSelectionPolicy){ // move constructor
    other.mSelectionPolicy = nullptr;

}
int Agent::getId() const
{
    return mAgentId;
}

int Agent::getPartyId() const
{
    return mPartyId;
}

void Agent::step(Simulation &sim)
{
    // Agent will run over all the edges and will check each vertice if it is in waiting/recieving offer states.
    // this will be done using Agent.SelectionPolicy::select().
    // if yes, it will give it offer if it does complete the terms of the selectionpolicy(private member)
    // if it does not, either complete the term of selectionpolicy or it is in joined state it won't offer it to join the coalition.
    Graph& mGraph = sim.getGraph();
    int partySelect = mSelectionPolicy->select(mGraph,getPartyId());
    // Party check:
    // We will check if the party state is currently available:
    // means, no one of the agents has given an offer before.
    if(partySelect > -1) {
        Party& choosedParty = mGraph.getParty(partySelect); // p is not in Joined state (checked in select)
        if (choosedParty.getState() == State::Waiting) {
            choosedParty.setState(State::CollectingOffers);
            choosedParty.addIter();
        }
        choosedParty.setLastOffer(mPartyId);
        mGraph.setOffer(partySelect,mPartyId);

    }

}
SelectionPolicy* Agent::constructPolicy(){
    return mSelectionPolicy->constructSelectionPolicy();

}

string Agent::getSelectionType() {
    return mSelectionPolicy->getType();
}

