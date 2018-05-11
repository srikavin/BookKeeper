package library;

import javafx.application.Application;
import library.ui.FXInitializer;

/**
 * The starting point of the application
 * @author Srikavin Ramkumar
 */
public class Main {
    /**
     * The starting point of the application
     *
     * @param args Any given command-line arguments
     */
    public static void main(String[] args){
        //Start the JavaFX GUI
        Application.launch(FXInitializer.class);
    }
}
