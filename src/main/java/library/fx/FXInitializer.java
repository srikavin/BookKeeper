package library.fx;

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
import java.nio.file.Path;

/**
 * The starting point for the JavaFX GUI. Initializes the JavaFX system and starts the program.
 *
 * @author Srikavin Ramkumar
 */
public class FXInitializer extends Application {
    private Stage helpStage;
    private BorderPane borderPane = new BorderPane();
    private BaseController currentController;
    private BaseController menuController;
    private MenuBar menuBar;
    private Library library;
    private boolean useTransitions = true;

    public static void main(String[] args) {
        Application.launch(FXInitializer.class, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        library = new Library(null);

        //Load all fonts before initializing the program
        loadFonts();

        //Add app icons
        ObservableList<Image> icons = primaryStage.getIcons();
        icons.add(new Image(getClass().getResourceAsStream("icons/icon@0.5x.png")));
        icons.add(new Image(getClass().getResourceAsStream("icons/icon@2x.png")));
        icons.add(new Image(getClass().getResourceAsStream("icons/icon@3x.png")));
        icons.add(new Image(getClass().getResourceAsStream("icons/icon@4x.png")));

        //Load the menu items separately from the main content
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("Menu.fxml"));
        menuBar = menuLoader.load();
        menuController = menuLoader.getController();
        menuController.initialize(this, library);

        //Load the default content
        FXMLCacheHolder cacheHolder = loadFile("MainWindow.fxml");
        Parent parent = cacheHolder.parent;

        //Make sure to load the fxml file before requesting the controller
        MainWindow controller = (MainWindow) cacheHolder.controller;
        this.currentController = controller;

        //Set FXInitializer to this object
        controller.initialize(this, library);


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

        //Initialize the help window
        Parent root = loadFile("Help.fxml").parent;
        helpStage = new Stage();
        helpStage.setTitle("Help");
        helpStage.setScene(new Scene(root, 450, 450));
        helpStage.setMinHeight(600);
        helpStage.setMinWidth(500);

        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
    }

    public void loadFile(Path path) throws IOException {
        this.library = new Library(path);
        setContent("MainWindow.fxml");
        menuController.initialize(this, library);
    }

    public <T> Stage showDialog(String fxFile, T controllerInstance) {
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
     * Load all fonts needed
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
            FXMLCacheHolder loadedCache = loadFile(fxmlFile);

            //Get the parent node from the file
            Parent content = loadedCache.parent;

            //Set the FXInitializer of the controller to this object.
            BaseController controller = loadedCache.controller;
            controller.initialize(this, library);
            controller.initializeData();

            if (useTransitions) {
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

    public void setUseTransitions(boolean useTransitions) {
        this.useTransitions = useTransitions;
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
     * @return The content contained in the file. The same as the result of {@link FXMLLoader#load()}.
     * @throws IOException If an error occurs when opening the file.
     */
    private FXMLCacheHolder loadFile(String fileName) throws IOException {
        //Load only if it has not been previously loaded
        FXMLLoader loader = new FXMLLoader(FXInitializer.class.getResource(fileName));

        //Save the loaded content into the caches
        Parent loadedParent = loader.load();
        return new FXMLCacheHolder(loader.getController(), loadedParent);
    }

    /**
     * Opens the help window containing documentation.
     */
    public void loadHelp() {
        //Open the window if it isn't open
        helpStage.show();
        //Un-minimize the window
        helpStage.setIconified(false);
        //Maximize window
        helpStage.setMaximized(true);
        //Bring the window to the front of all other windows
        helpStage.toFront();
    }

    private static class FXMLCacheHolder {
        BaseController controller;
        Parent parent;

        FXMLCacheHolder(BaseController controller, Parent parent) {
            this.controller = controller;
            this.parent = parent;
        }
    }
}
