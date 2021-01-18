package rogue;

import java.awt.Point;

public class Clothing extends Item implements Wearable {

    /**
     * Default constructor.
     */
    public Clothing() {

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
    public Clothing(int itemId, String name, String desc, String type, Point xyLocation) {
        super(itemId, name, desc, type, xyLocation);
    }

    @Override
    public String wear(Player player) {
        player.setWearableItem(this);
        return getDescription();
    }
}
