package rogue;

import java.awt.Point;

public class Food extends Item implements Edible {

    /**
     * Default constructor.
     */
    public Food() {
        super();
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
    public Food(int itemId, String name, String desc, String type, Point xyLocation) {
        super(itemId, name, desc, type, xyLocation);
    }

    @Override
    public String eat(Player player) {
        return getDescription();
    }
}
