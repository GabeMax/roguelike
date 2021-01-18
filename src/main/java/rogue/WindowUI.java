package rogue;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.TerminalPosition;

import java.awt.Container;
import java.awt.BorderLayout;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.JFileChooser;

public class WindowUI extends JFrame implements Serializable {

    private SwingTerminal terminal;
    private TerminalScreen screen;
    public static final int WIDTH = 700;
    public static final int HEIGHT = 800;
    public static final int COLS = 25;
    public static final int ROWS = 24;
    private static final int NAME_LENGTH = 20;
    private final char startCol = 0;
    private final char msgRow = 1;
    private final char roomRow = 3;
    private Rogue rogueGame;
    private Container contentPane;
    private JLabel descLabel;
    private JLabel inventoryLabel;
    private JLabel playerLabel;

    /**
     * Constructor.
     **/
    public WindowUI() {
        super("my awesome game");
        contentPane = getContentPane();
        setWindowDefaults(getContentPane());
        setUpPanels();
        pack();
        start();
    }

    private void setWindowDefaults(Container contentPaneContainer) {
        setTitle("Rogue!");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        contentPaneContainer.setLayout(new BorderLayout());
    }

    private void setTerminal() {
        JPanel terminalPanel = new JPanel();
        terminal = new SwingTerminal();
        terminalPanel.add(terminal);
        contentPane.add(terminalPanel, BorderLayout.CENTER);
    }

    private void setUpPanels() {
        descLabel = setUpLabelPanel();
        inventoryLabel = setUpInventoryPanel();
        playerLabel = setUpPlayerNamePanel();
        setUpMenu();
        setTerminal();
    }

    private JLabel setUpInventoryPanel() {
        JPanel invPanel = new JPanel();
        Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        invPanel.setBorder(border);
        JLabel text = new JLabel("");
        invPanel.add(text);
        contentPane.add(invPanel, BorderLayout.WEST);
        return text;
    }

    private JLabel setUpPlayerNamePanel() {
        JPanel playerPanel = new JPanel();
        Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        playerPanel.setBorder(border);
        JLabel playerName = new JLabel("");
        playerPanel.add(playerName);
        contentPane.add(playerPanel, BorderLayout.EAST);
        return playerName;
    }

    private JLabel setUpLabelPanel() {
        JPanel descPanel = new JPanel();
        Border prettyLine = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        descPanel.setBorder(prettyLine);
        JLabel descriptionLabel = new JLabel("Game Output: ");
        descPanel.add(descriptionLabel);
        contentPane.add(descPanel, BorderLayout.SOUTH);
        return descriptionLabel;
    }

    private void handleNameChange() {
        String s = JOptionPane.showInputDialog("New Player Name");
        if (s != null) {
            if (s.length() > NAME_LENGTH) {
                updateDescriptionOutput("That name is too long!");
            } else {
                rogueGame.getPlayer().setName(s);
                updatePlayerName();
                updateDescriptionOutput("Updated Player name to " + s);
            }
        }
    }

