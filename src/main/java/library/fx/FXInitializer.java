package library.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;
import library.data.Library;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The starting point for the JavaFX GUI. Initializes the JavaFX system and starts the program.
 *
 * @author Srikavin Ramkumar
 */
public class FXInitializer extends Application {
    private Stage helpStage;
    private BorderPane borderPane = new BorderPane();
    private BaseController currentController;
    private Library library;

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        library = new Library(Paths.get("testing", "data"));

        //Load all fonts before initializing the program
        loadFonts();

        //Load the menu items separately from the main content
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("Menu.fxml"));
        MenuBar menuBar = menuLoader.load();
        BaseController menuController = menuLoader.getController();
        menuController.initialize(this, library);

        //Load the default content
        FXMLCacheHolder cacheHolder = loadFile("MainWindow.fxml");
        Parent parent = cacheHolder.parent;

        //Make sure to load the fxml file before requesting the controller
        MainWindow controller = (MainWindow) cacheHolder.controller;
        this.currentController = controller;

        //Set FXInitializer to this object
        controller.initialize(this, library);

        //Set the top of the pane to the menu bar
        borderPane.setTop(menuBar);
        //Set the center of the pane to the content
        borderPane.setCenter(parent);
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

        loadHelp();
    }

    public void loadFile(Path path) throws IOException {
        this.library = new Library(path);
        setContent("MainWindow.fxml");
    }

    public Pair<Object, Stage> showDialog(String fxFile) {
        try {
            Stage stage = new Stage();
            FXMLCacheHolder cacheHolder = loadFile(fxFile);
            Object controller = cacheHolder.controller;
            Parent root = cacheHolder.parent;
            stage.setScene(new Scene(root));
            return new Pair<>(controller, stage);
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
            FXMLCacheHolder<BaseController> loadedCache = loadFile(fxmlFile);

            //Get the parent node from the file
            Parent content = loadedCache.parent;

            //Set the FXInitializer of the controller to this object.
            BaseController controller = loadedCache.controller;
            controller.initialize(this, library);
            controller.initializeData();

            //Set a callback after the animation has finished
            this.currentController.animateOut((e) ->
                    controller.animateIn((event) -> {
                        //Set the center of the pane to the content loaded
                        borderPane.setCenter(content);
                        //Set the current controller to the new content's controller
                        this.currentController = controller;
                    }));
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
        //Bring the window to the front of all other windows
        helpStage.toFront();
    }

    private static class FXMLCacheHolder<T> {
        T controller;
        Parent parent;

        FXMLCacheHolder(T controller, Parent parent) {
            this.controller = controller;
            this.parent = parent;
        }
    }
}
