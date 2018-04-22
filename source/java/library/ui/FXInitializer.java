package library.ui;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import library.data.Library;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The starting point for the JavaFX GUI. Initializes the JavaFX system and starts the program.
 *
 * @author Srikavin Ramkumar
 */
public class FXInitializer extends Application {
    private Stage helpStage;
    private Stage primaryStage;
    private BorderPane borderPane = new BorderPane();
    private BaseController currentController;
    private PreferenceManager preferenceManager;
    private Menu menuController;
    private MenuBar menuBar;
    private Library library;
    private Path dataFilePath;

    public static void main(String[] args) {
        Application.launch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        library = new Library(null);
        preferenceManager = new PreferenceManager();

        //Load all fonts before initializing the program
        loadFonts();

        //Add app icons
        ObservableList<Image> icons = primaryStage.getIcons();
        icons.add(new Image(FXInitializer.class.getResourceAsStream("icons/icon@0.5x.png")));
        icons.add(new Image(FXInitializer.class.getResourceAsStream("icons/icon@2x.png")));
        icons.add(new Image(FXInitializer.class.getResourceAsStream("icons/icon@3x.png")));
        icons.add(new Image(FXInitializer.class.getResourceAsStream("icons/icon@4x.png")));

        //Load the menu items separately from the main content
        FXMLLoader menuLoader = new FXMLLoader(FXInitializer.class.getResource("Menu.fxml"));
        menuBar = menuLoader.load();
        menuController = menuLoader.getController();
        menuController.initialize(this, library);
        menuController.initialize(primaryStage);

        //Load the default content
        FXMLInfoHolder mainWindow = loadFile("MainWindow.fxml");
        Parent parent = mainWindow.parent;

        //Make sure to load the fxml file before requesting the controller
        MainWindow controller = (MainWindow) mainWindow.controller;
        this.currentController = controller;

        //Set FXInitializer to this object
        controller.initialize(this, library);
        controller.initializeData();

        //Set the center of the pane to the content
        borderPane.setCenter(parent);
        //Set the top of the pane to the menu bar
        borderPane.setTop(menuBar);
        //Set the content of the window to the pane
        primaryStage.setScene(new Scene(borderPane));
        //Set the title
        primaryStage.setTitle("Library Management - BookKeeper");
        //Show the window after the animation has begun
        controller.animateIn((e) -> primaryStage.show());

        borderPane.maxWidthProperty().bind(primaryStage.widthProperty());
        borderPane.maxHeightProperty().bind(primaryStage.heightProperty());

        //Initialize the help window
        FXMLInfoHolder help = loadFile("Help.fxml");
        help.controller.initialize(this, library);
        help.controller.initializeData();
        Parent root = help.parent;
        helpStage = new Stage();
        helpStage.setTitle("Help");
        helpStage.setScene(new Scene(root, 450, 455));
        helpStage.setMinHeight(600);
        helpStage.setMinWidth(500);

        primaryStage.setMinWidth(685);
        primaryStage.setMinHeight(460);
    }

    /**
     * Load the specified data file and reset the application window to the main screen
     *
     * @param path The data file path to load
     *
     * @throws IOException If the path does not exist, an IOException may be thrown
     */
    public void loadFile(Path path) throws IOException {
        this.library = new Library(path);
        this.preferenceManager = new PreferenceManager(path);
        setContent("MainWindow.fxml");
        menuController.initialize(this, library);
        dataFilePath = path;
    }

    public void saveDataFile() throws IOException {
        saveDataFileTo(dataFilePath);
    }

    public void saveDataFileTo(Path path) throws IOException {
        dataFilePath = path;

        //If either the preference or the library data has been modified, create a backup of both before overwriting them
        //"Dynamic Backup"
        if (Files.isRegularFile(dataFilePath) && (library.isModified() || preferenceManager.isModified())) {
            //Generate the timestamp
            final DateTimeFormatter saveFileFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-SS");
            String timestamp = LocalDateTime.now().format(saveFileFormatter);

            //Dynamic backup; saves last data files to new files appended with the current timestamp
            library.saveTo(dataFilePath, timestamp);
            preferenceManager.saveTo(dataFilePath, timestamp);
        }
        //Save the current data
        library.saveTo(path);
        preferenceManager.saveTo(path);
    }

