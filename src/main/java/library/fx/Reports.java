package library.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import library.data.Book;
import library.data.Library;
import library.data.ReportGenerator;

import java.util.ArrayList;
import java.util.List;

public class Reports extends BaseController {
    public TextArea reportView;
    public RadioButton checkedOutItems;
    public RadioButton overDueItems;
    public RadioButton itemSort;
    public RadioButton patronSort;
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
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        //Make sure user wants to print
        if (printerJob != null && printerJob.showPrintDialog(null)) {
            //Set page layout and other settings
            PageLayout pageLayout = printerJob.getJobSettings().getPageLayout();
            printArea.setMaxWidth(pageLayout.getPrintableWidth());
            //Print page
            if (printerJob.printPage(printArea)) {
                //Finish printing
                printerJob.endJob();
            }
        }
    }

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

    enum Views {
        CHECKED_OUT,
        OVERDUE,
        FINES
    }
}
