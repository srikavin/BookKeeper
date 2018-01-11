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
            formatter.format("%-8s%-20s\n", patron.getIdentifier(), patron.getLastName() + ", " + patron.getFirstName());
            formatter.format("%-8s%-30.30s%-30.30s%-25s\n", "Item ID", "Title", "Author", "Due Date");
            formatter.format("-----------------------------------------------------------------------------\n");

            PatronType patronType = patron.getPatronType();

            for (Book book : booksOwned) {
                TemporalAmount maxCheckoutTime = Duration.ofDays(patronType.getMaxCheckoutDays());

                Instant dueDate = book.getCheckOutDate().plus(maxCheckoutTime);
                formatter.format("%-8s%-30.30s%-30.30s%-25s\n", book.getIdentifier().getId(), book.getName(), book.getAuthor(),
                        dateTimeFormatter.format(dueDate));
            }
            formatter.format("\n");
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

    public String formatByItems(List<Book> books) {
        if (books.isEmpty()) {
            return "No Books.";
        }

        //Use stringbuilder for efficiency; reduces creation of strings
        StringBuilder report = new StringBuilder();
        Formatter formatter = new Formatter(report);

        formatter.format("%-8s%-30.30s%-30.30s%-25s%-8s\n", "Item ID", "Title", "Author", "Due Date", "Days Till Due");

        for (Book e : books) {
            Patron patron = e.getCurrentPatron();

            PatronType patronType = patron.getPatronType();
            TemporalAmount maxCheckoutTime = Duration.ofDays(patronType.getMaxCheckoutDays());

            int daysLeft = getDayTillDue(e);

            Instant dueDate = e.getCheckOutDate().plus(maxCheckoutTime);
            if (daysLeft > 0) {
                formatter.format("%-8s%-30.30s%-30.30s%-25s%-8d\n", e.getIdentifier().getId(), e.getName(), e.getAuthor(),
                        dateTimeFormatter.format(dueDate), daysLeft);
            } else {
                formatter.format("%-8s%-30.30s%-30.30s%-25s%-8s\n", e.getIdentifier().getId(), e.getName(), e.getAuthor(),
                        dateTimeFormatter.format(dueDate), "Overdue");
            }
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
