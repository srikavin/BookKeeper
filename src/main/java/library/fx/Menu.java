package library.fx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

public class Menu extends BaseController implements Initializable {
    public CheckMenuItem useAnimations;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Update use of animations when the option is toggled
        useAnimations.setSelected(true);
        useAnimations.selectedProperty().addListener((observable, oldValue, newValue) ->
                getInitializer().setUseTransitions(newValue));
    }

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
    void open(ActionEvent event) {
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
    private void newLibrary(ActionEvent event) {
//        getInitializer().loadFile();
    }

    @FXML
    private void saveAs(ActionEvent event) {

    }

    @FXML
    private void loadSampleData(ActionEvent event) {
        try {
            Path temp = Files.createTempDirectory("sampleData");
            Files.copy(getClass().getResourceAsStream("data.txt"), temp.resolve("data.txt"));
            getInitializer().loadFile(temp);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Request");
            alert.setHeaderText("There was an error in loading sample data!");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
        }
    }
}
