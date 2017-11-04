package library.fx;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class Patrons {
    @FXML
    public TableView patronTable;
    @FXML
    public TextField lastName;
    @FXML
    public TextField identifier;
    @FXML
    public TextField firstName;
    @FXML
    public DatePicker birthDate;
}
