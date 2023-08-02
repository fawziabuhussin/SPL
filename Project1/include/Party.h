#pragma once
#include <string>

using std::string;

class JoinPolicy;
class Simulation;

enum State
{
    Waiting,
    CollectingOffers,
    Joined
};

class Party
{
public:
    Party(int id, string name, int mandates, JoinPolicy *);
    const string &getName() const;
    void step(Simulation &s);
    void setState(State state);
    State getState() const;
    int getMandates() const;
    /// Rule Of 5
    ~Party(); // Destructor'
    Party(const Party &other); // Copy constructor
    Party& operator=(const Party& other); // operator=
    Party& operator=(Party&& other) noexcept;  // move assignment.
    Party(Party&& other); // move constructor
    /// NEW METHODS ///
    int getLastOffer() const;
    void link(const Party& other);
    void setLastOffer(int pId);
    void addIter();
    void setCoalition(int coalId);
    int getCoalition();


private:
    int mId;
    string mName;
    int mMandates;
    JoinPolicy *mJoinPolicy;
    State mState;
    /// NEW MEMBERS ///
    int iterNum;
    int coalId;
    int lastOffer;
};
