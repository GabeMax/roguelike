package rogue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RogueParser implements Serializable {

    private ArrayList<Map<String, String>> rooms = new ArrayList<>();
    private ArrayList<Map<String, String>> items = new ArrayList<>();
    private ArrayList<Map<String, String>> itemLocations = new ArrayList<>();
    private Map<Integer, Map<String, String>> itemMap = new HashMap<>();
    private HashMap<String, Character> symbols = new HashMap<>();

    /**
     * Default constructor.
     */
    public RogueParser() {

    }

    /**
     * Constructor that takes filename and sets up the object.
     *
     * @param filename (String) name of file that contains file location for rooms and symbols
     */
    public RogueParser(String filename) {
        parse(filename);
    }

    /**
     * Gets all items that can exist in the game.
     *
     * @return a map that maps an item id to any existing item
     */
    public Map<Integer, Map<String, String>> getAllPossibleItems() {
        return itemMap;
    }

    /**
     * Gets the items for a specific room based upon the rooms id.
     *
     * @param roomId as the id of the room
     * @return a list of all item maps for that room
     */
    public ArrayList<Map<String, String>> getRoomItems(int roomId) {
        ArrayList<Map<String, String>> roomItems = new ArrayList<>();
        for (Map<String, String> roomItem : getItems()) {
            if (String.valueOf(roomId).equalsIgnoreCase(roomItem.get("room"))) {
                roomItems.add(roomItem);
            }
        }
        return roomItems;
    }

    /**
     * Gets all items in the game.
     * @return a list of all parsed items
     */
    public ArrayList<Map<String, String>> getItems() {
        return items;
    }

    /**
     * Gets all parsed rooms in the game.
     * @return all parsed rooms
     */
    public ArrayList<Map<String, String>> getRooms() {
        return rooms;
    }

    /**
     * Get the character for a symbol.
     *
     * @param symbolName (String) Symbol Name
     * @return (Character) Display character for the symbol
     */
    public Character getSymbol(String symbolName) {
        if (symbols.containsKey(symbolName)) {
            return symbols.get(symbolName);
        }
        return null;
    }

    /**
     * Read the file containing the file locations.
     *
     * @param filename (String) Name of the file
     */
    private void parse(String filename) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject configurationJSON = (JSONObject) parser.parse(new FileReader(filename));
            String roomsFileLocation = configurationJSON.get("Rooms").toString();
            String symbolsFileLocation = configurationJSON.get("Symbols").toString();
            JSONObject roomsJSON = (JSONObject) parser.parse(new FileReader(roomsFileLocation));
            JSONObject symbolsJSON = (JSONObject) parser.parse(new FileReader(symbolsFileLocation));
            extractInfo(roomsJSON, symbolsJSON);
            itemMap = constructItemMap(roomsJSON);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find file named: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("Error parsing JSON file");
        }
    }

    private void extractInfo(JSONObject roomsJSON, JSONObject symbolsJSON) {
        extractRoomInfo(roomsJSON);
        extractItemInfo(roomsJSON);
        extractSymbolInfo(symbolsJSON);
    }

    /**
     * Get the symbol information.
     *
     * @param symbolsJSON (JSONObject) Contains information about the symbols
     */
    private void extractSymbolInfo(JSONObject symbolsJSON) {
        JSONArray symbolsJSONArray = (JSONArray) symbolsJSON.get("symbols");
        for (Object o : symbolsJSONArray) {
            JSONObject symbolObj = (JSONObject) o;
            symbols.put(symbolObj.get("name").toString(), String.valueOf(symbolObj.get("symbol")).charAt(0));
        }
    }

    /**
     * Get the room information.
     *
     * @param roomsJSON (JSONObject) Contains information about the rooms
     */
    private void extractRoomInfo(JSONObject roomsJSON) {
        JSONArray roomsJSONArray = (JSONArray) roomsJSON.get("room");
        for (Object o : roomsJSONArray) {
            rooms.add(singleRoom((JSONObject) o));
        }
    }

    /**
     * Get a room's information.
     *
     * @param roomJSON (JSONObject) Contains information about one room
     * @return (Map < String, String >) Contains key/values that has information about the room
     */
    private Map<String, String> singleRoom(JSONObject roomJSON) {
        HashMap<String, String> room = new HashMap<>();
        initializeRoomData(room, roomJSON);
        JSONArray doorArray = (JSONArray) roomJSON.get("doors");
        for (Object o : doorArray) {
            JSONObject doorObj = (JSONObject) o;
            String dir = String.valueOf(doorObj.get("dir"));
            String cDir = dir + "C";
            room.replace(dir, doorObj.get("wall_pos").toString());
            room.replace(cDir, doorObj.get("con_room").toString());
        }
        JSONArray lootArray = (JSONArray) roomJSON.get("loot");
        for (Object o : lootArray) {
            itemLocations.add(itemPosition((JSONObject) o, roomJSON.get("id").toString()));
        }
        return room;
    }

    private void initializeRoomData(HashMap<String, String> room, JSONObject roomJSON) {
        room.put("id", roomJSON.get("id").toString());
        room.put("start", roomJSON.get("start").toString());
        room.put("height", roomJSON.get("height").toString());
        room.put("width", roomJSON.get("width").toString());
        room.put("E", "-1");
        room.put("N", "-1");
        room.put("S", "-1");
        room.put("W", "-1");
        room.put("EC", "-1");
        room.put("NC", "-1");
        room.put("SC", "-1");
        room.put("WC", "-1");
    }

    /**
     * Create a map for information about an item in a room.
     *
     * @param lootJSON (JSONObject) Loot key from the rooms file
     * @param roomID   (String) Room id value
     * @return (Map < String, String >) Contains information about the item, where it is and what room
     */
    private Map<String, String> itemPosition(JSONObject lootJSON, String roomID) {
        HashMap<String, String> loot = new HashMap<>();
        loot.put("room", roomID);
        loot.put("id", lootJSON.get("id").toString());
        loot.put("x", lootJSON.get("x").toString());
        loot.put("y", lootJSON.get("y").toString());
        return loot;
    }

    /**
     * Get the item information out of the item json array.
     * @param roomsJSON as the jsonarray that holds all items
     */
    private void extractItemInfo(JSONObject roomsJSON) {
        JSONArray itemsJSONArray = (JSONArray) roomsJSON.get("items");
        for (Object o : itemsJSONArray) {
            items.add(singleItem((JSONObject) o));
        }
    }

    /**
     * Get a single item from its JSON object.
     *
     * @param itemsJSON (JSONObject) JSON version of an item
     * @return (Map < String, String >) Contains information about a single item
     */
    private Map<String, String> singleItem(JSONObject itemsJSON) {
        HashMap<String, String> item = new HashMap<>();
        item.put("id", itemsJSON.get("id").toString());
        item.put("name", itemsJSON.get("name").toString());
        item.put("type", itemsJSON.get("type").toString());
        item.put("description", itemsJSON.get("description").toString());
        for (Map<String, String> itemLocation : itemLocations) {
            if (itemLocation.get("id").equals(item.get("id"))) {
                item.put("room", itemLocation.get("room"));
                item.put("x", itemLocation.get("x"));
                item.put("y", itemLocation.get("y"));
                break;
            }
        }
        return item;
    }

    /**
     * Maps out all possibly existing items in the game to a map for easy lookup.
     *
     * @param roomsJSON as the items jsonarray in the file
     * @return the associated item id mapped to its name type and description
     */
    private Map<Integer, Map<String, String>> constructItemMap(JSONObject roomsJSON) {
        JSONArray itemArray = (JSONArray) roomsJSON.get("items");
        Map<Integer, Map<String, String>> bigMap = new HashMap<>();
        for (Object o : itemArray) {
            Map<String, String> itemInfo = new HashMap<>();
            JSONObject jsonObject = (JSONObject) o;
            int id = Integer.parseInt(jsonObject.get("id").toString());
            itemInfo.put("name", jsonObject.get("name").toString());
            itemInfo.put("type", jsonObject.get("type").toString());
            itemInfo.put("description", jsonObject.get("description").toString());
            bigMap.put(id, itemInfo);
        }
        return bigMap;
    }

}
