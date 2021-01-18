package rogue;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A room within the dungeon - contains monsters, treasure,
 * doors out, etc.
 */
public class Room implements Serializable {

    private int currentWidth;
    private int currentHeight;
    private int currentId;
    private Player currentPlayer;
    private ArrayList<Item> roomItems = new ArrayList<>();
    private HashMap<String, HashMap<Integer, Door>> roomDoors = new HashMap<>();
    private HashMap<String, Character> symbols;
    private Rogue rogue;

    /**
     * Default room constructor.
     */
    public Room() {

    }

    /**
     * Room constructor that takes width, height, id and an instance of rogue.
     *
     * @param width     as the width of the room
     * @param height    as the height of the room
     * @param id        as the rooms id
     * @param rogueGame as the rogue game being used
     */
    public Room(int width, int height, int id, Rogue rogueGame) {
        this.currentWidth = width;
        this.currentHeight = height;
        this.currentId = id;
        this.rogue = rogueGame;
    }

    // Required getter and setters below

    /**
     * Gets the width of the room.
     *
     * @return width of the room
     */
    public int getWidth() {
        return currentWidth;
    }

    /**
     * Sets the width of the room.
     *
     * @param newWidth is the new width to be set for the room
     */
    public void setWidth(int newWidth) {
        this.currentWidth = newWidth;
    }

    /**
     * Gets the height of the room.
     *
     * @return the height of the room
     */
    public int getHeight() {
        return currentHeight;
    }

    /**
     * Sets the height of the room.
     *
     * @param newHeight is the new height to be set for the room
     */
    public void setHeight(int newHeight) {
        this.currentHeight = newHeight;
    }

    /**
     * Gets the id for the room.
     *
     * @return the id for the room
     */
    public int getId() {
        return currentId;
    }

    /**
     * Sets the id for the room.
     *
     * @param newId is the new id to be set for the room
     */
    public void setId(int newId) {
        this.currentId = newId;
    }

    /**
     * Gets all items contained within the room.
     *
     * @return all the items in the room
     */
    public ArrayList<Item> getRoomItems() {
        return roomItems;
    }

