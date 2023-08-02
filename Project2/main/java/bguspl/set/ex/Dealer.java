package bguspl.set.ex;

import bguspl.set.Env;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class manages the dealer's threads and data
 */
public class Dealer implements Runnable {

    /**
     * The game environment object.
     */
    private final Env env;
    /**
     * Game entities.
     */
    private final Table table;
    public final Player[] players;
    // final Object monitor = new Object();
    /**
     * The list of card ids that are left in the dealer's deck.
     */
    public final List<Integer> deck;
    /**
     * True iff game should be terminated due to an external event.
     */
    private Thread[] threadsOfPlayers;
    public volatile boolean terminate;
    public static final long dealerSleepTime = 1000;
    /**
     * The time when the dealer needs to reshuffle the deck due to turn timeout.
     */
    private long reshuffleTime = Long.MAX_VALUE;

    public Dealer(Env env, Table table, Player[] players) {
        this.env = env;
        this.table = table;
        this.players = players;
        deck = IntStream.range(0, env.config.deckSize).boxed().collect(Collectors.toList());
    }

    /**
     * The dealer thread starts here (main loop for the dealer thread).
     */
    @Override
    public void run() {
        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " starting.");
        threadsOfPlayers = new Thread[players.length];
        // Create and start threads in the first loop
        /* start the players */
        for (Player p : players) {
            threadsOfPlayers[p.id] = new Thread(p);
            threadsOfPlayers[p.id].start();
        }
        reshuffleTime = System.currentTimeMillis() + env.config.turnTimeoutMillis; // 60 seconds

        /* the loops */
        while (!shouldFinish()) {
            long timeWasted = System.currentTimeMillis();
            placeCardsOnTable(0);
            table.touchTable = false;
            timerLoop(System.currentTimeMillis() - timeWasted); // waste time in place cards
            table.touchTable = true;
            updateTimerDisplay(true, System.currentTimeMillis() - timeWasted);
            removeAllCardsFromTable();

        }