    private void setUpMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem changeName = new JMenuItem("Change name");
        fileMenu.add(changeName);
        changeName.addActionListener(e -> handleNameChange());
        JMenuItem openJSON = new JMenuItem("Open Json");
        fileMenu.add(openJSON);
        openJSON.addActionListener(e -> loadJSON());
        JMenuItem openFile = new JMenuItem("Open Save");
        fileMenu.add(openFile);
        openFile.addActionListener(e -> load());
        JMenuItem saveGame = new JMenuItem("Save game");
        fileMenu.add(saveGame);
        saveGame.addActionListener(e -> save());
    }

    private void start() {
        try {
            screen = new TerminalScreen(terminal);
            screen.setCursorPosition(TerminalPosition.TOP_LEFT_CORNER);
            screen.startScreen();
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints a string to the screen starting at the indicated column and row.
     *
     * @param toDisplay the string to be printed
     * @param column    the column in which to start the display
     * @param row       the row in which to start the display
     **/
    public void putString(String toDisplay, int column, int row) {
        Terminal t = screen.getTerminal();
        try {
            t.setCursorPosition(column, row);
            for (char ch : toDisplay.toCharArray()) {
                t.putCharacter(ch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the message at the top of the screen for the user.
     *
     * @param msg the message to be displayed
     **/
    public void setMessage(String msg) {
        putString("                                                ", 1, 1);
        putString(msg, startCol, msgRow);
    }

    /**
     * Redraws the whole screen including the room and the message.
     *
     * @param message the message to be displayed at the top of the room
     * @param room    the room map to be drawn
     **/
    public void draw(String message, String room) {

        try {
            setMessage(message);
            putString(room, startCol, roomRow);
            screen.refresh();
        } catch (IOException e) {

        }

    }

    /**
     * Obtains input from the user and returns it as a char.  Converts arrow
     * keys to the equivalent movement keys in rogue.
     *
     * @return the ascii value of the key pressed by the user
     **/
    public char getInput() {
        KeyStroke keyStroke = null;
        while (keyStroke == null) {
            try {
                keyStroke = screen.pollInput();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getKeyStroke(keyStroke);
    }

    private char getKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.ArrowDown) {
            return Rogue.DOWN;  //constant defined in rogue
        } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
            return Rogue.UP;
        } else if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
            return Rogue.LEFT;
        } else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
            return Rogue.RIGHT;
        } else {
            return keyStroke.getCharacter();
        }
    }

    private static void handleInvalidMove(Rogue theGame, WindowUI theGameUI) {
        String message = "You appear to be trying to move inside a wall";
        theGameUI.setMessage(message);
        theGameUI.updateDescriptionOutput(message);
        theGameUI.draw(Rogue.BLANK_OFFSET + message, theGame.getNextDisplay());
    }

    private static void drawAndUpdateGame(WindowUI theGameUI, Rogue theGame, String message) {
        theGameUI.draw(Rogue.BLANK_OFFSET + message, theGame.getNextDisplay());
        theGameUI.updateInventoryDisplay();
        theGameUI.updateDescriptionOutput(message);
    }

    private static void checkInput(WindowUI theGameUI, char userInput) {
        while (userInput != 'q') {
            userInput = theGameUI.getInput();
            Rogue theGame = theGameUI.rogueGame;
            String message;
            try {
                message = theGameUI.handleInventoryCall(theGame, userInput);
                if (message == null) {
                    message = theGame.makeMove(userInput);
                }
                if (theGame.isTransitioningRooms()) {
                    theGameUI.draw(message, theGame.printBlanks());
                }
                drawAndUpdateGame(theGameUI, theGame, message);
            } catch (InvalidMoveException badMove) {
                handleInvalidMove(theGame, theGameUI);
            }
        }
        System.exit(0);
    }

    private String[] getOptions(Rogue theGame) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < theGame.getPlayer().getInventory().getItems().size(); i++) {
            list.add(i + 1 + " " + theGame.getPlayer().getInventory().getItems().get(i).getName());
        }
        return list.toArray(new String[0]);
    }

    private String handleEatCall(Rogue theGame) {
        String[] options = getOptions(theGame);
        String s = (String) JOptionPane.showInputDialog(null, "Select an item to eat", "",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (s != null) {
            Item item = theGame.getPlayer().getInventory().getItems().get(Integer.parseInt(s.split(" ")[0]) - 1);
            if (item instanceof Edible) {
                String message = ((Edible) item).eat(theGame.getPlayer());
                theGame.getPlayer().getInventory().getItems().remove(item);
                updateInventoryDisplay();
                return message;
            } else {
                return "That item cannot be eaten!";
            }
        }
        return null;
    }

    private String handleWearCall(Rogue theGame) {
        String[] options = getOptions(theGame);
        String s = (String) JOptionPane.showInputDialog(null, "Select an item to wear", "",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (s != null) {
            Item item = theGame.getPlayer().getInventory().getItems().get(Integer.parseInt(s.split(" ")[0]) - 1);
            if (item instanceof Wearable) {
                String message = ((Wearable) item).wear(theGame.getPlayer());
                updateInventoryDisplay();
                return message;
            } else {
                return "That item cannot be worn!";
            }
        }
        return null;
    }

    private String handleTossCall(Rogue theGame) {
        String[] options = getOptions(theGame);
        String s = (String) JOptionPane.showInputDialog(null, "Select an item to toss", "",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (s != null) {
            Item item = theGame.getPlayer().getInventory().getItems().get(Integer.parseInt(s.split(" ")[0]) - 1);
            if (item instanceof Tossable) {
                String message = ((Tossable) item).toss(theGame.getPlayer());
                theGame.getPlayer().getInventory().getItems().remove(item);
                updateInventoryDisplay();
                return message;
            } else {
                return "That item cannot be tossed!";
            }
        }
        return null;
    }

    private String handleInventoryCall(Rogue theGame, char userInput) {
        if (theGame.getPlayer().getInventory().getItems().size() != 0) {
            if (userInput == 'e') {
                return handleEatCall(theGame);
            } else if (userInput == 'w') {
                return handleWearCall(theGame);
            } else if (userInput == 't') {
                return handleTossCall(theGame);
            }
        }
        return null;
    }

    /**
     * Updates the inventory with a new string.
     */
    public void updateInventoryDisplay() {
        inventoryLabel.setText(rogueGame.getPlayer().getInventory().displayInventory(rogueGame.getPlayer()));
    }

    private void updatePlayerName() {
        playerLabel.setText("<html> Player Name: <br>" + rogueGame.getPlayer().getName());
    }

    private void updateDescriptionOutput(String message) {
        descLabel.setText("Game Output: " + message);
    }

    /**
     * Saves the game into a file.
     */
    public void save() {
        JFileChooser j = new JFileChooser();
        j.setCurrentDirectory(new File(System.getProperty("user.dir")));
        j.showSaveDialog(null);
        if (j.getSelectedFile() != null) {
            try {
                FileOutputStream outputStream = new FileOutputStream(j.getSelectedFile().toString());
                ObjectOutputStream outputDest = new ObjectOutputStream(outputStream);
                outputDest.writeObject(rogueGame);
                draw(Rogue.BLANK_OFFSET + "Saved file", rogueGame.getNextDisplay());
                updateDescriptionOutput("Saved file");
                outputDest.close();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads a game from a file.
     */
    public void load() {
        JFileChooser j = new JFileChooser();
        j.setCurrentDirectory(new File(System.getProperty("user.dir")));
        j.showSaveDialog(null);
        if (j.getSelectedFile() != null) {
            String filename = j.getSelectedFile().toString();
            Rogue theClass = null;
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
                theClass = (Rogue) in.readObject();
            } catch (Exception e) {
                updateDescriptionOutput("That is not a valid save file!");
            }
            loadRogueGame(theClass);
        }
    }

    private void loadRogueGame(Rogue theClass) {
        if (theClass != null) {
            draw(Rogue.BLANK_OFFSET + "Loaded file", rogueGame.printBlanks());
            rogueGame = theClass;
            draw(Rogue.BLANK_OFFSET + "Loaded file", rogueGame.getNextDisplay());
            updateDescriptionOutput("Loaded file");
            updateInventoryDisplay();
            updatePlayerName();
        } else {
            updateDescriptionOutput("That is not a valid save file!");
        }
    }

    /**
     * Loads a new game from a specific json file.
     */
    public void loadJSON() {
        JFileChooser j = new JFileChooser();
        j.setCurrentDirectory(new File(System.getProperty("user.dir")));
        j.showSaveDialog(null);
        if (j.getSelectedFile() != null) {
            draw(Rogue.BLANK_OFFSET + "Loaded file", rogueGame.printBlanks());
            try {
                RogueParser parser = new RogueParser(j.getSelectedFile().toString());
                Rogue newRogue = new Rogue(parser);
                newRogue.getNextDisplay();
                rogueGame = newRogue;
                draw(Rogue.BLANK_OFFSET + "Loaded file", rogueGame.getNextDisplay());
                updateDescriptionOutput("Loaded file");
                updateInventoryDisplay();
            } catch (Exception e) {
                updateDescriptionOutput("Invalid json file! Must be formatted like a FileLocations File!");
            }
        }
    }

    /**
     * The controller method for making the game logic work.
     *
     * @param args command line parameters
     **/
    public static void main(String[] args) {
        char userInput = 'h';
        String message;
        Rogue theGame = new Rogue(new RogueParser("fileLocations.json"));
        WindowUI theGameUI = new WindowUI();
        theGameUI.rogueGame = theGame;
        theGame.setPlayer(new Player("Player"));
        message = Rogue.BLANK_OFFSET + "Welcome to my Rogue game";
        theGameUI.updateInventoryDisplay();
        theGameUI.draw(message, theGame.getNextDisplay());
        theGameUI.setVisible(true);
        theGameUI.updatePlayerName();
        theGameUI.updateDescriptionOutput(message);
        checkInput(theGameUI, userInput);
    }
}
