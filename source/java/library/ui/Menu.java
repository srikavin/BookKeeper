package library.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import library.data.Library;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Implements the controller for the FileMenu. This controller is responsible for delegating calls to the
 * {@link FXInitializer} and the {@link Library} object. Handles all of the operations within the file menu, including
 * File, Preferences, and Help
 */
public class Menu extends BaseController {
    public CheckMenuItem useAnimations;
    private boolean isTempData = true;

    /**
     *  Initializes the menu with the specified stage. This stage is used to detect close requests.
     *
     * @param stage The main window stage
     */
    public void initialize(Stage stage) {
        //Update use of animations when the option is toggled
        useAnimations.setSelected(true);
        useAnimations.selectedProperty().addListener((observable, oldValue, newValue) ->
                getInitializer().setUseTransitions(newValue));
        //When the user tries to close the window, make sure they intended to not save any changes
        stage.setOnCloseRequest(this::quit);
    }

    @FXML
    private void about(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About BookKeeper");
        alert.setHeaderText("About");
        alert.setContentText("Made by Srikavin Ramkumar for FBLA's Coding and Programming Competition.");
        alert.showAndWait();
    }

    @FXML
    private void documentation(ActionEvent event) {
        getInitializer().loadHelp();
    }

    @FXML
    private void open(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(Paths.get("").toAbsolutePath().toFile());
        directoryChooser.setTitle("Open library data file");

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        File file = directoryChooser.showDialog(stage);

        if (file == null) {
            return;
        }
        Path path = file.toPath();
        unsavedChanges(() -> {
            try {
                isTempData = false;
                getInitializer().loadFile(path);
            } catch (IOException e) {
                showError("opening the data file", e);
            }
        });
    }

    private void unsavedChanges(Runnable runnable) {
        Library library = getLibrary();
        if (library.isModified()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Unsaved changes");
            alert.setHeaderText("Unsaved changes exist");

            ButtonType quit = new ButtonType("Quit", ButtonBar.ButtonData.LEFT);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(quit, cancel);

            //Get the result of the alert dialog
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == quit) {
                //Exit if the user chooses quit
                runnable.run();
            }
        } else {
            runnable.run();
        }
    }

    @FXML
    private void quit(Event event) {
        unsavedChanges(Platform::exit);
    }

    @FXML
    private void newLibrary(ActionEvent event) {
        unsavedChanges(() -> {
            try {
                isTempData = true;
                getInitializer().loadFile(null);
            } catch (IOException e) {
                //Display an error message if an exception occurs when saving
                showError("creating a new library", e);
            }
        });
    }

    private void showError(String error, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Request");
        alert.setHeaderText("There was an error in " + error + "!");
        alert.setContentText(e.getLocalizedMessage());
        alert.showAndWait();
    }

    @FXML
    private void saveAs(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(Paths.get("").toAbsolutePath().toFile());
        directoryChooser.setTitle("Save As...");

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        File file = directoryChooser.showDialog(stage);

        if (file == null) {
            return;
        }
        Path path = file.toPath();

        Library library = getLibrary();
        try {
            library.saveTo(path);
        } catch (IOException e) {
            showError("saving the library", e);
        }
        isTempData = false;
    }

    @FXML
    private void save(ActionEvent event) {
        try {
            Library library = getLibrary();
            if (!isTempData) {
                library.save();
            } else {
                //Show the save as dialog if the user tries to save the sample data
                saveAs(event);
            }
        } catch (IOException e) {
            //Display an error message if an exception occurs when saving
            showError("saving the library", e);
        }
    }

    @FXML
    private void loadSampleData(ActionEvent event) {
        try {
            isTempData = true;
            Path temp = Files.createTempDirectory("sampleData");
            Files.copy(getClass().getResourceAsStream("data.txt"), temp.resolve("data.txt"));
            getInitializer().loadFile(temp);
        } catch (IOException e) {
            showError("loading sample data", e);
        }
    }
}