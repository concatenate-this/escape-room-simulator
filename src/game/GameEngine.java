package game;

import entities.Player;
import entities.Room;
import items.*;
import interactables.*;
import puzzles.*;
import java.util.Scanner;

public class GameEngine {
    private Player player;
    private Room currentRoom;
    private CommandHandler commandHandler;
    private boolean gameOver;
    private boolean escaped;
    private Door exitDoor;    // ← add this
    private Locker locker;    // ← add this


    public GameEngine() {
        this.commandHandler = new CommandHandler();
        this.gameOver = false;
        this.escaped = false;
    }
    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║       WELCOME TO THE ESCAPE ROOM      ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.print("\nEnter your name: ");
        String name = scanner.nextLine();
        player = new Player(name);
        setupRoom();
        System.out.println("\n" + player.getName() + ", you wake up in a mysterious room...");
        System.out.println("Type 'help' for commands.\n");
        // Main game loop: Input → Process → Output
        while (!gameOver) {
            currentRoom.describe();
            System.out.print("\n> ");
            String input = scanner.nextLine();
            processCommand(input);
            checkEscapeCondition();
        }
        if (escaped) {
            System.out.println("\n🎉 CONGRATULATIONS, " + player.getName().toUpperCase() + "! YOU ESCAPED! 🎉");
        }
        scanner.close();
    }
    private void setupRoom() {
        currentRoom = new Room("The Locked Chamber",
                "A dim room with stone walls. There's a faint musty smell.");
        // Items
        Key bronzeKey = new Key("Bronze Key", "A small bronze key", "bronze");
        Key goldenKey = new Key("Golden Key", "A shiny golden key", "golden");
        Clue noteClue = new Clue("Torn Note", "A crumpled piece of paper", "The code is the year the Titanic sank.");
        currentRoom.addItem(bronzeKey);
        currentRoom.addItem(noteClue);
        // Locker containing the golden key
        locker = new Locker("Metal Locker", "bronze", goldenKey);
        currentRoom.addUnlockable(locker);
        // Exit door
        exitDoor = new Door("Exit Door", "golden");

        // Puzzles
        RiddlePuzzle riddle = new RiddlePuzzle(
                "Stone Tablet",
                "I have keys but no locks. I have space but no room. You can enter but can't go inside. What am I?",
                "keyboard",
                "A secret compartment opens, revealing the locker!"
        );
        currentRoom.addPuzzle(riddle);
        CodePuzzle codeLock = new CodePuzzle(
                "Digital Keypad",
                "Enter the 4-digit code:",
                "1912",
                "Beep! The exit door becomes visible!"
        );
        currentRoom.addPuzzle(codeLock);
    }
    private void processCommand(String input) {
        commandHandler.parse(input);
        String action = commandHandler.getAction();
        String target = commandHandler.getTarget();
        switch (action) {
            case "help":
                showHelp();
                break;
            case "look":
                // Room describes itself on each loop
                break;
            case "take":
            case "grab":
            case "pick":
                takeItem(target);
                break;
            case "use":
                useItem(target);
                break;
            case "unlock":
                unlockTarget(target);
                break;
            case "open":
            case "interact":
                interactWith(target);
                break;
            case "solve":
            case "answer":
                solvePuzzle(target);
                break;
            case "inventory":
            case "inv":
            case "i":
                player.showInventory();
                break;
            case "inspect":
            case "examine":
                inspect(target);
                break;
            case "escape":
            case "exit":
            case "leave":
                attemptEscape();
                break;
            case "quit":
                System.out.println("Thanks for playing!");
                gameOver = true;
                break;
            default:
                System.out.println("Unknown command. Type 'help' for options.");
        }
    }
    private void showHelp() {
        System.out.println("\n=== Commands ===");
        System.out.println("  look           - Examine the room");
        System.out.println("  take [item]    - Pick up an item");
        System.out.println("  use [item]     - Use an item from inventory");
        System.out.println("  unlock [thing] - Use a key on something");
        System.out.println("  open [thing]   - Interact with something");
        System.out.println("  inspect [item] - Examine an item closely");
        System.out.println("  solve [puzzle] - Attempt to solve a puzzle");
        System.out.println("  inventory      - View your items");
        System.out.println("  escape         - Try to leave");
        System.out.println("  quit           - Exit game");
    }
    private void takeItem(String itemName) {
        Item item = currentRoom.takeItem(itemName);
        if (item != null) {
            player.addItem(item);
        } else {
            System.out.println("There's no " + itemName + " here to take.");
        }
    }
    private void useItem(String itemName) {
        Item item = player.getItem(itemName);
        if (item != null) {
            System.out.println(item.use());
        } else {
            System.out.println("You don't have a " + itemName + ".");
        }
    }
    private void unlockTarget(String targetName) {
        Scanner scanner = new Scanner(System.in);
        Unlockable target = currentRoom.getUnlockable(targetName);
        if (target == null) {
            System.out.println("There's nothing called " + targetName + " to unlock.");
            return;
        }
        System.out.print("Which key do you want to use? ");
        String keyName = scanner.nextLine();
        Item item = player.getItem(keyName);
        if (item instanceof Key) {
            String result = target.unlock((Key) item);
            System.out.println(result);
            // If locker unlocked, make contents available
            if (target instanceof Locker && !target.isLocked()) {
                Locker locker = (Locker) target;
                Item contents = locker.takeContents();
                if (contents != null) {
                    currentRoom.addItem(contents);
                    System.out.println("Something fell out: " + contents.getName());
                }
            }
        } else {
            System.out.println("That's not a key.");
        }
    }
    private void interactWith(String targetName) {
        Unlockable target = currentRoom.getUnlockable(targetName);
        if (target != null) {
            System.out.println(target.interact());
        } else {
            System.out.println("You can't interact with that.");
        }
    }
    private void solvePuzzle(String puzzleName) {
        Scanner scanner = new Scanner(System.in);
        Puzzle puzzle = currentRoom.getPuzzle(puzzleName);
        if (puzzle == null) {
            System.out.println("There's no puzzle called " + puzzleName + ".");
            return;
        }
        System.out.println("\n" + puzzle.getPrompt());
        System.out.print("Your answer: ");
        String answer = scanner.nextLine();
        String result = puzzle.attemptSolve(answer);
        System.out.println(result);
        // Trigger effects based on puzzle
        if (puzzle.isSolved()) {
            if (puzzle.getName().equals("Stone Tablet")) {
                // Reveal locker is now interactable
                System.out.println("You notice a metal locker behind a panel!");
            }
            if (puzzle.getName().equals("Digital Keypad")) {
                currentRoom.setDoorVisible(true);
                currentRoom.addUnlockable(exitDoor);
                System.out.println("A hidden door slides into view!");
            }
        }
    }
    private void inspect(String itemName) {
        // Check room first
        Puzzle puzzle = currentRoom.getPuzzle(itemName);
        if (puzzle != null) {
            System.out.println(puzzle.getPrompt());
            return;
        }
        // Check inventory
        Item item = player.getItem(itemName);
        if (item != null) {
            System.out.println(item.use());
            return;
        }
        System.out.println("You don't see anything special about " + itemName + ".");
    }
    private void attemptEscape() {
        Unlockable door = currentRoom.getUnlockable("Exit Door");
        if (door != null && !door.isLocked()) {
            System.out.println("You push open the door and step into freedom!");
            escaped = true;
            gameOver = true;
        } else if (!currentRoom.isDoorVisible()) {
            System.out.println("You don't see any way out yet...");
        } else {
            System.out.println("The exit door is still locked!");
        }
    }
    private void checkEscapeCondition() {
        Unlockable door = currentRoom.getUnlockable("Exit Door");
        // Could add auto-win conditions here
    }
}