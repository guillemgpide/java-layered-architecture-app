import Presentation.Controller;
/**
 * The Main class serves as the entry point for the Digital Shopping Experience application.
 * It initializes the controller and runs the main functionality of the application.
 */
public class Main {
    /**
     * The main method that initializes the controller and starts the application.
     *
     * @param args The command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.run();
    }
}