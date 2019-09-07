package sample.ui.viewStocks;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sample.Alert.AlertMaker;
import sample.Database.DatabaseHelper;
import sample.Database.DatabaseHelper_Product;
import sample.Database.ExcelHelper;
import sample.Main;
import sample.Utils.Preferences;
import sample.custom.tooltip.TooltippedTableCell;
import sample.model.Product;
import sample.model.User;

import java.io.File;


/**
 * View Stocks Module
 */
public class ViewStocksController {

    @FXML
    public TableView<Product> tableView;
    public StackPane root;
    @FXML
    BorderPane borderPane;
    @FXML
    JFXTextField searchBox, c, s, n, mrp, q, p, pl, min, h, r;
    @FXML
    JFXCheckBox editCheckBox;
    @FXML
    JFXComboBox<String> tableCB, categoryCB, subCategoryCB;
    @FXML
    VBox editPane;

    private Main mainApp;
    private Product singleSelectedItem = null;
    private User user;
    private Preferences preferences = Preferences.getPreferences();
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        init();
    }

    private void init() {
        this.user = mainApp.getUser();
        editCheckBox.setSelected(false);
        borderPane.setRight(null);
        tableCB.getItems().add("ALL");
        tableCB.getItems().addAll(preferences.getTableNames());
        tableCB.getSelectionModel().selectFirst();
        initTable();
        setAllProduct();
        if (user != null && !user.getAccess().equals("admin")) {
            min.setEditable(false);
            q.setEditable(false);
        }
        tableCB.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (tableCB.getValue() != null && !tableCB.getValue().isEmpty()) {
                setAllProduct();
            }
        });
        categoryCB.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (categoryCB.getValue() != null && !categoryCB.getValue().isEmpty()) {
                subCategoryCB.getItems().clear();
                subCategoryCB.getItems()
                        .addAll(DatabaseHelper_Product.getSubCategory(tableCB.getValue()
                                , categoryCB.getValue()));
                tableView.getItems().clear();
                tableView.getItems().addAll(DatabaseHelper_Product.getCategoryProductList(tableCB.getValue()
                                , categoryCB.getValue()));
            }
        });
        subCategoryCB.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (subCategoryCB.getValue() != null && !subCategoryCB.getValue().isEmpty()) {
                tableView.getItems().clear();
                tableView.getItems().addAll(DatabaseHelper_Product.getCSubProductList(tableCB.getValue()
                                , categoryCB.getValue(), subCategoryCB.getValue()));
            }
        });
        searchBox.textProperty().addListener((observable, oldValue, newValue) ->
                loadTable(searchBox.getText()));
        tableView.setOnMouseClicked(e -> {
            if (tableView.getSelectionModel().getSelectedItems().size() == 1) {
                singleSelectedItem = tableView.getSelectionModel().getSelectedItem();
                c.setText(singleSelectedItem.getCategory());
                s.setText(singleSelectedItem.getSubCategory());
                n.setText(singleSelectedItem.getName());
                mrp.setText(singleSelectedItem.getMRP());
                q.setText(singleSelectedItem.getQTY());
                p.setText(singleSelectedItem.getPartNo());
                pl.setText(singleSelectedItem.getPlace());
                min.setText(singleSelectedItem.getMin());
                h.setText(singleSelectedItem.getHsnCode());
                r.setText(singleSelectedItem.getRemarks());
            }
        });
        editCheckBox.setOnAction(e -> {
            if (editCheckBox.isSelected()) {
                borderPane.setRight(editPane);
            } else {
                borderPane.setRight(null);
            }
        });
    }

    private void initTable() {

        tableView.getColumns().clear();
        tableView.getItems().clear();

        addTableColumn("CATEGORY", "category", 125);
        addTableColumn("SUB-CATEGORY", "subCategory", 150);
        addTableColumn("NAME", "name", 250);
        addTableColumn("HSN", "hsnCode", 75);
        addTableColumn("PART.NO", "partNo", 125);
        addTableColumn("Qty", "QTY", 75);
        addTableColumn("Mrp", "MRP", 75);
        addTableColumn("PLACE", "place", 100);
        addTableColumn("REMARKS", "Remarks", 100);
        addTableColumn("Min", "min", 75);

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setTableMenuButtonVisible(true);
        loadTable("");
        borderPane.setCenter(tableView);
    }

    private void loadTable(String s) {
        if (tableCB.getValue() == null) return;
        tableView.getItems().clear();

        if (s.isEmpty()) {
            if (tableCB.getValue() != null && !tableCB.getValue().isEmpty()) {
                setAllProduct();
            }
        } else {
            if (tableCB.getValue().equals("ALL"))
                for (String s1 : preferences.getTableNames())
                    tableView.getItems().addAll(DatabaseHelper_Product.getProductList(s1, "%" + s + "%"));
            else
                tableView.getItems().addAll(DatabaseHelper_Product.getProductList(
                        tableCB.getValue(), "%" + s + "%"));
        }

    }

    private void setAllNull() {
        c.clear();
        s.clear();
        n.clear();
        p.clear();
        pl.clear();
        q.clear();
        mrp.clear();
        r.clear();
        h.clear();
        min.clear();
    }

    private void addTableColumn(String name, String msg, int width) {
        TableColumn<Product, String> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(msg));
        column.setCellFactory(TooltippedTableCell.forTableColumn());
        column.setPrefWidth(width);
        column.setResizable(false);
        tableView.getColumns().add(column);
    }

    @FXML
    public void handleSearch() {
        loadTable(searchBox.getText());
    }

    @FXML
    public void handleDelete() {
        ObservableList<Product> deleteList = tableView.getSelectionModel().getSelectedItems();

        if (deleteList.size() == 0) {
            mainApp.snackBar("Info", "Select a row first", "green");
        } else {
            try {
                setAllNull();

                boolean okay;
                if (!user.getAccess().equals("admin")) {
                    okay = mainApp.specialPassword("Are you sure you want to\n delete "
                            + deleteList.size() + " Stock items?");
                } else {
                    okay = AlertMaker.showMCAlert("Confirm"
                            , "Are you sure you want to delete the stocks? "
                            , mainApp);
                }
                if (okay) {
                    boolean ok = true;
                    for (Product pr : deleteList) {
                        ok = ok && DatabaseHelper_Product.deleteProduct(pr, tableCB.getValue());
                    }
                    if (ok) {
                        mainApp.snackBar("Deleted"
                                , "Selected stock item(s) are deleted"
                                , "green");
                    } else {
                        mainApp.snackBar("Failed"
                                , "Selected stock item(s) are not deleted"
                                , "red");
                    }
                }
                loadTable("");
            } catch (Exception e) {
                e.printStackTrace();
                AlertMaker.showErrorMessage(e);
            }
        }
    }

    @FXML
    public void handleBrandDelete() {
        try {
            setAllNull();
            boolean okay;
            if (tableCB.getValue() == null || tableCB.getValue().isEmpty()) return;
            if (!user.getAccess().equals("admin")) {
                okay = mainApp.specialPassword("Are you sure you want to \n " +
                        "delete " + tableCB.getValue() + " brand?");
            } else {
                okay = AlertMaker.showMCAlert("Confirm"
                        , "Are you sure you want to delete the " + tableCB.getValue() + " brand?"
                        , mainApp);

            }
            if (okay) {
                if (DatabaseHelper.deleteTable(tableCB.getValue())) {
                    mainApp.snackBar("Success", "Selected Brand is Deleted", "green");

                    tableCB.getItems().clear();
                    tableCB.getItems().addAll(Preferences.getPreferences().getTableNames());
                    tableCB.getSelectionModel().selectFirst();
                    loadTable("");
                    mainApp.initViewStocks();
                } else {
                    mainApp.snackBar("Failed", "Selected Brand is not Deleted", "red");
                }
            }
            loadTable("");
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);
        }
    }

    @FXML
    private void handleDownload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new
                FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        File dest = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        if (dest == null) {
            mainApp.snackBar(""
                    , " Download Cancelled"
                    , "red");
        } else {
            mainApp.addSpinner();
            boolean ok = ExcelHelper.productSQLToExcel(dest);
            mainApp.removeSpinner();
            if (ok)
                mainApp.snackBar("Success"
                        , "All the stock Data are written to excel"
                        , "green");

            else
                mainApp.snackBar("Failed"
                        , "Stock Data is not downloaded to excel"
                        , "red");

        }
    }

    @FXML
    public void handleEdit() {
        if (singleSelectedItem != null) {
            if (!singleSelectedItem.getCategory().equals(c.getText())) {

                if (DatabaseHelper_Product.changeCategoryOrSubC(singleSelectedItem.getCategory()
                        , c.getText(), tableCB.getValue(), true)) {
                    mainApp.snackBar("Success"
                            , singleSelectedItem.getCategory() + " Category changed to " + c.getText()
                            , "green");
                } else {
                    mainApp.snackBar("Failed"
                            , singleSelectedItem.getCategory()
                                    + " Category not changed to " + c.getText()
                            , "red");
                }
            }
            if (!singleSelectedItem.getSubCategory().equals(s.getText())) {
                if (DatabaseHelper_Product.changeCategoryOrSubC(singleSelectedItem.getSubCategory()
                        , s.getText(), tableCB.getValue(), false)) {
                    mainApp.snackBar("Success"
                            , singleSelectedItem.getSubCategory()
                                    + " Sub-Category changed to " + s.getText()
                            , "green");
                } else {
                    mainApp.snackBar("Failed"
                            , singleSelectedItem.getSubCategory() + " Sub-Category" +
                                    " not changed to " + s.getText()
                            , "red");
                }
            }
            if (!singleSelectedItem.getName().equals(n.getText())) {
                if (DatabaseHelper_Product.changeName(singleSelectedItem.getName()
                        , n.getText()
                        , singleSelectedItem.getCategory()
                        , singleSelectedItem.getSubCategory()
                        , tableCB.getValue())) {
                    mainApp.snackBar("Success"
                            , singleSelectedItem.getName() + " Sub-Category changed to " + n.getText()
                            , "green");
                } else {
                    mainApp.snackBar("Failed"
                            , singleSelectedItem.getName() + " Sub-Category" +
                                    " not changed to " + n.getText()
                            , "red");
                }
            }

            singleSelectedItem.setCategory(c.getText());
            singleSelectedItem.setSubCategory(s.getText());
            singleSelectedItem.setName(n.getText());
            singleSelectedItem.setMRP(mrp.getText());
            singleSelectedItem.setQTY(q.getText());
            singleSelectedItem.setPartNo(p.getText());
            singleSelectedItem.setPlace(pl.getText());
            singleSelectedItem.setMin(min.getText());
            singleSelectedItem.setHsnCode(h.getText());
            singleSelectedItem.setRemarks(r.getText());

            if (DatabaseHelper_Product.updateProduct(singleSelectedItem, tableCB.getValue())) {
                mainApp.snackBar("Success"
                        , singleSelectedItem.getName() + " stock is updated"
                        , "green");
                setAllNull();
                loadTable("");
                singleSelectedItem = null;
            } else {
                mainApp.snackBar("Failed"
                        , singleSelectedItem.getName() + " stock is not updated"
                        , "red");
            }
        } else {
            mainApp.snackBar("INFO", "Select a product", "info");
        }
    }

    private void setAllProduct() {
        tableView.getItems().clear();
        if (!tableCB.getValue().equals("ALL")) {
            categoryCB.getItems().clear();
            categoryCB.getItems().addAll(DatabaseHelper_Product.getCategories(tableCB.getValue()));
            tableView.getItems().addAll(DatabaseHelper_Product.getProductList(tableCB.getValue()));
        } else {
            categoryCB.getItems().clear();
            subCategoryCB.getItems().clear();
            tableView.getItems().clear();
            for (String s : preferences.getTableNames()) {
                tableView.getItems().addAll(DatabaseHelper_Product.getProductList(s));
            }
        }
    }
}
