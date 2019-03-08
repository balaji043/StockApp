package sample.custom.singleStock;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.IntegerValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;
import sample.Alert.AlertMaker;
import sample.Database.DatabaseHelper;
import sample.Main;
import sample.model.Log;
import sample.model.Product;

import java.io.IOException;
import java.util.Date;

public class Control1SingleStock extends HBox {

    @FXML
    private JFXComboBox<String> category, subCategory, name;
    @FXML
    private Label brandLabel;
    @FXML
    private HBox hBox;
    @FXML
    private VBox vBox;
    @FXML
    private JFXTextField mrp, qty, hsn, partno, place, remarks, min;

    private String tableName, action;
    private Product product1 = null;
    private boolean isProductExist = false;
    private Main main;

    private RequiredFieldValidator validator = new RequiredFieldValidator();

    public Control1SingleStock(String action, String tableName, Main main) {
        this.tableName = tableName;
        this.action = action;
        this.main = main;
        validator.setMessage("*");
        RequiredFieldValidator validator1 = new RequiredFieldValidator();
        validator1.setMessage("* Num Req");
        IntegerValidator validator2 = new IntegerValidator("* Num Req");

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource
                ("custom/singleStock/singleStock.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            if (!action.equals("in")) {
                category.setEditable(false);
                subCategory.setEditable(false);
                name.setEditable(false);
                hBox.getChildren().removeAll(partno, hsn, place, min,mrp);
            }

            category.getValidators().add(validator);
            subCategory.getValidators().add(validator);
            name.getValidators().add(validator);
            hsn.getValidators().add(validator);
            mrp.getValidators().addAll(validator1);
            qty.getValidators().addAll(validator1);
            min.getValidators().add(validator2);
            remarks.getValidators().add(validator);

            category.getItems().addAll(DatabaseHelper.getCategories(tableName));

            category.focusedProperty().addListener((o, oldVal, newVal) -> {
                if (!newVal) {
                    category.getValidators().add(validator);
                    category.validate();
                }
            });
            subCategory.focusedProperty().addListener((o, oldVal, newVal) -> {
                if (!newVal) {
                    subCategory.getValidators().add(validator);
                    subCategory.validate();
                }
            });
            name.focusedProperty().addListener((o, oldVal, newVal) -> {
                if (!newVal) {
                    name.getValidators().add(validator);
                    name.validate();
                } else name.getValidators().clear();
            });
            hsn.focusedProperty().addListener((o, oldVal, newVal) -> {
                if (newVal) {
                    hsn.getValidators().clear();
                }
            });
            mrp.focusedProperty().addListener((o, oldVal, newVal) -> {
                if (newVal) {
                    mrp.getValidators().clear();
                }
            });
            qty.focusedProperty().addListener((o, oldVal, newVal) -> {
                if (newVal) {
                    qty.getValidators().clear();
                }
            });

            if (action.equals("return")) remarks.validate();

            category.validate();
            subCategory.validate();
            name.validate();
            hsn.validate();
            mrp.validate();
            qty.validate();
            min.validate();

            category.valueProperty().addListener((observable, oldValue, newValue) -> {
                vBox.setStyle("-fx-background-color:white");
                subCategory.getItems().clear();
                name.getItems().clear();
                if (category.getValue() != null) {
                    subCategory.getItems().addAll(DatabaseHelper.getSubCategory(tableName
                            , category.getValue()));
                }
            });
            subCategory.valueProperty().addListener((observable, oldValue, newValue) -> {
                name.getItems().clear();
                if (subCategory.getValue() != null) {
                    name.getItems().addAll(DatabaseHelper.getProductName(
                            tableName, category.getValue(), subCategory.getValue()));
                }
            });
            name.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (category.getValue() != null && subCategory.getValue() != null
                        && name.getValue() != null) {
                    isProductExist = DatabaseHelper.isProductExistS(new Product(category.getValue(),
                            subCategory.getValue(), name.getValue()), tableName);
                    if (isProductExist) {
                        product1 = DatabaseHelper.getProductInfo(new Product(category.getValue(),
                                subCategory.getValue(), name.getValue()), tableName);
                        mrp.setPromptText("Mrp: " + product1.getMRP());
                        qty.setPromptText("Qty: " + product1.getQTY());
                        hsn.setPromptText("Hsn: " + product1.getHsnCode());
                        partno.setPromptText("Pno: " + product1.getPartNo());
                        place.setPromptText("Plc: " + product1.getPlace());
                        remarks.setPromptText("Rem: " + product1.getRemarks());
                        min.setPromptText("Min: " + product1.getMin());
                    }
                }
            });


            TextFields.bindAutoCompletion(category.getEditor(), category.getItems());

            category.getValidators().add(validator);
            subCategory.getValidators().add(validator);
            name.getValidators().add(validator);
            mrp.getValidators().add(validator1);
            partno.getValidators().add(validator);
            qty.getValidators().addAll(validator1);
            hsn.getValidators().add(validator);
            remarks.getValidators().add(validator);
            min.getValidators().add(validator1);
            brandLabel.setText(tableName);

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public boolean handleSubmit() {
        switch (action) {
            case "in": {
                return stockIn();
            }
            case "out": {
                return stockOut();
            }
            case "return": {
                return stockReturn();
            }
        }
        vBox.setStyle("-fx-background-color:#ffcccc");
        main.snackBar("Failed", product1.getName() +
                " Something went wrong !", "red");
        return false;
    }

    private Product getP() {
        return new Product(category.getValue()
                , subCategory.getValue(), name.getValue());
    }

    private boolean stockIn() {
        String r = "", pl = "";
        Date date = new Date();
        String entry = "0", received = "0", issued = "0", balance = "0";
        int a, b, q;
        boolean qtyChanged = false;
        boolean submitted;
        try {
            if (isProductExist) {
                product1 = DatabaseHelper.getProductInfo(getP(), tableName);
                if (partno.getText() != null && !partno.getText().isEmpty()) {
                    product1.setPartNo(partno.getText());
                }
                if (qty.getText() != null && !qty.getText().isEmpty()) {
                    try {
                        entry = "" + product1.getQTY();
                        a = Integer.parseInt(product1.getQTY());
                        b = Integer.parseInt(qty.getText());
                        received = "" + b;
                        q = a + b;
                        qtyChanged = true;
                        balance = "" + q;
                        product1.setQTY("" + q);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
                if (mrp.getText() != null && !mrp.getText().isEmpty()) {
                    product1.setMRP(mrp.getText());
                }
                if (hsn.getText() != null && !hsn.getText().isEmpty()) {
                    product1.setHsnCode(hsn.getText());
                }
                if (place.getText() != null && !place.getText().isEmpty()) {
                    product1.setPlace(place.getText());
                }
                if (remarks.getText() != null && !remarks.getText().isEmpty()) {
                    product1.setRemarks(remarks.getText());
                }
                if (min.getText() != null && !min.getText().isEmpty()) {
                    product1.setMin(min.getText());
                }
                if (qtyChanged) {
                    Log log = new Log(tableName
                            , "" + date.getTime()
                            , product1.getCategory()
                            , product1.getSubCategory()
                            , product1.getName()
                            , entry
                            , received
                            , issued
                            , balance
                            , main.getUser().getUserName()
                            , action, product1.getRemarks());

                    submitted = DatabaseHelper.updateProduct(product1, tableName) &&
                            DatabaseHelper.insertNewLog(log);
                } else {
                    submitted = DatabaseHelper.updateProduct(product1, tableName);
                }
                vBox.setStyle("-fx-background-color:#ccffcc");
                if (submitted) {
                    main.snackBar("Success", product1.getName() +
                            " Stock Item Added!", "green");
                } else {
                    main.snackBar("Failed", product1.getName() +
                            " Stock Item is not Added !", "red");
                }
                return submitted;
            } else {
                if (category.getValue() == null || category.getValue().isEmpty()
                        || subCategory.getValue() == null || subCategory.getValue().isEmpty()
                        || name.getValue() == null || name.getValue().isEmpty()
                        || mrp.getText() == null || mrp.getText().isEmpty()
                        || qty.getText() == null || qty.getText().isEmpty()
                        || hsn.getText() == null || hsn.getText().isEmpty()) {
                    if (category.getValue() == null) category.validate();
                    if (subCategory.getValue() == null) subCategory.validate();
                    if (name.getValue() == null) name.validate();
                    if (qty.getText().isEmpty()) qty.validate();
                    if (mrp.getText() == null) mrp.validate();
                    if (hsn.getText().isEmpty()) hsn.validate();
                    vBox.setStyle("-fx-background-color:#ffcccc");
                    main.snackBar("Error", "fields are empty", "red");
                    return false;
                } else {
                    if (remarks.getText() != null && !remarks.getText().isEmpty()) {
                        r = remarks.getText();
                    }
                    if (place.getText() != null && !place.getText().isEmpty()) {
                        pl = place.getText();
                    }
                    Product p = new Product(category.getValue(),
                            "" + subCategory.getValue(),
                            "" + name.getValue(),
                            "" + partno.getText(),
                            "" + qty.getText(),
                            "" + mrp.getText(),
                            "" + hsn.getText(),
                            pl,
                            r,
                            tableName
                    );

                    if (min.getText() != null && !min.getText().isEmpty())
                        p.setMin(min.getText());

                    received = p.getQTY();
                    balance = received;

                    Log log = new Log(tableName
                            , "" + date.getTime()
                            , p.getCategory()
                            , p.getSubCategory()
                            , p.getName()
                            , entry
                            , received
                            , issued
                            , balance
                            , main.getUser().getUserName()
                            , action, p.getRemarks());

                    submitted = DatabaseHelper.insertNewProduct(p, tableName)
                            && DatabaseHelper.insertNewLog(log);
                    vBox.setStyle("-fx-background-color:#ccffcc");
                    if (submitted) {
                        main.snackBar("Success", p.getName() +
                                " item " + issued + " is out !", "green");
                    } else {
                        main.snackBar("Failed", p.getName() +
                                " stock item is not out !", "red");
                    }
                    return submitted;
                }
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return false;
    }

    private boolean stockOut() {
        Date date = new Date();
        String entry, received = "0", issued, balance;
        int a, b, q;
        boolean submitted;

        try {
            product1 = DatabaseHelper.getProductInfo(getP(), tableName);
            if (qty.getText() != null && !qty.getText().isEmpty()) {
                try {
                    entry = "" + product1.getQTY();
                    a = Integer.parseInt(product1.getQTY());
                    b = Integer.parseInt(qty.getText());
                    if (a - b >= 0) {
                        issued = "" + b;
                        q = a - b;
                    } else {
                        main.snackBar("Error", product1.getName()
                                + " consists of " + product1.getQTY() + " only." +
                                "You are trying to issue " + b + "." +
                                " Operation cannot be done!", "red");
                        return false;
                    }
                    balance = "" + q;
                    product1.setQTY("" + q);
                } catch (NumberFormatException e) {
                    return false;
                }
            } else {
                qty.validate();
                return false;
            }
            if (remarks.getText() != null && !remarks.getText().isEmpty()) {
                product1.setRemarks(remarks.getText());
            }

            Log log = new Log(tableName, "" + date.getTime(), product1.getCategory()
                    , product1.getSubCategory(), product1.getName(), entry, received, issued
                    , balance, main.getUser().getUserName(), action, product1.getRemarks());

            submitted = DatabaseHelper.updateProduct(product1, tableName) &&
                    DatabaseHelper.insertNewLog(log);

            if (submitted) {
                main.snackBar("Success", product1.getName() +
                        " stock item is updated !", "green");
            } else {
                main.snackBar("Failed", product1.getName() +
                        " stock item is not updated !", "green");
            }

            return submitted;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return false;
    }

    private boolean stockReturn() {
        Date date = new Date();
        String entry, received, issued = "0", balance;
        int a, b, q;
        boolean submitted;
        try {
            product1 = DatabaseHelper.getProductInfo(getP(), tableName);
            if (qty.getText() != null && !qty.getText().isEmpty()) {
                try {
                    entry = "" + product1.getQTY();
                    a = Integer.parseInt(product1.getQTY());
                    b = Integer.parseInt(qty.getText());
                    received = "" + b;
                    q = a + b;
                    balance = "" + q;
                    product1.setQTY("" + q);
                } catch (NumberFormatException e) {
                    return false;
                }
            } else {
                qty.validate();
                main.snackBar("Failed", product1.getName() +
                        " Enter Quantity !", "green");
                return false;
            }
            if (remarks.getText() != null) {
                product1.setRemarks(remarks.getText());
            } else {
                main.snackBar("Failed", product1.getName() +
                        " remarks is important for sales return!", "red");
                return false;
            }

            Log log = new Log(tableName, "" + date.getTime(), product1.getCategory()
                    , product1.getSubCategory(), product1.getName(), entry, received
                    , issued, balance, main.getUser().getUserName(), action, product1.getRemarks());

            submitted = DatabaseHelper.updateProduct(product1, tableName) &&
                    DatabaseHelper.insertNewLog(log);
            if (submitted) {
                main.snackBar("Success", product1.getName() +
                        " stock item is returned !", "green");
            } else {
                main.snackBar("Failed", product1.getName() +
                        " stock item is not returned !", "red");
            }
            return submitted;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return false;
    }
}