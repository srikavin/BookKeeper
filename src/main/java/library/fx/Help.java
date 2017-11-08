package library.fx;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Help extends BaseController implements Initializable {
    @FXML
    public Label help;
    @FXML
    private ListView<String> helpList;
    @FXML
    private WebView helpWebView;

    private Map<String, String> categoryFileMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Help.class.getResourceAsStream("help/help.txt")))) {

            ObservableList<String> list = helpList.getItems();


            reader.lines()
                    // Filter out comments specified in the file
                    .filter((e) -> !e.startsWith("#"))
                    .forEach((e) -> {

                        //Split the string on spaces to separate the name and the html filename
                        String[] split = e.split(" ");

                        //Load the data into the category-file map
                        categoryFileMap.put(split[0], split[1]);

                        //Add the category name to the list
                        list.add(split[0]);

                    });
        } catch (IOException e) {
            //Display an error message after catching an exception
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error loading help files!");
            alert.setContentText(e.getLocalizedMessage());

            alert.showAndWait();
            e.printStackTrace();
        }

        //On selecting the list, load the specified html page
        helpList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            WebEngine engine = helpWebView.getEngine();
            engine.load(Help.class.getResource("help/" + categoryFileMap.get(newValue)).toExternalForm());
        });
    }
}
