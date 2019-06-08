package sample.ui.history;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.controlsfx.control.textfield.TextFields;
import sample.Alert.AlertMaker;
import sample.Database.DatabaseHelper_Log;
import sample.Database.ExcelHelper;
import sample.Main;
import sample.Utils.Preferences;
import sample.model.Log;

import java.io.File;
import java.time.LocalDate;

/*
Stock History
*/
public class HistoryController {
    public JFXTextField searchBox;
    private Main mainApp;
    @FXML
    JFXComboBox<String> table, categoryCB, subCategoryCB, NameCB;
    @FXML
    BorderPane borderPane;
    @FXML
    JFXDatePicker fromDate, toDate;
    @FXML
    JFXCheckBox inCB, outCB, returnCB;
    @FXML
    public TableView<Log> tableView;

    @FXML
    public JFXButton addI, searchI;

    private ObservableList<Log> logs = FXCollections.observableArrayList();

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        initTables();
        table.getItems().addAll(Preferences.getPreferences().getTableNames());
        inCB.setSelected(true);
        outCB.setSelected(true);
        returnCB.setSelected(true);
        TextFields.bindAutoCompletion(searchBox, DatabaseHelper_Log.getNames());
        double i = 35;
        addI.setGraphic(new ImageView(new Image(Main.class.
                getResourceAsStream("resources/icons/refreshIcon.png")
                , i, i, true, true)));
        i = 30;
        searchI.setGraphic(new ImageView(new Image(Main.class.
                getResourceAsStream("resources/icons/search.png")
                , i, i, true, true)));
        toDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (fromDate.getValue() != null)
                    setDisable(empty || date.compareTo(fromDate.getValue()) < 0);
            }
        });

        inCB.setOnAction(e -> inOutReturnFunction());
        outCB.setOnAction(e -> inOutReturnFunction());
        returnCB.setOnAction(e -> inOutReturnFunction());

        table.valueProperty().addListener((observable, oldValue, newValue) -> {
            categoryCB.getItems().clear();
            categoryCB.setPromptText("Category");
            subCategoryCB.getItems().clear();
            subCategoryCB.setPromptText("Sub-Category");
            NameCB.getItems().clear();
            NameCB.setPromptText("Name");

            if (table.getSelectionModel().getSelectedItem() != null)
                categoryCB.getItems().addAll(DatabaseHelper_Log.getCategoriesFromLog(table.getValue()));
        });
        categoryCB.valueProperty().addListener((observable, oldValue, newValue) -> {

            subCategoryCB.getItems().clear();
            subCategoryCB.setPromptText("Sub-Category");
            NameCB.getItems().clear();
            NameCB.setPromptText("Name");

            if (categoryCB.getSelectionModel().getSelectedItem() != null)
                subCategoryCB.getItems().addAll(DatabaseHelper_Log.getSubCategoryFromLog(table.getValue(), categoryCB.getValue()));
        });
        subCategoryCB.valueProperty().addListener((observable, oldValue, newValue) -> {

            NameCB.getItems().clear();
            NameCB.setPromptText("Name");

            if (subCategoryCB.getSelectionModel().getSelectedItem() != null)
                NameCB.getItems().addAll(DatabaseHelper_Log.getProductNameFromLog(table.getValue(), categoryCB.getValue(), subCategoryCB.getValue()));
        });
    }

    private void inOutReturnFunction() {

        ObservableList<Log> log = FXCollections.observableArrayList();

        if (inCB.isSelected()) {
            for (Log l : logs) {
                if (l.getAction().equals("in")) log.add(l);
            }
        }
        if (outCB.isSelected()) {
            for (Log l : logs) {
                if (l.getAction().equals("out")) log.add(l);
            }
        }
        if (returnCB.isSelected()) {
            for (Log l : logs) {
                if (l.getAction().equals("return")) log.add(l);
            }
        }
        tableView.getItems().clear();

        tableView.getItems().addAll(log);
    }

    @FXML
    public void handleSearch() {
        loadTables();
    }

    private void initTables() {
        tableView.getColumns().clear();
        tableView.getItems().clear();

        addTableColumn("Name", "name");
        addTableColumn("Date", "date");
        addTableColumn("Entry", "entry");
        addTableColumn("Received", "received");
        addTableColumn("Issued", "issued");
        addTableColumn("Balance", "balance");
        addTableColumn("User", "user");
        addTableColumn("Action", "action");
        addTableColumn("Remarks", "Remarks");

        tableView.setTableMenuButtonVisible(true);
        logs = DatabaseHelper_Log.getAllLogList();
        tableView.getItems().addAll(logs);
    }

    @FXML
    public void refresh() {
        mainApp.snackBar("", "Refreshed", "green");

        table.getSelectionModel().clearSelection();
        categoryCB.getItems().clear();
        subCategoryCB.getItems().clear();
        NameCB.getItems().clear();

        table.setPromptText("Brand");
        categoryCB.setPromptText("Category");
        subCategoryCB.setPromptText("Sub-Category");
        NameCB.setPromptText("Name");

        loadTables();
    }

    private void loadTables() {
        //table
        if (searchBox.getText() != null && !searchBox.getText().equals("")) {
            logs = DatabaseHelper_Log.getLogLisWithSearchText(searchBox.getText());
        } else {
            if (!table.getSelectionModel().isEmpty()) {
                if (!categoryCB.getSelectionModel().isEmpty()) {
                    if (!subCategoryCB.getSelectionModel().isEmpty()) {
                        if (!NameCB.getSelectionModel().isEmpty()) {
                            if (fromDate.getValue() != null
                                    || toDate.getValue() != null) {
                                logs = DatabaseHelper_Log.getLogListNameWithDate(
                                        categoryCB.getValue()
                                        , subCategoryCB.getValue()
                                        , NameCB.getValue()
                                        , fromDate.getValue()
                                        , toDate.getValue());
                            } else {
                                logs = DatabaseHelper_Log.getLogListName(table.getValue()
                                        , categoryCB.getValue()
                                        , subCategoryCB.getValue()
                                        , NameCB.getValue());
                            }
                        } else {
                            if (fromDate.getValue() != null
                                    || toDate.getValue() != null) {
                                logs = DatabaseHelper_Log.getLogListSubCategoryWithDate(table.getValue()
                                        , categoryCB.getValue()
                                        , subCategoryCB.getValue()
                                        , fromDate.getValue()
                                        , toDate.getValue());
                            } else {
                                logs = DatabaseHelper_Log.getLogListSubCategory(table.getValue()
                                        , categoryCB.getValue(), subCategoryCB.getValue());

                            }
                        }
                    } else {
                        if (fromDate.getValue() != null || toDate.getValue() != null) {
                            logs = DatabaseHelper_Log.getLogListCategoryWithDate(table.getValue()
                                    , categoryCB.getValue(), fromDate.getValue(), toDate.getValue());
                        } else {
                            logs = DatabaseHelper_Log.getLogListCategory(table.getValue()
                                    , categoryCB.getValue());
                        }
                    }
                } else {
                    if (fromDate.getValue() != null || toDate.getValue() != null) {
                        logs = DatabaseHelper_Log.getLogListBrandWithDate(table.getValue()
                                , fromDate.getValue(), toDate.getValue());
                    } else {
                        logs = DatabaseHelper_Log.getLogListBrand(table.getValue());
                    }
                }
            } else {
                if ((fromDate.getValue() != null || toDate.getValue() != null)) {
                    logs = DatabaseHelper_Log.getLogListDateOnly(fromDate.getValue(), toDate.getValue());
                } else {
                    logs = DatabaseHelper_Log.getAllLogList();
                }
            }
        }
        inOutReturnFunction();
        tableView.getItems().clear();
        tableView.getItems().addAll(logs);
        inOutReturnFunction();
    }

    public void handleExportLogToExcel() {
        if (logs.size() == 0 || tableView.getItems().size() == 0) {
            mainApp.snackBar("", "Nothing to Import", "red");
            return;
        }
        mainApp.snackBar("", "Choose File", "green");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("File Save as");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(
                "Excel", "*.xlsx"));
        File dest = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        if (dest == null) {
            mainApp.snackBar("Info", "Operation Cancelled", "green");
        } else {
            mainApp.addSpinner();
            boolean ok = ExcelHelper.allLogsFromSqlToExcel(dest, tableView.getItems());
            mainApp.removeSpinner();
            if (ok)
                mainApp.snackBar("Success", "Stock History Data Written to Excel", "green");
            else
                mainApp.snackBar("Something Went Wrong"
                        , "Stock History Data is NOT written to Excel", "red");

        }
    }

    @FXML
    public void handleDelete() {
        if (tableView.getSelectionModel().getSelectedItems().size() == 0) {
            mainApp.snackBar("Info", "Nothing to delete", "red");
        } else {
            boolean ok = mainApp.getUser().getAccess().equals("admin") ||
                    AlertMaker.showSPAlert("Are you sure you want to delete history", mainApp);
            if (ok) {
                if (tableView.getSelectionModel().getSelectedItems().size() == 1) {
                    ObservableList<Log> l = FXCollections.observableArrayList();
                    Log log = tableView.getSelectionModel().getSelectedItem();
                    l.add(log);
                    tableView.getItems().remove(log);
                    if (DatabaseHelper_Log.deleteLogs(l)) {
                        mainApp.snackBar("Success", "Selected Log is Deleted", "green");
                    } else {
                        mainApp.snackBar("Failed", "Selected Log is not Deleted", "red");
                    }
                } else {
                    if (DatabaseHelper_Log.deleteLogs(logs)) {
                        mainApp.snackBar("Success", "Selected Log is Deleted", "green");
                        tableView.getItems().clear();
                        loadTables();
                    } else {
                        mainApp.snackBar("Failed", "Selected Log is not Deleted", "red");
                    }
                }
            } else {
                mainApp.snackBar("Info", "Operation Cancelled", "green");
            }
        }
    }

    private void addTableColumn(String name, String msg) {
        TableColumn<Log, String> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(msg));
        tableView.getColumns().add(column);
    }

}
