package library.fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import library.data.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Checkout extends DataViewController<Book> {
    public TextField patronName;
    public Text booksCheckedOut;
    public ChoiceBox<BookStatus> bookStatus;
    public TextField bookAuthor;
    public TextField bookTitle;
    public TextField currentBook;
    public TextField currentPatron;
    private Books books = new Books();

    @Override
    protected Predicate<Book> getFilterPredicate(String filter) {
        return book -> book.getIdentifier().getId().toLowerCase().contains(filter)
                || book.getName().toLowerCase().contains(filter)
                || book.getIsbn().toLowerCase().contains(filter)
                || book.getAuthor().toLowerCase().contains(filter);
    }

    @Override
    protected List<Book> getDataSource() {
        return FXCollections.observableList(new ArrayList<>());
    }

    @Override
    protected boolean validate() {
        return false;
    }

    @Override
    protected void setupColumns(TableView<Book> table) {
        books.setupColumns(table);
    }

    @Override
    protected void setCurrentState(Book current) {
        updateBook(current.getIdentifier());
    }

    @Override
    protected Book createNewItem(Identifier identifier) {
        return books.createNewItem(identifier);
    }

    @Override
    protected void update(Book toUpdate) {

    }

    @FXML
    private void findPatron(MouseEvent event) {
        Select.PatronSelect select = new Select.PatronSelect();
        Library library = getLibrary();
        Stage stage = getInitializer().getDialog("Select.fxml", select);
        select.init(this::updatePatron, library.getPatrons(), stage);
        stage.showAndWait();
    }

    private void updatePatron(Identifier id) {
        currentPatron.pseudoClassStateChanged(errorClass, false);
        Library library = getLibrary();
        Patron p = library.getPatronFromID(id);
        currentPatron.setText(p.getIdentifier().getId());
        patronName.setText(p.getFirstName() + " " + p.getLastName());
        updateTable();
    }

    @FXML
    private void updateCurrentPatron(ActionEvent event) {
        Identifier id = new Identifier(currentPatron.getText());
        Library library = getLibrary();
        Patron patron = library.getPatronFromID(id);
        if (patron == null) {
            currentPatron.pseudoClassStateChanged(errorClass, true);
            return;
        }
        updatePatron(id);
    }

    private void updateBook(Identifier id) {
        currentBook.pseudoClassStateChanged(errorClass, false);
        Library library = getLibrary();
        Book book = library.getBookFromID(id);
        currentBook.setText(book.getIdentifier().getId());
        bookTitle.setText(book.getName());
        bookAuthor.setText(book.getAuthor());
        bookStatus.getSelectionModel().select(book.getStatus());
        bookStatus.pseudoClassStateChanged(errorClass, false);
        if (book.getStatus() == BookStatus.CHECKED_OUT) {
            bookStatus.pseudoClassStateChanged(errorClass, true);
        }
    }

    @FXML
    private void updateCurrentBook(ActionEvent event) {
        Identifier id = new Identifier(currentBook.getText());
        Library library = getLibrary();
        Book book = library.getBookFromID(id);
        if (book == null) {
            currentBook.pseudoClassStateChanged(errorClass, true);
            return;
        }

        updateBook(id);
    }

    private ObservableList<Book> getCheckedOutBooks(Identifier id) {
        Library library = getLibrary();
        List<Book> allCheckedOut = library.getReportGenerator().getCheckedOutBooks();
        List<Book> toReturn = new ArrayList<>();
        for (Book e : allCheckedOut) {
            if (e.getCurrentPatron().getIdentifier().equals(id)) {
                toReturn.add(e);
            }
        }
        return FXCollections.observableArrayList(toReturn);
    }

    @FXML
    private void findBook(MouseEvent event) {
        Select.BookSelect select = new Select.BookSelect();
        Library library = getLibrary();
        Stage stage = getInitializer().getDialog("Select.fxml", select);
        select.init(this::updateBook, library.getBooks(), stage);
        stage.showAndWait();
    }

    private void updateTable() {
        Library library = getLibrary();

        Identifier patronID = new Identifier(currentPatron.getText());
        Patron patron = library.getPatronFromID(patronID);
        ObservableList<Book> books = getCheckedOutBooks(patronID);
        setData(books);

        int currentlyCheckedOut = books.size();
        int maxCheckedOut = patron.getPatronType().getMaxCheckedOutBooks();
        booksCheckedOut.setText(currentlyCheckedOut + "/" + maxCheckedOut);
        if (currentlyCheckedOut >= maxCheckedOut) {
            booksCheckedOut.setFill(Color.RED);
        } else {
            booksCheckedOut.setFill(Color.BLACK);
        }
    }

    @FXML
    private void returnBook(ActionEvent event) {
        Book selected = getCurrentlySelected();
        if (selected != null) {
            selected.setCurrentPatron(null);
            selected.setCheckOutDate(null);
            selected.setStatus(BookStatus.AVAILABLE);
            updateTable();
        }
    }

    private boolean isOverLimit(Patron patron) {
        List<Book> books = getCheckedOutBooks(patron.getIdentifier());
        return books.size() >= patron.getPatronType().getMaxCheckedOutBooks();
    }

    @FXML
    private void checkoutBook(ActionEvent event) {
        Library library = getLibrary();
        Book book = library.getBookFromID(new Identifier(currentBook.getText()));
        Patron patron = library.getPatronFromID(new Identifier(currentPatron.getText()));
        if (patron == null) {
            currentPatron.pseudoClassStateChanged(errorClass, true);
            return;
        }
        if (book == null) {
            currentBook.pseudoClassStateChanged(errorClass, true);
            return;
        }
        if (book.getStatus() == BookStatus.CHECKED_OUT) {
            bookStatus.pseudoClassStateChanged(errorClass, true);
            Alert alert = new Alert(Alert.AlertType.WARNING, "Book already checked out!", ButtonType.CANCEL, ButtonType.YES);
            alert.setHeaderText("Override checkout?");
            Optional<ButtonType> buttonTypeOptional = alert.showAndWait();
            if (!buttonTypeOptional.isPresent()) {
                return;
            }
            if (buttonTypeOptional.get().equals(ButtonType.CANCEL)) {
                return;
            }
        }

        if (isOverLimit(patron)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Max book limit reached for patron!", ButtonType.CANCEL, ButtonType.YES);
            alert.setHeaderText("Override book limit?");
            Optional<ButtonType> buttonTypeOptional = alert.showAndWait();
            if (!buttonTypeOptional.isPresent()) {
                return;
            }
            if (buttonTypeOptional.get().equals(ButtonType.CANCEL)) {
                return;
            }
        }

        book.setCheckOutDate(Instant.now());
        book.setCurrentPatron(patron);
        book.setStatus(BookStatus.CHECKED_OUT);
        updateTable();
        setCurrentState(book);
    }

    @Override
    public void initializeData() {
        super.initializeData();
        table.setPlaceholder(new Text("No books checked out by current patron"));
        bookStatus.setItems(FXCollections.observableArrayList(BookStatus.values()));
        bookStatus.setConverter(new StringConverter<BookStatus>() {
            @Override
            public String toString(BookStatus object) {
                return object.name();
            }

            @Override
            public BookStatus fromString(String string) {
                return BookStatus.valueOf(string);
            }
        });
    }
}
