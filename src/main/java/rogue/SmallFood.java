package rogue;

import java.awt.Point;


public class SmallFood extends Food implements Tossable {

    /**
     * Default constructor.
     */
    public SmallFood() {
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
    public SmallFood(int itemId, String name, String desc, String type, Point xyLocation) {
        super(itemId, name, desc, type, xyLocation);
    }

    @Override
    public String eat(Player player) {
        return getDescription().split(":")[0];
    }

    @Override
    public String toss(Player player) {
        this.setXyLocation(Rogue.lookForEmptyTile(player.getCurrentRoom()));
        this.getCurrentRoom().getRogue().addRoomItem(this, player.getCurrentRoom());
        String[] split = getDescription().split(":");
        if (split.length == 2) {
            return split[1];
        } else {
            return getDescription();
        }
    }
}
