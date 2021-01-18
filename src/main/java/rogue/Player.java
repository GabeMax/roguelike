package rogue;

import java.awt.Point;
import java.io.Serializable;

/**
 * The player character.
 */
public class Player implements Serializable {

    private String name;
    private Room currentRoom;
    private Point xyLocation;
    private Inventory inventory = new Inventory();
    private Item wearableItem;

    /** Default constructor.*/
    public Player() {

    }

    /** Constructor that takes a player name.
     * @param playerName is the name to be set for the player
     */
    public Player(String playerName) {
        this.name = playerName;
    } //Player string constructor


    /** Constructor that takes both a name and a position for the player.
     *
     * @param names takes a name for the player
     * @param xyPosition takes a position for the player to start in
     */
    public Player(String names, Point xyPosition) { //Player name and location constructor
        this.name = names;
        this.xyLocation = xyPosition;
    }

    /**
     * Gets the item the player is currently wearing.
     * @return the item the player is currently wearing
     */
    public Item getWearableItem() {
        return wearableItem;
    }

    /**
     * Sets the item the player is currently wearing.
     * @param item as the item that the player will be set to wearing
     */
    public void setWearableItem(Item item) {
        this.wearableItem = item;
    }

    /**
     * Gets the inventory of the player.
     * @return the inventory of the player
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Sets the inventory of the player.
     * @param inv as the inventory to be set to
     */
    public void setInventory(Inventory inv) {
        this.inventory = inv;
    }

    /** Gets the players current name.
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    } //Returns player name

    /** Sets the current name of the player.
     *
     * @param newName is the new name given to the player
     */
    public void setName(String newName) {
        this.name = newName;
    } //Sets player name

    /** Gets the players current xy location.
     *
     * @return the location of the player
     */
    public Point getXyLocation() {
        return xyLocation;
    } //Gets player location

    /** Sets the current players location.
     *
     * @param newXyLocation is the new location of the player
     */
    public void setXyLocation(Point newXyLocation) {
        this.xyLocation = newXyLocation;
    } //Sets player location

    /** Gets the players current room they reside in.
     *
     * @return the room that the player is currently in
     */
    public Room getCurrentRoom() {
        return currentRoom;
    } //Gets players current room

    /** Sets the players current room to another room.
     *
     * @param newRoom is the new room that the player resides in
     */
    public void setCurrentRoom(Room newRoom) {
        this.currentRoom = newRoom;
    }
}