    public Node loadNode(String fxFile, Object controllerInstance) {
        try {
            FXMLLoader loader = new FXMLLoader(FXInitializer.class.getResource(fxFile));
            loader.setController(controllerInstance);
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load a JavaFX file and return the stage it is on
     *
     * @param fxFile             The fx file to load
     * @param controllerInstance The controller to set on the loaded fx file
     * @param <T>                The type of the controller
     *
     * @return The stage the fx file was loaded on to
     */
    public <T> Stage getDialog(String fxFile, T controllerInstance) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(FXInitializer.class.getResource(fxFile));
            loader.setController(controllerInstance);
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            return stage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load all fonts needed to load the application
     */
    private void loadFonts() {
        Font.loadFont(FXInitializer.class.getResourceAsStream("font/Roboto-Light.ttf"), 10);
        Font.loadFont(FXInitializer.class.getResourceAsStream("font/MaterialIcons-Regular.ttf"), 10);
        Font.loadFont(FXInitializer.class.getResourceAsStream("font/Roboto-Bold.ttf"), 10);
        Font.loadFont(FXInitializer.class.getResourceAsStream("font/Roboto_Condensed_Regular.ttf"), 10);
        Font.loadFont(FXInitializer.class.getResourceAsStream("font/RobotoCondensed-Bold.ttf"), 10);
    }

    /**
     * This changes the center of the main window pane to the specified fxml file.
     * If an error occurs, it is caught and an error dialog is displayed.
     * The controller of the specified file must be defined inside the FXML and the controller
     * must extend {@link BaseController}. The FXInitializer is set to this instance through
     * {@link BaseController#initialize(FXInitializer, Library)}
     *
     * @param fxmlFile The .fxml file containing the content to display on the window.
     */
    public void setContent(String fxmlFile) {
        try {
            //Load the specified fxml file
            FXMLInfoHolder loadedCache = loadFile(fxmlFile);

            //Get the parent node from the file
            Parent content = loadedCache.parent;

            //Set the FXInitializer of the controller to this object.
            BaseController controller = loadedCache.controller;
            controller.initialize(this, library);
            controller.initializeData();

            if (preferenceManager.getValueAsBoolean("use_transitions", true)) {
                //Set a callback after the animation has finished
                this.currentController.animateOut((e) ->
                        controller.animateIn((event) -> changeContent(content, controller)));
            } else {
                changeContent(content, controller);
            }
        } catch (Exception e) {
            //Display an error message if an Exception occurs
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Request");
            alert.setHeaderText("There was an error in your request.");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void changeContent(Node content, BaseController controller) {
        //Set the center of the pane to the content loaded
        borderPane.setCenter(content);
        //Keep the menu on top
        borderPane.setTop(null);
        borderPane.setTop(menuBar);
        //Set the current controller to the new content's controller
        this.currentController = controller;
    }

    /**
     * Loads the content specified in the file if it has not been cached.
     *
     * @param fileName .fxml file to load
     *
     * @return The content contained in the file. The same as the result of {@link FXMLLoader#load()}.
     *
     * @throws IOException If an error occurs when opening the file.
     */
    private FXMLInfoHolder loadFile(String fileName) throws IOException {
        //Load only if it has not been previously loaded
        FXMLLoader loader = new FXMLLoader(FXInitializer.class.getResource(fileName));

        //Save the loaded content into the caches
        Parent loadedParent = loader.load();
        return new FXMLInfoHolder(loader.getController(), loadedParent);
    }

    /**
     * Opens the help window containing user help/documentation.
     */
    public void loadHelp() {
        //Un-minimize the window
        helpStage.setIconified(false);
        //Maximize window
        helpStage.setMaximized(true);
        //Open the window if it isn't open
        helpStage.show();
        //Bring the window to the front of all other windows
        helpStage.toFront();
    }

    /**
     * Gets the stage containing the main window content
     *
     * @return The stage containing the main window content
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    /**
     * This class holds the controller and root element of a loaded JavaFX file.
     */
    private static class FXMLInfoHolder {
        BaseController controller;
        Parent parent;

        FXMLInfoHolder(BaseController controller, Parent parent) {
            this.controller = controller;
            this.parent = parent;
        }
    }
}
