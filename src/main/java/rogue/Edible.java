package rogue;

public interface Edible {

    /**
     * Eats an item.
     * @param player as the player that eats the item
     * @return the string description
     */
    String eat(Player player);
}
