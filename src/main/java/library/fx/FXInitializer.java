package library.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FXInitializer extends Application {
    private BorderPane borderPane = new BorderPane();

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("Menu.fxml"));
        MenuBar menuBar = menuLoader.load();

        FXMLLoader contentLoader = new FXMLLoader(getClass().getResource("Patrons.fxml"));
        Parent parent = contentLoader.load();

        borderPane.setTop(menuBar);
        borderPane.setCenter(parent);
        primaryStage.setScene(new Scene(borderPane));
        primaryStage.show();
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }
}
