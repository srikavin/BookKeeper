package library.fx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import library.data.Library;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class Menu extends BaseController {
    @FXML
    void about(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About BookKeeper");
        alert.setHeaderText("About");
        alert.setContentText("Made by Srikavin Ramkumar for FBLA's Coding and Programming Competition.");
        alert.showAndWait();
    }

    @FXML
    void documentation(ActionEvent event) {
        getInitializer().loadHelp();
    }

    @FXML
    void newAction(ActionEvent event) {

    }

    @FXML
    void open(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open library data file");
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Path path = directoryChooser.showDialog(stage).toPath();
        unsavedChanges(() -> {
            try {
                getInitializer().loadFile(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load library data file!");
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
    void quit(ActionEvent event) {
        unsavedChanges(Platform::exit);
    }

    @FXML
    void save(ActionEvent event) {
        try {
            Library library = getLibrary();
            library.save();
        } catch (IOException e) {
            //Display an error message if an exception occurs when saving
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Request");
            alert.setHeaderText("There was an error in saving!");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void settings(ActionEvent event) {

    }

    @FXML
    private void newLibrary(ActionEvent event) {

    }

    @FXML
    private void saveAs(ActionEvent event) {

    }

    @FXML
    private void loadSampleData(ActionEvent event) {

    }
}
