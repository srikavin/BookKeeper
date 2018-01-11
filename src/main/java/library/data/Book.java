package library.data;

import java.time.Instant;

/**
 * Represents a single library book.
 *
 * @author Srikavin Ramkumar
 */
public class Book implements LibraryData {
    private Identifier identifier;
    private String isbn;
    private String name;
    private String author;
    private BookStatus status;
    private Patron currentPatron;
    private Instant checkOutDate;

    /**
     * Creates an instance of this class.
     *
     * @param identifier    Identifier representing this object
     * @param name          The title of the book
     * @param author        The author's name
     * @param isbn          The ISBN of this book
     * @param status        The current status of this book
     * @param checkOutDate  The checked out date of this book, can be null if it is not currently checked out
     * @param currentPatron The current patron holding this book
     */
    public Book(Identifier identifier, String name, String author, String isbn, BookStatus status, Patron currentPatron, Instant checkOutDate) {
        this.identifier = identifier;
        this.isbn = isbn;
        this.status = status;
        this.name = name;
        this.author = author;
        this.currentPatron = currentPatron;
        this.checkOutDate = checkOutDate;
    }

    /**
     * Initialize this object using saved data from {@link #asData()}.
     *
     * @param data A string array in the same format as returned by {@link #asData()}
     */
    public Book(String[] data, Library library) {
        this(new Identifier(data[0]), data[1], data[2], data[3], BookStatus.valueOf(data[4]),
                data[5].equals("null") || data[5].isEmpty() ? null : library.getPatronFromID(new Identifier(data[5])),
                data[6].equals("null") || data[6].isEmpty() ? null : Instant.parse(data[6]));
        if (data.length != 7) {
            throw new RuntimeException("Invalid data type!");
        }
    }

    public Patron getCurrentPatron() {
        return currentPatron;
    }

    public void setCurrentPatron(Patron currentPatron) {
        this.currentPatron = currentPatron;
    }

    public Instant getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Instant checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String[] asData() {
        return new String[]{
                identifier.getId(),
                name,
                author,
                isbn,
                status.name(),
                currentPatron != null ? currentPatron.getIdentifier().getId() : "null",
                checkOutDate != null ? checkOutDate.toString() : "null"
        };
    }

    public String getIsbn() {
        return isbn;
    }

    /**
     * Update the ISBN of this book.
     *
     * @param isbn A valid 10 or 13 digit ISBN.
     */
    public void setIsbn(String isbn) {
        int isbnChars = 0;
        for (char e : isbn.toCharArray()) {
            if (Character.isDigit(e) || Character.isAlphabetic(e)) {
                isbnChars++;
            }
        }
        if (isbnChars != 10 && isbnChars != 13) {
            throw new RuntimeException("Invalid ISBN!");
        }
        this.isbn = isbn;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
