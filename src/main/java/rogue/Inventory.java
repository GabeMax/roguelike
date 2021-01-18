package rogue;

import java.io.Serializable;
import java.util.ArrayList;

public class Inventory implements Serializable {

    private ArrayList<Item> inventory = new ArrayList<>();

    /**
     * Default constructor.
     */
    public Inventory() {
    }


    /**
     * Displays the inventory contents.
     * @param player as corresponding player to this inventory instance
     * @return the completed string representation of the inventory
     */
    public String displayInventory(Player player) {
        StringBuilder builder = new StringBuilder();
        for (Item item : inventory) {
            if (player.getWearableItem() == item) {
                builder.append("W: ").append(item.getName()).append("\n");
            } else {
                builder.append(item.getName()).append("\n");
            }
        }
        return "<html>Inventory:<br>" + builder.toString().replaceAll("\n", "<br>");
    }

    /**
     * Removes the specified item from the inventory.
     * @param item as the item to remove
     */
   public void removeItem(Item item) {
        inventory.remove(item);
   }

    /**
     * Adds an item to the inventory.
     *
     * @param item as the item to add
     */
    public void addItem(Item item) {
        inventory.add(item);
    }

    /**
     * Gets the inventory of the player.
     *
     * @return the inventory for the player
     */
    public ArrayList<Item> getItems() {
        return inventory;
    }

    /**
     * Sets the inventory for the player.
     *
     * @param inv as the new inventory for the player
     */
    public void setInventory(ArrayList<Item> inv) {
        this.inventory = inv;
    }
}
