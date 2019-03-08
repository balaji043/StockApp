package sample.ui.need;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import sample.Alert.AlertMaker;
import sample.Database.DatabaseHelper;
import sample.Main;
import sample.Utils.Preferences;
import sample.custom.ToolTip.TooltippedTableCell;
import sample.model.Product;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/*
Export Stocks
*/
public class NeedController implements Initializable {

    @FXML
    JFXComboBox<String> tableCB;
    @FXML
    TableView<Product> tableView;
    @FXML
    BorderPane borderPane;

    private Preferences preferences = Preferences.getPreferences();
    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    private void init() {
        try {
            tableCB.getItems().add("All");
            tableCB.getItems().addAll(preferences.getTableNames());
            tableCB.getSelectionModel().selectFirst();
            initTable();
            tableCB.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (!tableCB.getValue().isEmpty()) {
                    loadTable();
                }
            });
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDownload() {
        boolean okay;
        if (tableView.getItems().size() == 0) {
            mainApp.snackBar("INFO", "Nothing to Download", "green");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new
                FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        File dest = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        if (dest == null) {
            mainApp.snackBar("INFO", "Operation Cancelled", "green");
        } else {
            if (!dest.getName().endsWith(".xlsx"))
                dest = new File(dest.toString() + ".xlsx");

            mainApp.addSpinner();

            if (tableCB.getValue().equals("All")) {
                okay = DatabaseHelper.needProductSQLToExcel(dest, preferences.getTableNames());
            } else {
                Set<String> s = new HashSet<>();
                s.add(tableCB.getValue());
                okay = DatabaseHelper.needProductSQLToExcel(dest, s);
            }
            mainApp.removeSpinner();
            if (okay)
                mainApp.snackBar("Success"
                        , "All the Needed stock Data are written to excel"
                        , "green");
            else
                mainApp.snackBar("Failed"
                        , "All the Needed stock Data is not written to excel"
                        , "red");
        }

    }

    private void initTable() {

        tableView.getColumns().clear();
        tableView.getItems().clear();
        addTableColumn("Brand", "brand", 125);
        addTableColumn("Category", "Category", 125);
        addTableColumn("Sub Category", "subCategory", 200);
        addTableColumn("Name", "name", 200);
        addTableColumn("PART.NO", "partNo", 175);
        addTableColumn("C-QTY", "QTY", 75);
        addTableColumn("Min Qty", "min", 75);
        addTableColumn("Required", "required", 75);


        tableView.setTableMenuButtonVisible(true);
        loadTable();

    }

    private void loadTable() {
        tableView.getItems().clear();
        if (tableCB.getValue().equals("All")) {
            Set<String> table = Preferences.getPreferences().getTableNames();
            for (String s : table) {
                tableView.getItems().addAll(DatabaseHelper.getNeededProductList(s));
            }
        } else {
            tableView.getItems().addAll(DatabaseHelper
                    .getNeededProductList(tableCB.getValue()));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
    }

    private void addTableColumn(String name, String msg, int width) {
        TableColumn<Product, String> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(msg));
        column.setCellFactory(TooltippedTableCell.forTableColumn());
        column.setPrefWidth(width);

        tableView.getColumns().add(column);
    }

}
