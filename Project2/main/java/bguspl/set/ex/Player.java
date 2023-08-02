package bguspl.set.ex;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import bguspl.set.Env;

/**
 * This class manages the players' threads and data
 *
 * @inv id >= 0
 * @inv score >= 0
 */
public class Player implements Runnable {

    /**
     * The game environment object.
     */
    private final Env env;

    /**
     * Game entities.
     */
    private final Table table;

    /**
     * The id of the player (starting from 0).
     */
    public final int id;

    /**
     * The thread representing the current player.
     */

    /**
     * The thread of the AI (computer) player (an additional thread used to generate
     * key presses).
     */
    private Thread aiThread;

    /**
     * True iff the player is human (not a computer player).
     */
    private final boolean human;

    /**
     * True iff game should be terminated due to an external event.
     */
    public volatile boolean terminate;

    /**
     * The current score of the player.
     */
    public int score;
    /*
     * NEW MEMBERS
     */
    public BlockingQueue<Integer> tokensQueue = new LinkedBlockingDeque<Integer>();

    public volatile boolean mustWait = false;
    public volatile boolean mustWaitAI = false;

    // to not check after getting penalty again and again,
    public volatile boolean checkedAfterGettingPenalty = false;
    public volatile boolean currentlyFreezed = false; // to allow/disallow keypresses.
    public volatile boolean LegalOrIllegal = false; // this mean we have now legal set, or not.
    final Object monitor = new Object();
    Integer[] myCardsSlots; // memorize the slot of each card.

    /**
     * The class constructor.
     *
     * @param env    - the environment object.
     * @param dealer - the dealer object.
     * @param table  - the table object.
     * @param id     - the id of the player.
     * @param human  - true iff the player is a human player (i.e. input is provided
     *               manually, via the keyboard).
     */
    public Player(Env env, Dealer dealer, Table table, int id, boolean human) {
        this.env = env;
        this.table = table;
        this.id = id;
        this.human = human;
        myCardsSlots = new Integer[env.config.deckSize];
        for (int i = 0; i < env.config.deckSize; i++) {
            myCardsSlots[i] = null;
        }
    }

    /**
     * The main player thread of each player starts here (main loop for the player
     * thread).
     */
    @Override
    public void run() {
        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + "starting.");
        if (!human)
            createArtificialIntelligence();
        while (!terminate) {
            if (tokensQueue.size() == Table.legalSetSize && !checkedAfterGettingPenalty
                    && !table.playersQueue.contains(this)) {
                mustWait = true;
                currentlyFreezed = true;
                table.playersQueue.add(this);
                synchronized (this) {
                    while (mustWait) {
                        try {
                            monitor.wait();
                        } catch (Exception e) {
                        }
                    }
                }
                if (LegalOrIllegal) { // legal
                    playerToSleeep(id, env.config.pointFreezeMillis);
                } else { // illegal
                    playerToSleeep(id, env.config.penaltyFreezeMillis);
                    checkedAfterGettingPenalty = true;
                }
                currentlyFreezed = false;
                mustWaitAI = false;
            }
        }
        if (!human)
            try {
                aiThread.join();
            } catch (InterruptedException ignored) {
                env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " terminated.");
            }
    }

    /**
     * Creates an additional thread for an AI (computer) player. The main loop of
     * this thread repeatedly generates
     * key presses. If the queue of key presses is full, the thread waits until it
     * is not full.
     */
    private void createArtificialIntelligence() {
        // note: this is a very very smart AI (!)
        aiThread = new Thread(() -> {
            env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " starting.");
            while (!terminate) {
                if (!table.playersQueue.contains(this)) {
                    mustWaitAI = true;
                    inValidTokens();
                    keyPressesAI();
                }
            }
            env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " terminated.");
        }, "computer-" + id);
        aiThread.start();
    }

    /*
     * checks if the cards in my queue are on the table.
     */
    private void inValidTokens() {
        for (Integer tokenI : tokensQueue) {
            Integer cardInQueue = tokenI;
            if (table.cardToSlot[cardInQueue] != myCardsSlots[cardInQueue]
                    && myCardsSlots[cardInQueue] != null & !table.playersQueue.contains(this)) {
                tokensQueue.remove(cardInQueue);
                env.ui.removeToken(id, myCardsSlots[cardInQueue]);
                myCardsSlots[cardInQueue] = null;
            }
        }
    }

    public void keyPressesAI() {
        Random rand = new Random();
        int randomSlot = rand.nextInt(table.slotToCard.length); // 0-11 slots
        if (table.slotToCard[randomSlot] != null) { // if cards on the table are less than 12
            keyPressed(randomSlot);
            if (tokensQueue.size() == Table.legalSetSize) {
                while (mustWait) {
                    try {
                        aiThread.wait();
                    } catch (Exception e) {
                    }
                }
            }
        }

    }

    /**
     * Called when the game should be terminated due to an external event.
     */
    public void terminate() {
        terminate = true;
    }

    /**
     * This method is called when a key is pressed.
     *
     * @param slot - the slot corresponding to the key pressed.
     */
    public void keyPressed(int slot) {
        Integer card = table.slotToCard[slot];
        if (!table.touchTable) {
            if (!currentlyFreezed & !table.playersQueue.contains(this)) { // If we are in currentlyFreeze, no one can
                if (tokensQueue.contains(card)) { // remove token
                    tokensQueue.remove(card);
                    table.removeToken(id, slot);
                    myCardsSlots[card] = null;
                    if (!currentlyFreezed)
                        checkedAfterGettingPenalty = false; // once we remove a token, then we don't get a penalty
                } else {
                    if (tokensQueue.size() < Table.legalSetSize) { // add token
                        tokensQueue.add(card);
                        table.placeToken(id, slot);
                        myCardsSlots[card] = slot;
                        checkedAfterGettingPenalty = false;
                    }
                }
            }
        }
    }

    /**
     * Award a point to a player and perform other related actions.
     *
     * @post - the player's score is increased by 1.
     * @post - the player's score is updated in the ui.
     */
    public void point() {
        int ignored = table.countCards(); // this part is just for demonstration
        env.ui.setScore(id, ++score); // add one point
        LegalOrIllegal = true; // true means legal.
    }

    /**
     * Penalize a player and perform other related actions.
     */
    public void penalty() {
        LegalOrIllegal = false; // illegal means false;
    }

    private void playerToSleeep(int playerId, long time) {
        env.ui.setFreeze(playerId, time); // Red color.
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
        env.ui.setFreeze(playerId, (-1) * time); // Black color.
    }

    public int score() {
        return score;
    }
}
