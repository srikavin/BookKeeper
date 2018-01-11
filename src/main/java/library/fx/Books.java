package library.fx;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;
import library.data.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This class serves as a controller of the Books view defined in the FXML.
 * It handles updating and setting the content relating to the Patron class in the view as well as in the data layer.
 *
 * @author Srikavin Ramkumar
 */
public class Books extends DataViewController<Book> implements Initializable {
    @FXML
    private ChoiceBox<BookStatus> status;
    @FXML
    private TextField author;
    @FXML
    private TextField bookName;
    @FXML
    private TextField isbn;
    @FXML
    private TextField identifier;
    @FXML
    private TextField filter;
    @FXML
    private TextField currentPatron;


    @FXML
    protected void update(Book book) {
        Library library = getLibrary();

        book.setAuthor(author.getText());
        book.setName(bookName.getText());
        book.setIdentifier(new Identifier(identifier.getText()));
        book.setIsbn(isbn.getText());
        book.setStatus(status.getValue());
        book.setCurrentPatron(library.getPatronFromID(new Identifier(currentPatron.getText())));
    }

    @Override
    protected Predicate<Book> getFilterPredicate(String filter) {
        return book -> book.getIdentifier().getId().toLowerCase().contains(filter)
                || book.getIdentifier().getId().contains(filter)
                || book.getIsbn().toLowerCase().contains(filter)
                || book.getAuthor().toLowerCase().contains(filter);
    }

    @Override
    protected List<Book> getDataSource() {
        Library library = getLibrary();
        return library.getBooks();
    }

    @Override
    protected boolean validate() {
        //Set all fields to the valid state to reset error states
        author.pseudoClassStateChanged(errorClass, false);
        bookName.pseudoClassStateChanged(errorClass, false);
        isbn.pseudoClassStateChanged(errorClass, false);
        identifier.pseudoClassStateChanged(errorClass, false);
        status.pseudoClassStateChanged(errorClass, false);
        currentPatron.pseudoClassStateChanged(errorClass, false);

        //Get values
        String authorValue = author.getText();
        String bookNameValue = bookName.getText();
        String isbnValue = isbn.getText();
        String identifierValue = identifier.getText();
        String currentPatronValue = currentPatron.getText();

        //Keep track of the values
        Set<Node> errors = new HashSet<>();

        if (authorValue.isEmpty()) {
            errors.add(author);
        }
        if (bookNameValue.isEmpty()) {
            errors.add(bookName);
        }
        if (!currentPatronValue.isEmpty()) {
            //Verify current patron is a valid patron ID
            Library library = getLibrary();
            if (library.getPatronFromID(new Identifier(currentPatronValue)) == null) {
                errors.add(currentPatron);
            }
        }
        //Verify the ISBN to have 10 or 13 digits (excluding non-digit characters)
        int numbers = 0;
        for (char e : isbnValue.toCharArray()) {
            if (Character.isDigit(e) || Character.isAlphabetic(e)) {
                numbers++;
            }
        }
        if (numbers != 10 && numbers != 13) {
            errors.add(isbn);
        }
        if (identifierValue.isEmpty()) {
            errors.add(identifier);
        }
        //Make sure the currently selected book status is not empty
        if (status.getSelectionModel().isEmpty()) {
            errors.add(status);
        }

        //Iterate through all errors and add a red outline to them
        for (Node e : errors) {
            e.pseudoClassStateChanged(errorClass, true);
        }

        //Return true if errors is empty and return false if errors have occurred
        return errors.isEmpty();
    }

    @Override
    protected void setupColumns(TableView<Book> table) {
        //Add columns
        ObservableList<TableColumn<Book, ?>> columns = table.getColumns();

        TableColumn<Book, String> idColumn = new TableColumn<>("ID");
        TableColumn<Book, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        TableColumn<Book, BookStatus> statusColumn = new TableColumn<>("Status");

        idColumn.setCellValueFactory((value) ->
                new ReadOnlyObjectWrapper<>(value.getValue().getIdentifier().getId()));
        nameColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getName()));
        isbnColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getIsbn()));
        authorColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getAuthor()));
        statusColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getStatus()));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        columns.addAll(idColumn, nameColumn, isbnColumn, authorColumn, statusColumn);
    }

    @Override
    protected void setCurrentState(Book book) {
        //Set the text field values to the current state
        identifier.setText(book.getIdentifier().getId());
        author.setText(book.getAuthor());
        isbn.setText(book.getIsbn());
        bookName.setText(book.getName());
        status.getSelectionModel().select(book.getStatus());
        //Make sure the current patron value does not cause a null pointer exception
        Patron currentPatronValue = book.getCurrentPatron();
        if (currentPatronValue == null) {
            currentPatron.setText("");
        } else {
            currentPatron.setText(currentPatronValue.getIdentifier().getId());
        }
    }

    @Override
    protected Book createNewItem(Identifier identifier) {
        return new Book(identifier, "", "", "", BookStatus.AVAILABLE, null, Instant.now());
    }

    @FXML
    private void findPatron(MouseEvent mouseEvent) {
        Library library = getLibrary();
        Pair<Object, Stage> dialogInfo = getInitializer().showDialog("PatronSelect.fxml");
        Stage stage = dialogInfo.getValue();
        PatronSelect patronSelect = (PatronSelect) dialogInfo.getKey();
        patronSelect.init((e) -> currentPatron.setText(e.getId()), library.getPatrons(), stage);
        stage.showAndWait();
        stage.toFront();
        stage.setTitle("Select a Patron");
    }

    @Override
    public void initializeData() {
        super.initializeData();
        status.getItems().setAll(BookStatus.values());
    }
}
