package library.fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.*;
import javafx.scene.chart.PieChart;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import library.data.Book;
import library.data.BookStatus;
import library.data.Library;
import library.data.ReportGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Reports extends BaseController {
    @FXML
    private TextArea reportView;
    @FXML
    private RadioButton checkedOutItems;
    @FXML
    private RadioButton overDueItems;
    @FXML
    private RadioButton itemSort;
    @FXML
    private RadioButton patronSort;
    @FXML
    private PieChart bookStatusChart;
    private boolean sortByItem = false;
    private Views currentView = Views.CHECKED_OUT;

    private void setReportContent() {
        Library library = getLibrary();
        ReportGenerator reportGenerator = library.getReportGenerator();

        List<Book> bookList = new ArrayList<>();
        switch (currentView) {
            case CHECKED_OUT:
                bookList = reportGenerator.getCheckedOutBooks();
                break;
            case OVERDUE:
                bookList = reportGenerator.getOverdueBooks();
                break;
            case FINES:
                reportView.setText(reportGenerator.getFines());
                sortByItem = false;
                itemSort.setSelected(false);
                itemSort.setDisable(true);
                patronSort.setSelected(true);
                return;
        }
        itemSort.setDisable(false);

        if (sortByItem) {
            reportView.setText(reportGenerator.formatByItems(bookList));
        } else {
            reportView.setText(reportGenerator.formatByPatron(bookList));
        }

        Map<BookStatus, Integer> bookStatusTotals = reportGenerator.getBookStatusTotals();
        ObservableList<PieChart.Data> bookStatusData = FXCollections.observableArrayList();

        //For each entry in the map, create a new data entry and add it to the list
        bookStatusTotals.forEach((bookStatus, amount) -> bookStatusData.add(new PieChart.Data(bookStatus.toString(), amount)));
        //Set the data into the PieChart
        bookStatusChart.setData(bookStatusData);
    }

    @FXML
    private void viewOverdueItems(ActionEvent event) {
        currentView = Views.OVERDUE;
        setReportContent();
    }

    @FXML
    private void sortByItem(ActionEvent event) {
        sortByItem = true;
        setReportContent();
    }

    @FXML
    private void sortByPatron(ActionEvent event) {
        sortByItem = false;
        setReportContent();
    }

    @FXML
    private void checkoutView(ActionEvent event) {
        currentView = Views.CHECKED_OUT;
        setReportContent();
    }

    @FXML
    private void print(ActionEvent event) {
        Text reportText = new Text(reportView.getText());
        reportText.setFont(Font.font("monospaced"));
        TextFlow printArea = new TextFlow(reportText);

        Printer printer = Printer.getDefaultPrinter();
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);

        double maxRatio = (pageLayout.getPrintableWidth() / pageLayout.getPrintableHeight());
        double scaleX = maxRatio * printArea.getBoundsInParent().getWidth();
        double scaleY = maxRatio * printArea.getBoundsInParent().getHeight();
        Scale scale = new Scale(scaleX, scaleY);
        printArea.getTransforms().add(scale);

        int pages = (int) (Screen.getPrimary().getDpi() * (printArea.getHeight() / pageLayout.getPrintableHeight()) + 1);

        //Make sure user wants to print
        if (printerJob != null && printerJob.showPrintDialog(getInitializer().getPrimaryStage())) {
            //Set page layout and other settings
            printArea.setMaxWidth(pageLayout.getPrintableWidth());
            //Print all pages in the node
            for (int page = 0; page <= pages; page++) {
                printerJob.printPage(pageLayout, printArea);
                printArea.setTranslateX(-1 * page * pageLayout.getPrintableHeight());
            }
            printerJob.endJob();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeData() {
        super.initializeData();
        setReportContent();
    }

    @FXML
    private void viewFines(ActionEvent event) {
        currentView = Views.FINES;
        setReportContent();
    }

    /**
     * Used to maintain internal state about the current checked item
     */
    private enum Views {
        CHECKED_OUT,
        OVERDUE,
        FINES
    }
}
