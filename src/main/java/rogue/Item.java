package rogue;

import java.awt.Point;
import java.io.Serializable;

/**
 * A basic Item class; basic functionality for both consumables and equipment.
 */
public class Item implements Serializable {

    private int id;
    private String itemName;
    private String itemType;
    private Point xYLocation;
    private Character displayCharacter;
    private String description;
    private Room currentRoom;

    //Constructors

    /**
     * Default item constructor.
     */
    public Item() {

    }

    /**
     * Item constructor that takes an id, name, type, and xylocation.
     *
     * @param itemId     as the id of the item
     * @param name       as the name of the item
     * @param desc       as the description of the item
     * @param type       as the type of the item
     * @param xyLocation as the location of the item
     */
    public Item(int itemId, String name, String desc, String type, Point xyLocation) {
        this.id = itemId;
        this.itemName = name;
        this.itemType = type;
        this.xYLocation = xyLocation;
        this.description = desc;
    }

    /**
     * Item constructor that takes an itemId, name, type, xyLocation, and room.
     *
     * @param itemId     as the id of the item
     * @param name       as the name of the item
     * @param type       as the type of the item
     * @param xyLocation as the location of the item
     * @param newRoom    as the current room the item resides in
     */
    public Item(int itemId, String name, String type, Point xyLocation, Room newRoom) {
        this.id = itemId;
        this.itemName = name;
        this.itemType = type;
        this.xYLocation = xyLocation;
        this.currentRoom = newRoom;
    }

    /**
     * Creates an item based upon its type.
     *
     * @param itemId     as the id of the item
     * @param name       as the name of the item
     * @param desc       as the description of the item
     * @param type       as the type of item
     * @param xyLocation as the location of the item
     * @return the new item subclass instance
     */
    public static Item createItem(int itemId, String name, String desc, String type, Point xyLocation) {
        if (type.equalsIgnoreCase("Food")) {
            return new Food(itemId, name, desc, type, xyLocation);
        } else if (type.equalsIgnoreCase("Clothing")) {
            return new Clothing(itemId, name, desc, type, xyLocation);
        } else if (type.equalsIgnoreCase("Magic")) {
            return new Magic(itemId, name, desc, type, xyLocation);
        } else if (type.equalsIgnoreCase("Potion")) {
            return new Potion(itemId, name, desc, type, xyLocation);
        } else if (type.equalsIgnoreCase("Ring")) {
            return new Ring(itemId, name, desc, type, xyLocation);
        } else if (type.equalsIgnoreCase("SmallFood")) {
            return new SmallFood(itemId, name, desc, type, xyLocation);
        }
        return new Item(itemId, name, desc, type, xyLocation);
    }


    /**
     * Returns the id of the item.
     *
     * @return the id of the item
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the current id of the item.
     *
     * @param itemId as the id to set the item to
     */
    public void setId(int itemId) {
        this.id = itemId;
    }

    /**
     * Gets the name of the item.
     *
     * @return the name of the item
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Sets the name of the item.
     *
     * @param newItemName as the new name for the item.
     */
    public void setItemName(String newItemName) {
        this.itemName = newItemName;
    }

    /**
     * Gets the name of an item.
     *
     * @return the name of the item
     */
    public String getName() {
        return itemName;
    }

    /**
     * Sets the name of the item.
     *
     * @param newItemName as the new name to set the item too
     */
    public void setName(String newItemName) {
        this.itemName = newItemName;
    }

    /**
     * Gets the item type of the item.
     *
     * @return the item type
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * Gets the item type of the item.
     *
     * @return the item type
     */
    public String getType() {
        return itemType;
    }

    /**
     * Sets the item type of the item.
     *
     * @param newItemType as the new type for the item
     */
    public void setItemType(String newItemType) {
        this.itemType = newItemType;
    }

    /**
     * Sets the item type of the item.
     *
     * @param newItemType as the new type for the item
     */
    public void setType(String newItemType) {
        this.itemType = newItemType;
    }

    /**
     * Gets a character to display for the item.
     *
     * @return the character corresponding to an item
     */
    public Character getDisplayCharacter() {
        return displayCharacter;
    }

    /**
     * Sets the display character for the item.
     *
     * @param newDisplayCharacter as the new character to display for the item
     */
    public void setDisplayCharacter(Character newDisplayCharacter) {
        this.displayCharacter = newDisplayCharacter;
    }

    /**
     * Gets the description of the item.
     *
     * @return the description for the item
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description for the item.
     *
     * @param newDescription as the new item description
     */
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    /**
     * Gets the point where the item resides at currently.
     *
     * @return the point at which the item is found
     */
    public Point getXyLocation() {
        return xYLocation;
    }

    /**
     * Sets the location of the item.
     *
     * @param newXyLocation as the new point the item will reside at
     */
    public void setXyLocation(Point newXyLocation) {
        this.xYLocation = newXyLocation;
    }

    /**
     * Gets the current room the item resides in.
     *
     * @return the current room the item is in
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Sets the current room the item resides in.
     *
     * @param newCurrentRoom as the new room the item will reside in
     */
    public void setCurrentRoom(Room newCurrentRoom) {
        this.currentRoom = newCurrentRoom;
    }
}
