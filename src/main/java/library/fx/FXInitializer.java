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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FXInitializer extends Application {
    private Stage helpStage;
    private BorderPane borderPane = new BorderPane();
    private BaseController currentController;
    private Map<String, FXMLCacheHolder> fxmlCache = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Load all fonts before initializing the program
        loadFonts();

        //Load the menu items separately from the main content
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("Menu.fxml"));
        MenuBar menuBar = menuLoader.load();

        //Load the default content
        FXMLCacheHolder cacheHolder = loadFile("MainWindow.fxml");
        Parent parent = cacheHolder.parent;

        //Make sure to load the fxml file before requesting the controller
        MainWindow controller = (MainWindow) cacheHolder.controller;
        this.currentController = controller;

        //Set FXInitializer to this object
        controller.setFXInitializer(this);

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
     * {@link BaseController#setFXInitializer(FXInitializer)}
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
            controller.setFXInitializer(this);

            //Set a callback after the animation has finished
            this.currentController.animateOut((e) ->
                    controller.animateIn((event) -> {
                        //Set the center of the pane to the content loaded
                        borderPane.setCenter(content);
                        //Set the current controller to the new content's controller
                        this.currentController = controller;
                        }));
        } catch (IOException e) {
            //Display an error message if an IOException occurs
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
//        if (fxmlCache.containsKey(fileName)) {
//            return fxmlCache.get(fileName);
//        }
        //Load only if it has not been previously loaded
        FXMLLoader loader = new FXMLLoader(FXInitializer.class.getResource(fileName));

        //Save the loaded content into the cache
        Parent loadedParent = loader.load();
        FXMLCacheHolder cache = new FXMLCacheHolder(loader.getController(), loadedParent);
        fxmlCache.put(fileName, cache);

        return cache;
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

    private static class FXMLCacheHolder {
        BaseController controller;
        Parent parent;

        FXMLCacheHolder(BaseController controller, Parent parent) {
            this.controller = controller;
            this.parent = parent;
        }
    }
}
