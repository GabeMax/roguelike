package rogue;

import java.io.Serializable;
import java.util.ArrayList;

public class Door implements Serializable {

    private ArrayList<Room> connectedRooms = new ArrayList<>();

    /**
     * Default door constructor.
     */
    public Door() {

    }

    /**
     * Connects a room to the door.
     *
     * @param r as the room to connect
     */
    public void connectRoom(Room r) {
        if (connectedRooms.size() < 2) {
            connectedRooms.add(r);
        }
    }

    /**
     * Gets the two connected rooms for the door.
     *
     * @return the list of two rooms
     */
    public ArrayList<Room> getConnectedRooms() {
        return connectedRooms;
    }

    /**
     * Gets the door in the opposing room attached.
     *
     * @param currentRoom as the current room
     * @return the other room the door is attached too
     */
    public Room getOtherRoom(Room currentRoom) {
        for (Room room : getConnectedRooms()) {
            if (room != currentRoom) {
                return room;
            }
        }
        return null;
    }
}
