package library.data;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.*;

public class ReportGenerator {
    private static final double FINE_RATE = 1.5;
    private static final double FINE_LIMIT = 60;
    private static final String BOOK_HEADER_FORMAT = "%-8s%-30.30s%-30.30s%-12s%-8s%n";
    private static final String BOOK_CONTENT_FORMAT = "%-8s%-30.30s%-30.30s%-12s%-8s%n";
    private static final String SEPARATOR = "---------------------------------------------------------------------------------------------\n";

    private final Library library;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault());

    public ReportGenerator(Library library) {
        this.library = library;
    }

    public String formatByPatron(List<Book> books) {
        if (books.isEmpty()) {
            return "No Books.";
        }

        //Use stringbuilder for efficiency; reduces creation of strings
        StringBuilder report = new StringBuilder();
        Formatter formatter = new Formatter(report);

        Map<Patron, List<Book>> patronBookMap = new HashMap<>();
        for (Book e : books) {
            Patron patron = e.getCurrentPatron();

            patronBookMap.computeIfAbsent(patron, (s) -> new ArrayList<>());
            patronBookMap.get(patron).add(e);
        }

        for (Map.Entry<Patron, List<Book>> e : patronBookMap.entrySet()) {
            List<Book> booksOwned = e.getValue();
            Patron patron = e.getKey();
            report.append(patron.getLastName()).append(", ").append(patron.getFirstName()).append(" - ").append(patron.getIdentifier()).append('\n');
            formatter.format(BOOK_HEADER_FORMAT, "Item ID", "Title", "Author", "Due Date", "Days Till Due");
            report.append(SEPARATOR);

            PatronType patronType = patron.getPatronType();

            for (Book book : booksOwned) {
                TemporalAmount maxCheckoutTime = Duration.ofDays(patronType.getMaxCheckoutDays());

                int daysTillDue = getDayTillDue(book);

                Instant dueDate = book.getCheckOutDate().plus(maxCheckoutTime);
                formatter.format(BOOK_CONTENT_FORMAT, book.getIdentifier().getId(), book.getTitle(), book.getAuthor(),
                        dateTimeFormatter.format(dueDate), getDaysTillDue(daysTillDue));
            }
            formatter.format("\n\n");
        }

        return formatter.out().toString();
    }

    private int getDayTillDue(Book e) {
        Patron patron = e.getCurrentPatron();
        PatronType patronType = patron.getPatronType();
        TemporalAmount maxCheckoutTime = Duration.ofDays(patronType.getMaxCheckoutDays());

        Instant dueDate = e.getCheckOutDate().plus(maxCheckoutTime);
        return (int) ChronoUnit.DAYS.between(Instant.now(), dueDate);
    }

    private String getDaysTillDue(int daysLeft) {
        if (daysLeft > 0) {
            return String.format("%-8.2s", daysLeft);
        } else {
            return "Overdue";
        }
    }

    public String formatByItems(List<Book> books) {
        if (books.isEmpty()) {
            return "No Books.";
        }

        //Use stringbuilder for efficiency; reduces creation of strings
        StringBuilder report = new StringBuilder();
        Formatter formatter = new Formatter(report);

        formatter.format(BOOK_HEADER_FORMAT, "Item ID", "Title", "Author", "Due Date", "Days Till Due");
        report.append(SEPARATOR);

        for (Book e : books) {
            Patron patron = e.getCurrentPatron();

            PatronType patronType = patron.getPatronType();
            TemporalAmount maxCheckoutTime = Duration.ofDays(patronType.getMaxCheckoutDays());

            int daysLeft = getDayTillDue(e);

            Instant dueDate = e.getCheckOutDate().plus(maxCheckoutTime);
            formatter.format(BOOK_CONTENT_FORMAT, e.getIdentifier().getId(), e.getTitle(), e.getAuthor(),
                    dateTimeFormatter.format(dueDate), getDaysTillDue(daysLeft));

        }
        return formatter.out().toString();
    }

    public String getFines() {
        List<Book> books = getOverdueBooks();
        if (books.isEmpty()) {
            return "No fines.";
        }

        //Use stringbuilder for efficiency; reduces creation of strings
        StringBuilder report = new StringBuilder();
        Formatter formatter = new Formatter(report);

        Map<Patron, List<Book>> patronBookMap = new HashMap<>();

        for (Book e : books) {
            Patron patron = e.getCurrentPatron();

            patronBookMap.computeIfAbsent(patron, (s) -> new ArrayList<>());
            patronBookMap.get(patron).add(e);
        }

        for (Map.Entry<Patron, List<Book>> bookValue : patronBookMap.entrySet()) {
            List<Book> booksOwned = bookValue.getValue();
            Patron patron = bookValue.getKey();
            report.append(patron.getLastName()).append(", ").append(patron.getFirstName()).append(" - ").append(patron.getIdentifier()).append('\n');
            formatter.format(BOOK_HEADER_FORMAT, "Item ID", "Title", "Author", "Due Date", "Fine");
            report.append(SEPARATOR);

            PatronType patronType = patron.getPatronType();

            for (Book book : booksOwned) {
                TemporalAmount maxCheckoutTime = Duration.ofDays(patronType.getMaxCheckoutDays());

                int daysLeft = -getDayTillDue(book);
                double fine = daysLeft * FINE_RATE;
                if (fine > FINE_LIMIT) {
                    fine = FINE_LIMIT;
                }

                Instant dueDate = book.getCheckOutDate().plus(maxCheckoutTime);

                formatter.format(BOOK_CONTENT_FORMAT, book.getIdentifier().getId(), book.getTitle(), book.getAuthor(),
                        dateTimeFormatter.format(dueDate), fine);
            }
            formatter.format("\n\n");
        }

        return formatter.out().toString();
    }

    public List<Book> getCheckedOutBooks() {
        List<Book> checkedOutBooks = new ArrayList<>();
        for (Book e : library.getBooks()) {
            if (bookIsCheckedOut(e)) {
                checkedOutBooks.add(e);
            }
        }
        return checkedOutBooks;
    }

    public List<Book> getOverdueBooks() {
        List<Book> overDueBooks = new ArrayList<>();
        for (Book e : getCheckedOutBooks()) {
            Patron patron = e.getCurrentPatron();
            PatronType patronType = patron.getPatronType();
            TemporalAmount maxCheckoutTime = Duration.ofDays(patronType.getMaxCheckoutDays());

            Instant dueDate = e.getCheckOutDate().plus(maxCheckoutTime);
            //Book is overdue; the due date is before now
            if (dueDate.isBefore(Instant.now())) {
                overDueBooks.add(e);
            }
        }
        return overDueBooks;
    }

    private boolean bookIsCheckedOut(Book e) {
        return e.getStatus() == BookStatus.CHECKED_OUT && e.getCurrentPatron() != null &&
                !e.getCurrentPatron().getIdentifier().getId().equals("null")
                && e.getCheckOutDate() != null;
    }
}