        /* End of game */
        terminate();
        for (int i = players.length - 1; i >= 0; i--) {
            try {
                threadsOfPlayers[i].join();
            } catch (InterruptedException e) {
            }
        }
        announceWinners();
        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " terminated.");
    }

    /**
     * The inner loop of the dealer thread that runs as long as the countdown did
     * not time out.
     */
    private void timerLoop(long timeWasted) {
        while (!terminate && System.currentTimeMillis() < reshuffleTime + timeWasted) {
            updateTimerDisplay(false, timeWasted);
            sleepUntilWokenOrTimeout();
            long removeCardsWasteTime = System.currentTimeMillis();
            removeCardsFromTable();
            placeCardsOnTable(System.currentTimeMillis() - removeCardsWasteTime);
        }
    }

    /**
     * Called when the game should be terminated due to an external event.
     */
    public void terminate() {
        for (int i = players.length - 1; i >= 0; i--) {
            players[i].terminate();
            try {
                threadsOfPlayers[players[i].id].notify();
            } catch (Exception e) {
            }
            players[i].mustWait = false;
        }
        terminate = true;
    }

    /**
     * Check if the game should be terminated or the game end conditions are met.
     *
     * @return true iff the game should be finished.
     */
    private boolean shouldFinish() {
        return terminate || env.util.findSets(deck, 1).size() == 0;
    }

    /**
     * Checks cards should be removed from the table and removes them.
     */

    private void removeCardsFromTable() {
        // check if a player have a legal set, if yes remove his cards
        // else do nothing
        if (table.playersQueue.size() != 0) {
            Player playerRef = table.playersQueue.peek(); // remove the player from the queue.
            int[] tokensArray = playerRef.tokensQueue.stream().mapToInt(i -> i).toArray(); // copy the vector.

            if (tokensArray != null && tokensArray.length == Table.legalSetSize) {

                boolean checkLegalSet = env.util.testSet(tokensArray); // check if it is legal set.

                if (checkLegalSet) {
                    playerRef.tokensQueue.clear(); // remove all the cards from the queue of this player.

                    for (Integer cardOfToken : tokensArray) {

                        if (table.cardToSlot[cardOfToken] != null) {
                            int slotOfCard = table.cardToSlot[cardOfToken]; // get tokens card.

                            if (table.cardToSlot[cardOfToken] != null) {
                                table.removeToken(playerRef.id, slotOfCard);
                                checkOtherPlayersTokens(players, cardOfToken, slotOfCard); // check if other players
                                                                                           // place the token at the
                                                                                           // same card.
                                table.removeCard(slotOfCard); // remove the card from the slot.
                                playerRef.myCardsSlots[cardOfToken] = null;

                            }

                        }

                    }

                    playerRef.point();

                }

                else {
                    playerRef.penalty();
                }
            }
            table.playersQueue.remove(); // remove the player from the queue.
            try {
                threadsOfPlayers[playerRef.id].notify(); // Player can exit the monitor.
            } catch (Exception e) {
            }
            playerRef.mustWait = false;
        }
    }

    /**
     * check if player has token at card that part of legal set.
     */
    private void checkOtherPlayersTokens(Player[] players, int card, int slot) {
        for (Player plyr : players) {
            if (plyr.tokensQueue.contains(card)) { // if player has a token at this card remove the token.
                plyr.tokensQueue.remove(card);
                table.removeToken(plyr.id, slot);
                plyr.myCardsSlots[card] = null;
                if (table.playersQueue.contains(plyr)) { // if the player was in the queue, remove it from the queue.
                    table.playersQueue.remove(plyr);
                    try {
                        threadsOfPlayers[plyr.id].notify();
                    } catch (Exception e) {
                    }
                    plyr.mustWait = false;
                }
            }
        }
    }

    /**
     * Check if any cards can be removed from the deck and placed on the table.
     */
    private void placeCardsOnTable(long timeWasted) {
        if (table.countCards() < env.config.tableSize && deck.size() > 0) { // found set - reshuffle.
            reshuffleTime = System.currentTimeMillis() + env.config.turnTimeoutMillis; // 60 seconds
            env.ui.setCountdown(env.config.turnTimeoutMillis, false);
        }
        table.placeCardsOnTable(deck);

    }

    /**
     * Sleep for a fixed amount of time or until the thread is awakened for some
     * purpose.
     */
    private void sleepUntilWokenOrTimeout() {
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Reset and/or update the countdown and the countdown display.
     */
    private void updateTimerDisplay(boolean reset, long timeWasted) {
        if (!reset) {
            long tmp = reshuffleTime - System.currentTimeMillis()
                     + timeWasted;
            if (tmp <= env.config.turnTimeoutWarningMillis && tmp >= 0) { // change the color to red
                env.ui.setCountdown(tmp, true);
            } else if (tmp > env.config.turnTimeoutWarningMillis)
                env.ui.setCountdown(tmp, false);
        } else { // to reset the time
            reshuffleTime = System.currentTimeMillis() + env.config.turnTimeoutMillis; // 60 seconds
            env.ui.setCountdown(0, true);
        }
    }

    /**
     * Returns all the cards from the table to the deck.
     */
    private void removeAllCardsFromTable() {

        if (!shouldFinish()) {
            table.removeAllTokens(players);
            table.removeAllCards(this);
            for (Player p : players) {
                try {
                    threadsOfPlayers[p.id].notify();
                } catch (Exception e) {
                }
                p.mustWait = false;
            }
            table.playersQueue.clear();
        }
    }

    /**
     * Check who is/are the winner/s and displays them.
     */
    private void announceWinners() {
        int maxScore = getHighestScore();
        int numbersOfwinners = getNumberOfWinners(maxScore);
        int[] winners = new int[numbersOfwinners];
        getWinners(maxScore, winners);
        synchronized (table) {
            terminate();
            table.removeAllTokens(players);
            table.removeAllCards(this);
            env.ui.announceWinner(winners);

            try {
                Thread.sleep(env.config.endGamePauseMillies);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void getWinners(int maxScore, int[] winners) {
        int counter = 0;
        for (Player p : players) { // add all the players with the highest score.
            if (maxScore == p.score()) {
                winners[counter] = p.id;
                counter++;
            }
        }
    }

    private int getNumberOfWinners(int maxScore) {
        int numbersOfwinners = 0;
        for (Player p : players) { // count the number of winners.
            if (maxScore == p.score()) {
                numbersOfwinners++;
            }
            p.terminate();
        }
        return numbersOfwinners;
    }

    public int getHighestScore() {
        int maxScore = 0;
        for (Player p : players) { // get the highest score in array of players.
            int current = p.score();
            maxScore = current > maxScore ? current : maxScore;
        }
        return maxScore;
    }
}
