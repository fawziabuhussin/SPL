#pragma once
#include "Graph.h"

class SelectionPolicy {
public:
    virtual int select(Graph& mGraph, int partyId) = 0 ;
    virtual string getType() = 0;
    virtual ~SelectionPolicy();
    virtual SelectionPolicy* constructSelectionPolicy() = 0;

};

class MandatesSelectionPolicy: public SelectionPolicy {
public:

    MandatesSelectionPolicy() = default;
    string getType() override ;
    int select(Graph &mGraph, int partyId) override ;
    SelectionPolicy* constructSelectionPolicy();

};
class EdgeWeightSelectionPolicy: public SelectionPolicy{
public:

    EdgeWeightSelectionPolicy() = default;
    string getType() override;
    int select(Graph& mGraph, int partyId) override ;
    SelectionPolicy* constructSelectionPolicy();

};