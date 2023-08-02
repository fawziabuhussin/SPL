package bguspl.set.ex;

import bguspl.set.Env;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

/**
 * This class contains the data that is visible to the player.
 *
 * @inv slotToCard[x] == y iff cardToSlot[y] == x
 */
public class Table {

    private Object[] keysOfslots; // Keys for each slot.
    /**
     * The game environment object.
     */
    private final Env env;
    public static final int legalSetSize = 3;
    public static final int second = 999;

    public volatile boolean touchTable;

    /**
     * Mapping between a slot and the card placed in it (null if none).
     */
    protected final Integer[] slotToCard; // card per slot (if any)

    /**
     * Mapping between a card and the slot it is in (null if none).
     */
    protected final Integer[] cardToSlot; // slot per card (if any)

    public BlockingQueue<Player> playersQueue = new LinkedBlockingDeque<Player>();

    /**
     * Constructor for testing.
     *
     * @param env        - the game environment objects.
     * @param slotToCard - mapping between a slot and the card placed in it (null if
     *                   none).
     * @param cardToSlot - mapping between a card and the slot it is in (null if
     *                   none).
     */
    public Table(Env env, Integer[] slotToCard, Integer[] cardToSlot) {

        this.env = env;
        this.slotToCard = slotToCard;
        this.cardToSlot = cardToSlot;
        keysOfslots = new Object[env.config.tableSize];
        touchTable = true;

        for (int i = 0; i < env.config.tableSize; i++) {
            keysOfslots[i] = new Object();
        }
    }

    /**
     * Constructor for actual usage.
     *
     * @param env - the game environment objects.
     */
    public Table(Env env) {

        this(env, new Integer[env.config.tableSize], new Integer[env.config.deckSize]);

    }

    /**
     * This method prints all possible legal sets of cards that are currently on the
     * table.
     */
    public void hints() {
        List<Integer> deck = Arrays.stream(slotToCard).filter(Objects::nonNull).collect(Collectors.toList());
        env.util.findSets(deck, Integer.MAX_VALUE).forEach(set -> {
            StringBuilder sb = new StringBuilder().append("Hint: Set found: ");
            List<Integer> slots = Arrays.stream(set).mapToObj(card -> cardToSlot[card]).sorted()
                    .collect(Collectors.toList());
            int[][] features = env.util.cardsToFeatures(set);
            System.out.println(
                    sb.append("slots: ").append(slots).append(" features: ").append(Arrays.deepToString(features)));
        });
    }

    /**
     * Count the number of cards currently on the table.
     *
     * @return - the number of cards on the table.
     */
    public int countCards() {
        int cards = 0;
        for (Integer card : slotToCard)
            if (card != null)
                ++cards;
        return cards;
    }

    /**
     * Places a card on the table in a grid slot.
     * 
     * @param card - the card id to place in the slot.
     * @param slot - the slot in which the card should be placed.
     *
     * @post - the card placed is on the table, in the assigned slot.
     */
    public void placeCard(int card, int slot) {
        try {
            Thread.sleep(env.config.tableDelayMillis);
        } catch (InterruptedException ignored) {
        }
        synchronized (keysOfslots[slot]) {
            cardToSlot[card] = slot;
            slotToCard[slot] = card;
            env.ui.placeCard(card, slot); // show in the display.
        }
    }

    /**
     * Removes a card from a grid slot on the table.
     * 
     * @param slot - the slot from which to remove the card.
     */
    public void removeCard(int slot) {
        try {
            Thread.sleep(env.config.tableDelayMillis);
        } catch (InterruptedException ignored) {
        }
        synchronized (keysOfslots[slot]) {
            Integer card = slotToCard[slot];
            if (card != null) {
                cardToSlot[card] = null;
                slotToCard[slot] = null;
                env.ui.removeCard(slot); // show in the display.
            }
        }
    }

    /**
     * Places a player token on a grid slot.
     * 
     * @param player - the player the token belongs to.
     * @param slot   - the slot on which to place the token.
     */
    public void placeToken(int player, int slot) {
        synchronized (keysOfslots[slot]) {
            Integer card = slotToCard[slot];
            if (card != null && cardToSlot[card] != null) {
                env.ui.placeToken(player, slot);
            }
        }
    }

    /**
     * Removes a token of a player from a grid slot.
     * 
     * @param player - the player the token belongs to.
     * @param slot   - the slot from which to remove the token.
     * @return - true iff a token was successfully removed.
     */
    public boolean removeToken(int player, int slot) {
        synchronized (keysOfslots[slot]) {
            Integer card = slotToCard[slot];
            if (card != null && cardToSlot[card] != null) {
                env.ui.removeToken(player, slot);
                return true;
            }
        }
        return false;
    }

    public void removeAllTokens(Player[] players) { // remove all
        synchronized (keysOfslots) {
            for (Player plyr : players) {
                int[] cardsOfPlayer = plyr.tokensQueue.stream().mapToInt(i -> i).toArray();
                if (cardsOfPlayer != null) {
                    for (int i = 0; i < cardsOfPlayer.length; i++) {
                        if (cardToSlot[cardsOfPlayer[i]] != null) {
                            removeToken(plyr.id, cardToSlot[cardsOfPlayer[i]]);
                            plyr.myCardsSlots[cardsOfPlayer[i]] = null;
                        }
                    }
                }
                plyr.tokensQueue.clear();
            }
        }
    }

    /**
     * @param d
     */
    public synchronized void removeAllCards(Dealer d) { // remove all cards.
        synchronized (keysOfslots) {
            for (int i = 0; i < slotToCard.length; i++) {
                if (slotToCard[i] != null) {
                    if (d.deck.size() != 0)
                        d.deck.add(slotToCard[i]);
                    removeCard(i);
                }
            }
        }
    }

    /*
     * This method will check every slot if it's null:
     * If null: will place a card from the deck if there is cards.
     * If not: it will do nothing.
     */
    public synchronized void placeCardsOnTable(List<Integer> deck) {
        synchronized (keysOfslots) {
            for (int i = 0; i < env.config.tableSize; i++) {
                if (deck.size() > 0) { // there is no cards in the deck.
                    Integer card = slotToCard[i];
                    if (card == null) {
                        Random rand = new Random();
                        int randomCard = rand.nextInt(deck.size());
                        placeCard(deck.remove(randomCard), i);
                    }
                }
            }
        }

    }
}
