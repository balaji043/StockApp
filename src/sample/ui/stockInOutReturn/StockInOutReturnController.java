package sample.ui.stockInOutReturn;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.textfield.TextFields;
import sample.Alert.AlertMaker;
import sample.Database.DatabaseHelper;
import sample.Main;
import sample.Utils.Preferences;
import sample.custom.singlestock.SingleStock;

/*
 * Stock In
 *
 * */

public class StockInOutReturnController {

    public JFXListView<SingleStock> listView;

    private Main mainApp;
    private String action;

    @FXML
    private ComboBox<String> table;

    @FXML
    private JFXButton a, d;


    public void init(Main mainApp, String action) {
        this.mainApp = mainApp;
        this.action = action;
        if (!action.equals("in")) table.setEditable(false);
        int i = 35;
        a.setGraphic(new ImageView(new Image(Main.class.
                getResourceAsStream("resources/icons/add.png")
                , i, i, true, true)));
        d.setGraphic(new ImageView(new Image(Main.class.
                getResourceAsStream("resources/icons/delete.png")
                , i, i, true, true)));

        listView.setExpanded(true);
        listView.setVerticalGap(5.0);
        table.getItems().addAll(Preferences.getPreferences().getTableNames());
        TextFields.bindAutoCompletion(table.getEditor(), table.getItems());
    }

    @FXML
    public void handleSubmit() {
        if (listView.getItems().size() == 0) {
            mainApp.snackBar("", "Nothing to Submit", "red");
            return;
        }
        if (action.equals("return")) {
            try {
                boolean okay;
                if (!mainApp.getUser().getAccess().equals("admin")) {
                    okay = mainApp.specialPassword("Are you sure you want to \n return these Stocks");
                } else {
                    okay = AlertMaker.showMCAlert("Confirm?"
                            , listView.getItems().size() + " items are to be returned"
                            , mainApp);
                }
                if (okay) {
                    stockOp();
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertMaker.showErrorMessage(e);
            }

        } else {
            stockOp();
        }
    }

    @FXML
    private void handleAdd() {

        if (table.getValue() == null || table.getValue().isEmpty()) {
            mainApp.snackBar("", "Brand Not Selected.\nSelect Brand first.", "red");
            listView.getItems().clear();
        } else {
            DatabaseHelper.createProductTable(table.getValue());
            try {
                SingleStock singleStock = new SingleStock(action, table.getValue(), mainApp);
                listView.getItems().add(singleStock);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void handleRemove() {
        if (listView.getSelectionModel().getSelectedItems().size() != 0
                && listView.getSelectionModel().getSelectedItem() != null) {
            listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
        } else {
            mainApp.snackBar("", "Select a row to Delete", "red");
        }

    }

    private void stockOp() {
        for (int i = listView.getItems().size() - 1; i >= 0; i--) {
            try {
                if (listView.getItems().get(i).handleSubmit()) {
                    listView.getItems().remove(i);
                }
            } catch (Exception e) {
                AlertMaker.showErrorMessage(e);
            }
        }
        if (listView.getItems().size() == 0) {
            mainApp.snackBar("Success", "All the Stock Items are" +
                    " successfully updated", "Green");

        }
        if (listView.getItems().size() != 0) {
            mainApp.snackBar("Info", "Stock items not Updated are marked red"
                    , "red");
        }

    }
}
