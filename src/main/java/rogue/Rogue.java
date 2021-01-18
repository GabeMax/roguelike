package rogue;


import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Rogue implements Serializable {

    public static final char DOWN = 1;
    public static final char UP = 2;
    public static final char LEFT = 3;
    public static final char RIGHT = 4;
    public static final String BLANK_OFFSET = "               ";
    private Player player;
    private ArrayList<Room> rooms = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    private HashMap<String, Character> symbols = new HashMap<>();
    private boolean roomTransition = false;
    private int prevRoomWidth;
    private int prevRoomHeight;
    private RogueParser rogueParser;

    /**
     * Default rogue constructor.
     */
    public Rogue() {

    }

    /**
     * Rogue constructor with parser.
     *
     * @param parser as the parser to be passed in
     */
    public Rogue(RogueParser parser) {
        this.rogueParser = parser;
        setupRooms();
    }

    /**
     * Rogue constructor with current player.
     *
     * @param thePlayer as the player to be passed in
     */
    public void setPlayer(Player thePlayer) {
        if (this.player != null) {
            thePlayer.setCurrentRoom(this.player.getCurrentRoom());
            thePlayer.getCurrentRoom().setPlayer(thePlayer);
            thePlayer.setXyLocation(this.player.getXyLocation());
        }
        this.player = thePlayer;
    }

    /**
     * Gets the rogue parser.
     *
     * @return this games instance of the rogue parser
     */
    public RogueParser getRogueParser() {
        return rogueParser;
    }

    /**
     * Setup all rooms to be stored in subsequent objects correctly.
     */
    public void setupRooms() {
        for (Map<String, String> roomMap : rogueParser.getRooms()) {
            addRoom(roomMap);
        }
        verifyAllRooms();
    }

    /**
     * Adds a room to the game based off parsed data.
     *
     * @param toAdd as the room map to add
     */
    public void addRoom(Map<String, String> toAdd) {
        int width = Integer.parseInt(toAdd.get("width"));
        int height = Integer.parseInt(toAdd.get("height"));
        int id = Integer.parseInt(toAdd.get("id"));
        ArrayList<Map<String, String>> itemMap = rogueParser.getRoomItems(id); //Get items with room
        Room createRoom = new Room(width, height, id, this);
        if (toAdd.get("start").equalsIgnoreCase("true")) {
            createRoom.setPlayer(new Player("Yo", new Point(1, 1)));
            createRoom.getPlayer().setCurrentRoom(createRoom);
            setPlayer(createRoom.getPlayer());
        }
        addRoomItems(itemMap, createRoom);
        setDoors(createRoom, toAdd);
        rooms.add(createRoom);
    }

    private void addRoomItems(ArrayList<Map<String, String>> itemMap, Room createRoom) {
        for (Map<String, String> item : itemMap) { //For each item in the room, add the item
            addItem(item);
            Item newItem = getItem(Integer.parseInt(item.get("id")));
            addRoomItem(newItem, createRoom);
        }
    }

    /**
     * Adds an item to a room.
     *
     * @param newItem    as the item to add
     * @param createRoom as the room that it is being added too
     */
    public void addRoomItem(Item newItem, Room createRoom) {
        try {
            createRoom.addItem(newItem);
            newItem.setCurrentRoom(createRoom);
        } catch (ImpossiblePositionException e) {
            Point newItemSpot = lookForEmptyTile(createRoom);
            newItem.setXyLocation(newItemSpot);
            try {
                createRoom.addItem(newItem);
                newItem.setCurrentRoom(createRoom);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (NoSuchItemException e) {
            e.printStackTrace(); //The item is not added if the exception is thrown
        }
    }

    /**
     * Adds an item to the game based off parsed information.
     *
     * @param toAdd as the item map to add
     */
    public void addItem(Map<String, String> toAdd) {
        int itemId = Integer.parseInt(toAdd.get("id"));
        String name = toAdd.get("name");
        String type = toAdd.get("type");
        String desc = toAdd.get("description");
        Point loc = new Point(Integer.parseInt(toAdd.get("x")), Integer.parseInt(toAdd.get("y")));
        Item newItem = Item.createItem(itemId, name, desc, type, loc);
        items.add(newItem);
    }

    private void verifyAllRooms() {
        for (Room room : getRooms()) {
            try {
                if (!room.verifyRoom()) {
                    System.exit(1);
                }
            } catch (NotEnoughDoorsException e) {
                handleNotEnoughDoorsException(room);
            }
        }
    }

    private void handleNotEnoughDoorsException(Room room) {
        for (Room r : getRooms()) {
            if (r != room) {
                if (findMatch(room, r)) {
                    return;
                }
            }
        }
        System.out.println("This file cannot be used!");
        System.exit(1);
    }

    private boolean findMatch(Room room, Room r) {
        String[] directions = {"N", "S", "E", "W"};
        for (String direction : directions) {
            int loc = r.getDoorLocation(direction);
            if (loc == -1) {
                Door door = new Door();
                door.connectRoom(r);
                r.setDoor(direction, 1, door);
                door.connectRoom(room);
                room.setDoor(getOpposingDirection(direction), 1, door);
                return true;
            }
        }
        return false;
    }

    /**
     * Gets an item based off its id.
     *
     * @param id as the id of the item
     * @return the item with the associated id
     */
    public Item getItem(int id) {
        for (Item item : getItems()) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    /**
     * Sets up doors for each room.
     *
     * @param createRoom as the room being created
     * @param roomMap    as the parsed data for the room
     */
    private void setDoors(Room createRoom, Map<String, String> roomMap) {
        String[] directions = {"N", "S", "E", "W"};
        for (String direction : directions) {
            int doorPos = Integer.parseInt(roomMap.get(direction));
            if (doorPos != -1) {
                Room connectedRoom = getRoom(Integer.parseInt(roomMap.get(direction + "C")));
                if (connectedRoom != null) {
                    Door connectedDoor = connectedRoom.getDoor(getOpposingDirection(direction));
                    if (connectedDoor != null) {
                        connectedDoor.connectRoom(createRoom);
                        createRoom.setDoor(direction, doorPos, connectedDoor);
                    }
                } else {
                    Door door = new Door();
                    door.connectRoom(createRoom);
                    createRoom.setDoor(direction, doorPos, door);
                }
            }
        }
    }

    /**
     * Gets the opposite direction for a N S E W input.
     *
     * @param direction as the direction to be inverted
     * @return the opposing direction
     */
    private String getOpposingDirection(String direction) {
        switch (direction) {
            case "N":
                return "S";
            case "S":
                return "N";
            case "E":
                return "W";
            case "W":
                return "E";
            default:
                return "I";
        }
    }

    /**
     * Gets a room based off its id.
     *
     * @param id as the id of the room you want to get
     * @return the corresponding room
     */
    private Room getRoom(int id) {
        for (Room room : getRooms()) {
            if (room.getId() == id) {
                return room;
            }
        }
        return null;
    }

    /**
     * Look for an empty tile in a room.
     *
     * @param room as the room to look in
     * @return the point at which an empty tile is found
     */
    public static Point lookForEmptyTile(Room room) {
        Point point = new Point();
        for (int i = 1; i < room.getHeight() - 1; i++) {
            for (int j = 1; j < room.getWidth() - 1; j++) {
                point.x = j;
                point.y = i;
                if (!(room.isOnItem(point) || room.isPlayer(point))) {
                    return point;
                }
            }
        }
        return null;
    }

    /**
     * Returns all rooms.
     *
     * @return rooms
     */
    public ArrayList<Room> getRooms() {
        return rooms;
    }

    /**
     * Returns all items in the game.
     *
     * @return items
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Gets the current player in the game.
     *
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Displays all rooms.
     *
     * @return constructed rooms string
     */
    public String displayAll() {
        StringBuilder totalDisplay = new StringBuilder();
        for (Room room : getRooms()) {
            totalDisplay.append(room.displayRoom());
            totalDisplay.append("\n\n");
        }
        return totalDisplay.toString();
    }

    /**
     * Gets the next room to display.
     *
     * @return next room to display
     */
    public String getNextDisplay() {
        return getPlayer().getCurrentRoom().displayRoom();
    }

    /**
     * Moves the player.
     *
     * @param userInput as the input from the user
     * @return the movement made
     * @throws InvalidMoveException upon an invalid movement
     */

    public String makeMove(char userInput) throws InvalidMoveException {
        Room currentRoom = getPlayer().getCurrentRoom();
        Point newLoc = new Point(getPlayer().getXyLocation());
        moveUser(userInput, newLoc);
        if (doorCheck(currentRoom, newLoc)) {
            return "Transitioned rooms";
        }
        if (itemCheck(currentRoom, newLoc)) {
            return "Got item";
        }
        if (movedOnFloorCheck(currentRoom, newLoc)) {
            return "Moved";
        }
        throw new InvalidMoveException();
    }

    private boolean movedOnFloorCheck(Room currentRoom, Point newLoc) {
        if (!currentRoom.isWall(newLoc)) {
            getPlayer().setXyLocation(newLoc);
            return true;
        }
        return false;
    }

    private boolean itemCheck(Room currentRoom, Point newLoc) {
        if (!currentRoom.isWall(newLoc)) {
            if (currentRoom.isOnItem(newLoc)) {
                getPlayer().getInventory().addItem(currentRoom.getItem(newLoc));
                currentRoom.getRoomItems().remove(currentRoom.getItem(newLoc));
                getPlayer().setXyLocation(newLoc);
                return true;
            }
        }
        return false;
    }

    private boolean doorCheck(Room currentRoom, Point newLoc) {
        if (currentRoom.isOnDoor(newLoc)) {
            Door door = currentRoom.getDoor(currentRoom.getWallDir(newLoc));
            Room connectedRoom = door.getOtherRoom(currentRoom);
            getPlayer().setCurrentRoom(connectedRoom);
            currentRoom.setPlayer(null);
            connectedRoom.setPlayer(getPlayer());
            getPlayer().setXyLocation(connectedRoom.getDoorPoint(getOpposingDirection(currentRoom.getWallDir(newLoc))));
            incrementPlayerPos(getPlayer(), getOpposingDirection(currentRoom.getWallDir(newLoc)));
            prevRoomWidth = currentRoom.getWidth();
            prevRoomHeight = currentRoom.getHeight();
            roomTransition = true;
            return true;
        }
        return false;
    }

    private void moveUser(char userInput, Point newLoc) {
        if (userInput == Rogue.DOWN) {
            newLoc.y++;
        } else if (userInput == Rogue.UP) {
            newLoc.y--;
        } else if (userInput == Rogue.LEFT) {
            newLoc.x--;
        } else if (userInput == Rogue.RIGHT) {
            newLoc.x++;
        }
    }

    private void incrementPlayerPos(Player p, String dir) {
        Point newPoint = new Point(p.getXyLocation());
        if (dir.equalsIgnoreCase("N")) {
            newPoint.y++;
        } else if (dir.equalsIgnoreCase("S")) {
            newPoint.y--;
        } else if (dir.equalsIgnoreCase("E")) {
            newPoint.x--;
        } else if (dir.equalsIgnoreCase("W")) {
            newPoint.x++;
        }
        p.setXyLocation(newPoint);
    }

    /**
     * Checks for a room transition.
     *
     * @return whether there has been a room transition or not
     */
    public boolean isTransitioningRooms() {
        if (roomTransition) {
            roomTransition = false;
            return true;
        }
        return false;
    }

    /**
     * Clears screen upon room transition.
     *
     * @return blanks in place of a cleared room
     */
    public String printBlanks() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < prevRoomHeight * (2 * 2); i++) {
            for (int j = 0; j < prevRoomWidth * (2 * 2); j++) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }
}
