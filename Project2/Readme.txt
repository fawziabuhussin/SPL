This project requires a good understanding of Java Threads and Java Synchronization.


In the following assignment you are required to implement a simple version of the game “Set”. A description of the original game can be found below. 
All of the UI (User Interface), graphics, keyboard handling, etc. were already written.



THE IMPLEMENTED VERSION OF THE GAME: 

The game contains a deck of 81 cards. Each card contains a drawing with four features (color, number, shape, shading).

The game starts with 12 drawn cards from the deck that are placed on a 3x4 grid on the table.
The goal of each player is to find a combination of three cards from the cards on the table that are said to make up a “legal set”.
A “legal set” is defined as a set of 3 cards, that for each one of the four features — color, number, shape, and shading — the three cards must display that feature as either: (a) all the same, or: (b) all different (in other words, for each feature the three cards must avoid having two cards showing one version of the feature and the remaining card showing a different version).

The possible values of the features are:
	▪ The color: red, green or purple.
	▪ The number of shapes: 1, 2 or 3.
	▪ The geometry of the shapes: squiggle, diamond or oval.
	▪ The shading of the shapes: solid, partial or empty


For example: 

Example1: these 3 cards do form a set, because the shadings of the three cards are all the same, while the numbers, the colors, and the shapes are all different.

Example 2: these 3 cards do not form a set (although the numbers of the three cards are all the same, and the colors, and shapes are all different, only two cards have the same shading).



The game's active (i.e., initiate events) components contain the dealer and the players.
The players play together simultaneously on the table, trying to find a legal set of 3 cards. They do so by placing tokens on the cards, and once they place the third token, they should ask the dealer to check if the set is legal.

If the set is not legal, the player gets a penalty, freezing his ability of removing or placing his tokens for a specified time period.

If the set is a legal set, the dealer will discard the cards that form the set from the table, replace them with 3 new cards from the deck and give the successful player one point. In this case the player also gets frozen although for a shorter time period.

To keep the game more interesting and dynamic, and in case no legal sets are currently available on the table, once every minute the dealer collects all the cards from the table, reshuffles the deck and draws them anew.
The game will continue as long as there is a legal set to be found in the remaining cards (that are either on table or in the deck). When there is no legal set left, the game will end and the player with the most points will be declared as the winner.






SET CARD GAME : 

Set (stylized as SET or SET!) is a real-time card game designed by Marsha Falco in 1974 and published by Set Enterprises in 1991. The deck consists of 81 unique cards that vary in four features across three possibilities for each kind of feature: number of shapes (one, two, or three), shape (diamond, squiggle, oval), shading (solid, striped, or open), and color (red, green, or purple).[2] Each possible combination of features (e.g. a card with three striped green diamonds) appears as a card precisely once in the deck.
Gameplay


GAMEPLAY:


In the game, certain combinations of three cards are said to make up a "set". For each one of the four categories of features — color, number, shape, and shading — the three cards must display that feature as either a) all the same, or b) all different. Put another way: For each feature the three cards must avoid having two cards showing one version of the feature and the remaining card showing a different version.

For example, 3 solid red diamonds, 2 solid green squiggles, and 1 solid purple oval form a set, because the shadings of the three cards are all the same, while the numbers, the colors, and the shapes among the three cards are all different.

For any set, the number of features that are constant (the same on all three cards) and the number of features that differ (different on all three cards) may break down as: all 4 features differing; or 1 feature being constant and 3 differing; or 2 constant and 2 differing; or 3 constant and 1 differing. (All 4 features being constant would imply that the three cards in the set are identical, which is impossible since no cards in the Set deck are identical.) 