    /**
     * Get an item based on its locaton.
     *
     * @param location as the location of the item
     * @return the item at that location
     */
    public Item getItem(Point location) {
        for (Item item : getRoomItems()) {
            if (item.getXyLocation().getX() == location.getX()) {
                if (item.getXyLocation().getY() == location.getY()) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Sets the items for the room.
     *
     * @param newRoomItems is the list of items contained in the room
     */
    public void setRoomItems(ArrayList<Item> newRoomItems) {
        this.roomItems = newRoomItems;
    }

    /**
     * Gets the instance of rogue this room is running on.
     * @return the rogue instance
     */
    public Rogue getRogue() {
        return rogue;
    }

    /**
     * Gets the player in the room.
     *
     * @return the current player in the room
     */
    public Player getPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the player for the room.
     *
     * @param newPlayer as the player to be set for the room
     */
    public void setPlayer(Player newPlayer) {
        this.currentPlayer = newPlayer;
    }

    /**
     * Gets a door in the room based off its direction.
     *
     * @param direction as the directional string "N", "S", "E", "W"
     * @param location  as the location on the wall the door resides
     * @return the location of the associated door
     */
    public Door getDoor(String direction, int location) {
        return getAllDoors().get(direction).get(location);
    }

    /**
     * Gets the location of a door based off its direction.
     *
     * @param direction as the directional wall the door is located on
     * @return the location
     */
    public int getDoorLocation(String direction) {
        HashMap<Integer, Door> map = getAllDoors().get(direction);
        if (map != null) {
            for (int distance : map.keySet()) {
                return distance;
            }
        }
        return -1;
    }

    /**
     * Gets a door object based off its direction.
     *
     * @param direction as the directional wall the door is located on
     * @return the associated door
     */
    public Door getDoor(String direction) {
        HashMap<Integer, Door> map = getAllDoors().get(direction);
        for (Door door : map.values()) {
            return door;
        }
        return null;
    }

    /**
     * Get all doors in the room.
     *
     * @return a map of all door maps
     */
    public HashMap<String, HashMap<Integer, Door>> getAllDoors() {
        return roomDoors;
    }

    /**
     * Get all the doors in a room and store in an arraylist.
     *
     * @return the arraylist of all door objects
     */
    public ArrayList<Door> getDoors() {
        ArrayList<Door> list = new ArrayList<>();
        for (HashMap<Integer, Door> map : getAllDoors().values()) {
            list.addAll(map.values());
        }
        return list;
    }

/*
direction is one of NSEW
location is a number between 0 and the length of the wall
*/

    /**
     * Adds a door to the room.
     *
     * @param direction as the direction the door is on the map
     * @param location  as the value that determines how far the door is on that wall
     */
    public void setDoor(String direction, int location) {
        HashMap<Integer, Door> door = getAllDoors().computeIfAbsent(direction, k -> new HashMap<>());
        door.put(location, new Door());
        roomDoors.put(direction, door);
    }

    /**
     * Adds a given door to the door objects.
     *
     * @param direction as the direction of the door
     * @param location  as the location of the door
     * @param door      as the pre existing door
     */
    public void setDoor(String direction, int location, Door door) {
        HashMap<Integer, Door> doors = getAllDoors().computeIfAbsent(direction, k -> new HashMap<>());
        doors.put(location, door);
        getAllDoors().computeIfAbsent(direction, k -> roomDoors.put(direction, doors));
    }

    /**
     * Adds an item to the rooms itemlist after checking if it can be added.
     *
     * @param toAdd as the item to add to the room
     * @throws ImpossiblePositionException if the item cannot be placed in a specific position
     * @throws NoSuchItemException         if the item does not have a valid id
     */
    public void addItem(Item toAdd) throws ImpossiblePositionException, NoSuchItemException {
        Point itemPos = toAdd.getXyLocation();
        if (!(isWall(itemPos) || isOnItem(itemPos) || !(isInRoomBounds(itemPos)) || isPlayer(itemPos))) {
            if (rogue.getRogueParser().getAllPossibleItems().get(toAdd.getId()) != null) {
                roomItems.add(toAdd);
            } else {
                throw new NoSuchItemException();
            }
        } else {
            throw new ImpossiblePositionException();
        }
    }

    /**
     * Checks if a player is in the room.
     * @return if the player is in the room
     */
    public boolean isPlayerInRoom() {
        return getPlayer() != null;
    }

    /**
     * Verify's a room is built correctly.
     *
     * @return whether the room is built correctly (true) or it isnt (false)
     * @throws NotEnoughDoorsException if the room does not have at least one door
     */
    public boolean verifyRoom() throws NotEnoughDoorsException {
        if (!(checkItems() && checkPlayer() && checkDoors())) {
            return false;
        }
        if (getDoors().size() < 1) {
            throw new NotEnoughDoorsException();
        }
        return true;
    }

    private boolean checkItems() {
        for (Item item : getRoomItems()) {
            Point itemPos = item.getXyLocation();
            if (isWall(itemPos) || isPlayer(itemPos) || !(isInRoomBounds(itemPos))) {
                return false;
            }
        }
        return true;
    }

    private boolean checkPlayer() {
        if (getPlayer() != null) {
            Point playerLoc = getPlayer().getXyLocation();
            return isInRoomBounds(playerLoc) && !isWall(playerLoc) && !isOnItem(playerLoc);
        }
        return true;
    }

    private boolean checkDoors() {
        for (Door door : getDoors()) {
            if (door.getConnectedRooms().size() < 2) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a location is a wall.
     *
     * @param point as the location to check
     * @return true if it is a wall or false if it isnt
     */
    public boolean isWall(Point point) {
        boolean horizontalWall = point.getX() == 0 || point.getX() == getWidth() - 1;
        boolean verticalWall = point.getY() == 0 || point.getY() == getHeight() - 1;
        return horizontalWall || verticalWall;
    }

    /**
     * Check if a location is on an item.
     *
     * @param point as the location to check
     * @return true if it is on an item and false if it isnt
     */
    public boolean isOnItem(Point point) {
        for (Item item : getRoomItems()) {
            if (item.getXyLocation().getX() == point.getX() && item.getXyLocation().getY() == point.getY()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a point is within the rooms bounds.
     *
     * @param point as the point to check
     * @return whether the point is within the room
     */
    public boolean isInRoomBounds(Point point) {
        return point.getX() >= 0 && point.getX() < getWidth() && point.getY() >= 0 && point.getY() < getHeight();
    }

    /**
     * Checks if a location is on the player.
     *
     * @param point as the location to check
     * @return if the location is on a player
     */
    public boolean isPlayer(Point point) {
        Player player = getPlayer();
        if (player != null) {
            return point.getX() == player.getXyLocation().getX() && point.getY() == player.getXyLocation().getY();
        }
        return false;
    }

    /**
     * Checks to see if a point is on a door.
     *
     * @param point as the point to check
     * @return true if on a door, false if not
     */
    public boolean isOnDoor(Point point) {
        Point loc = getDoorPoint(getWallDir(point));
        if (loc != null) {
            return loc.getX() == point.getX() && loc.getY() == point.getY();
        }
        return false;
    }

    /**
     * Gets the string representation of a wall face.
     *
     * @param point as the point to check if on a wall face or not
     * @return the string representation of the wall face
     */
    public String getWallDir(Point point) {
        if (point.getX() == 0) {
            return "W";
        } else if (point.getX() == getWidth() - 1) {
            return "E";
        } else if (point.getY() == 0) {
            return "N";
        } else if (point.getY() == getHeight() - 1) {
            return "S";
        }
        return "I";
    }

    /**
     * Gets a doors location based off a direction.
     *
     * @param direction as the direction to be checked
     * @return the point where the door resides at
     */
    public Point getDoorPoint(String direction) {
        int loc = getDoorLocation(direction);
        if (loc != -1) {
            if (direction.equalsIgnoreCase("N")) {
                return new Point(loc, 0);
            } else if (direction.equalsIgnoreCase("S")) {
                return new Point(loc, getHeight() - 1);
            } else if (direction.equalsIgnoreCase("E")) {
                return new Point(getWidth() - 1, loc);
            } else if (direction.equalsIgnoreCase("W")) {
                return new Point(0, loc);
            }
        }
        return null;
    }

    /**
     * Produces a string that can be printed to produce an ascii rendering of the room and all of its contents.
     *
     * @return (String) String representation of how the room looks
     */
    public String displayRoom() {
        StringBuilder room = new StringBuilder();
        room.append(Rogue.BLANK_OFFSET);
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                checkNorthSouth(room, i, j);
            }
            room.append("\n" + Rogue.BLANK_OFFSET);
        }
        return room.toString();
    }

    private void checkNorthSouth(StringBuilder room, int i, int j) {
        if (i == 0) {
            if (getDoorLocation("N") == j) {
                room.append(rogue.getRogueParser().getSymbol("DOOR"));
            } else {
                room.append(rogue.getRogueParser().getSymbol("NS_WALL"));
            }
        } else if (i == getHeight() - 1) {
            if (getDoorLocation("S") == j) {
                room.append(rogue.getRogueParser().getSymbol("DOOR"));
            } else {
                room.append(rogue.getRogueParser().getSymbol("NS_WALL"));
            }
        } else {
            checkEastWest(room, i, j);
        }
    }

    private void checkEastWest(StringBuilder room, int i, int j) {
        if (j == 0) {
            if (getDoorLocation("W") == i) {
                room.append(rogue.getRogueParser().getSymbol("DOOR"));
            } else {
                room.append(rogue.getRogueParser().getSymbol("EW_WALL"));
            }
        } else if (j == getWidth() - 1) {
            if (getDoorLocation("E") == i) {
                room.append(rogue.getRogueParser().getSymbol("DOOR"));
            } else {
                room.append(rogue.getRogueParser().getSymbol("EW_WALL"));
            }
        } else {
            room.append(checkPlayerAndLoot(j, i));
        }
    }

    private char checkPlayerAndLoot(int x, int y) {
        if (getPlayer() != null) {
            if (getPlayer().getXyLocation().getX() == x && getPlayer().getXyLocation().getY() == y) {
                return rogue.getRogueParser().getSymbol("PLAYER");
            }
        }
        for (Item item : getRoomItems()) {
            if (item.getXyLocation().getX() == x && item.getXyLocation().getY() == y) {
                return rogue.getRogueParser().getSymbol(item.getItemType().toUpperCase());
            }
        }
        return rogue.getRogueParser().getSymbol("FLOOR");
    }
}
