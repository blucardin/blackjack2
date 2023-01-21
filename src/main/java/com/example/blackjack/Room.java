/*
 * This is a room class that will be used to store the engine and id of the room.
 * 
 */

package com.example.blackjack;

public class Room {
    // create an engine object, and id attirbute, and a locked boolean
    private BlackJackEngine engine = new BlackJackEngine();
    private int id;
    private boolean locked;
    private String dealersub;
    private boolean stored = false;
    private int turn;

    /**
     * 
     * Sets the engine object
     * 
     * @param engine BlackJackEngine object
     */
    public void setEngine(BlackJackEngine engine) {
        this.engine = engine;
    }

    /**
     * 
     * Returns the engine object
     * 
     * @return BlackJackEngine engine
     */
    public BlackJackEngine getEngine() {
        return engine;
    }

    /**
     * 
     * Returns the id
     * 
     * @return int id
     */
    public int getId() {
        return id;
    }

    /**
     * 
     * Sets the id
     * 
     * @param id int value
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 
     * Returns the locked status
     * 
     * @return boolean locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * 
     * Sets the locked status
     * 
     * @param locked boolean value
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * 
     * Returns the dealer sub
     * 
     * @return String dealersub
     */
    public String getDealersub() {
        return dealersub;
    }

    /**
     * 
     * Sets the dealer sub
     * 
     * @param dealersub String value
     */
    public void setDealersub(String dealersub) {
        this.dealersub = dealersub;
    }

    /**
     * 
     * Returns the turn number
     * 
     * @return int turn
     */
    public int getTurn() {
        return turn;
    }

    /**
     * 
     * Sets the turn number
     * 
     * @param turn int value
     */
    public void setTurn(int turn) {
        this.turn = turn;
    }

    /**
     * 
     * Returns the stored status
     * 
     * @return boolean stored
     */
    public boolean isStored() {
        return stored;
    }

    /**
     * 
     * Sets the stored status
     * 
     * @param stored boolean value
     */
    public void setStored(boolean stored) {
        this.stored = stored;
    }

    /**
     * Constructor for room class
     * 
     * @param id        id of the room
     * @param dealersub dealer identification
     */
    Room(int id, String dealersub) {
        this.id = id;
        this.locked = false;
        this.dealersub = dealersub;
        this.turn = 0;
    }
}
