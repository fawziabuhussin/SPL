#pragma once

#include "Graph.h"

class JoinPolicy {
public:
    virtual int join(Graph& mGraph, int myParty) = 0;
    virtual string getType() = 0;
    virtual ~JoinPolicy();
    virtual JoinPolicy* constructJoinPolicy() = 0;
};

class MandatesJoinPolicy : public JoinPolicy {
public:
    MandatesJoinPolicy() = default;
    string getType() override;
    int join(Graph& mGraph, int myParty) override;
    JoinPolicy* constructJoinPolicy();

};

class LastOfferJoinPolicy : public JoinPolicy {
public:
    LastOfferJoinPolicy() = default;
    string getType() override;
    int join(Graph& mGraph, int myParty) override;
    JoinPolicy* constructJoinPolicy();

};