package rogue;

public interface Wearable {

    /**
     * Wears item.
     * @param player as the player who calls the wear function
     * @return the string description
     */
    String wear(Player player);
}
