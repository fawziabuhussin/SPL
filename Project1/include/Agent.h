#pragma once

#include "SelectionPolicy.h"
#include <vector>
#include "Graph.h"

class SelectionPolicy;

class Agent {
public:
    Agent(int agentId, int partyId, SelectionPolicy *selectionPolicy); // Constructor
    int getPartyId() const;
    int getId() const;

    /// RULE OF 5 ///
    ~Agent(); // Destructor'
    Agent(const Agent &other) noexcept; // Copy constructor
    Agent &operator=(const Agent &other); // operator=
    Agent &operator=(Agent &&other) noexcept;  // move assignment.
    Agent(Agent &&other); // move constructor

    /// TO BE IMPLEMENTED ///
    void step(Simulation &);

    /// NEW METHODS. ///
    void link(const Agent &other);
    string getSelectionType();
    SelectionPolicy* constructPolicy();


private:
    int mAgentId;
    int mPartyId;
    SelectionPolicy *mSelectionPolicy;
};
