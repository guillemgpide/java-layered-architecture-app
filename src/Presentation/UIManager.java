package Presentation;

import java.util.InputMismatchException;
import java.util.Scanner;
/**
 * The UIManager class is responsible for managing the user interface interactions, including displaying messages,
 * presenting the main menu, managing product-related actions, managing shop-related actions, and collecting user input.
 *
 * @author : Bruno Bordoy, Guillem Gil
 * @version : 13/11/2023
 */
public class UIManager {
    /**
     * Displays a message with a new line.
     *
     * @param message The message to be displayed.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }
    /**
     * Displays a message without a new line.
     *
     * @param message The message to be displayed.
     */
    public void showMessageWithNoSpace(String message) {
        System.out.print(message);
    }
    /**
     * Presents the main menu to the user and collects their choice.
     *
     * @return The user's choice from the main menu.
     */
    public int askForMainMenu() {
        int selection;
        showMessage("\n\t1)\tManage Products\n\t2)\tManage Shops\n\t3)\tSearch Products\n\t4)\tList Shops\n\t5)\tYour Cart\n\n\t6)\tExit");
        selection = askForInt("\nChoose a Digital Shopping Experience: ", 1, 6);
        return selection;
    }
    /**
     * Collects an integer input from the user within a specified range.
     *
     * @param message The message prompting the user for input.
     * @param optMin  The minimum allowed value for the input.
     * @param optMax  The maximum allowed value for the input.
     * @return The validated integer input from the user.
     */
    public int askForInt(String message, int optMin, int optMax) {
        Scanner scanner = new Scanner(System.in);
        int option;
        showMessageWithNoSpace(message);
        try {
            do {
                option = scanner.nextInt();
                if (option > optMax || option < optMin) {
                    showMessage("\nPlease, select an option in range [" + optMin + ".." + optMax + "]:");
                }
            } while (option > optMax || option < optMin);
        } catch (InputMismatchException e) {
            showMessage("Input must be an integer");
            option = askForInt(message, optMin, optMax);
        }
        return option;
    }
    /**
     * Displays a closing message when the user exits the application.
     */
    public void closeShops() {
        showMessage("We hope to see you again!");
    }
    /**
     * Displays a presentation message when the user enters the application.
     */
    public void presentationToMall() {
        showMessage("        ________      ____");
        showMessage("  ___  / / ____/___  / __/_______ ");
        showMessage(" / _ \\/ / /   / __ \\/ /_/ ___/ _ \\");
        showMessage("/  __/ / /___/ /_/ / __/ /  /  __/");
        showMessage("\\___/_/\\____/\\____/_/ /_/   \\___/");
        showMessage("\nWelcome to elCofre Digital Shopping Experiences.\n");
    }
    /**
     * Presents the menu for managing product-related actions and collects the user's choice.
     *
     * @return The user's choice for managing products.
     */
    public int askForManageProductMenu() {
        int selection;
        showMessage("\n\t1)\tCreate a Product\n\t2)\tRemove a Product\n\n\t3)\tBack\n");
        selection = askForInt("Choose an option: ", 1, 3);
        return selection;
    }
    /**
     * Collects a string input from the user.
     *
     * @param message The message prompting the user for input.
     * @return The string input provided by the user.
     */
    public String askForString(String message) {
        Scanner scanner = new Scanner(System.in);
        showMessageWithNoSpace(message);
        return scanner.nextLine();
    }
    /**
     * Collects a float input from the user, handling potential exceptions.
     *
     * @param message The message prompting the user for input.
     * @return The validated float input from the user.
     */
    public float askForFloat(String message) {
        Scanner scanner = new Scanner(System.in);
        showMessageWithNoSpace(message);
        while (true) {
            try {
                String input = scanner.next();
                return Float.parseFloat(input.replace(",","."));
            } catch (NumberFormatException e) {
                showMessage("This is not a decimal number, try again: ");
            }
        }
    }
    /**
     * Collects a selected string option from the user, validating against specified options.
     *
     * @param productOrShop Indicates whether the options are for a product or a shop.
     * @param message       The message prompting the user for input.
     * @return The selected string option provided by the user.
     */
    public String askForSelectedStrings(boolean productOrShop, String message){
        String category = "not working";
        String character;
        Scanner scanner = new Scanner(System.in);
        do{
            showMessageWithNoSpace(message);
            character = scanner.nextLine();
        } while(!character.equals("A") && !character.equals("B") && !character.equals("C"));
        switch (character) {
            case "A" -> {
                if (productOrShop) {
                    category = "GENERAL";
                } else {
                    category = "MAXIMUM BENEFITS";
                }
            }
            case "B" -> {
                if (productOrShop) {
                    category = "REDUCED TAXES";
                } else {
                    category = "LOYALTY";
                }
            }
            case "C" -> {
                if (productOrShop) {
                    category = "SUPERREDUCED TAXES";
                } else {
                    category = "SPONSORED";
                }
            }
            default -> showMessageWithNoSpace("That is not a valid option!");
        }
        return category;
    }
    /**
     * Presents the menu for managing shop-related actions and collects the user's choice.
     *
     * @return The user's choice for managing shops.
     */
    public int askForManageShopMenu() {
        int selection;
        showMessage("\n\t1)\tCreate a Shop\n\t2)\tExpand a Shop's Catalogue\n\t3)\tReduce a Shop's Catalogue\n\n\t4)\tBack\n");
        selection = askForInt("Choose an option: ", 1, 4);
        return selection;
    }

    /**
     * Adapts a String so it has it first letter in mayus.
     * @param sentence the string that must be adapted.
     * @return the adapted String.
     */
    public String adaptStringToFirstMayus(String sentence) {
        sentence = sentence.toLowerCase();
        sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1);
        return sentence;
    }
}