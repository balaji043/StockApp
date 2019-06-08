package sample.ui.addUser;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import sample.Alert.AlertMaker;
import sample.Database.DatabaseHelper_User;
import sample.Main;
import sample.Utils.Preferences;
import sample.model.User;


public class AddNewUserController {
    public StackPane root;
    @FXML
    private JFXTextField name, username;
    @FXML
    private JFXPasswordField passwordField, passwordField1;
    @FXML
    private JFXComboBox<String> accessComboBox;
    @FXML
    private BorderPane borderPane;
    @FXML
    public TableView<User> userTableView;
    private User user = null;
    private boolean isNewUser = true;
    private Main mainApp;
    private String[] access = {"admin", "employee"};
    private String emp = null;
    private RequiredFieldValidator validator = new RequiredFieldValidator();

    public void setMainApp(Main main) {
        accessComboBox.getItems().addAll(access);
        this.mainApp = main;
        initTable();
        validator.setMessage("Cannot Delete Admin");

        accessComboBox.getValidators().add(validator);
        userTableView.setOnMouseClicked(e -> {
            user = userTableView.getSelectionModel().getSelectedItem();
            clearAll();
            if (user == null) return;
            isNewUser = false;
            name.setText(user.getName());
            emp = user.getId();
            accessComboBox.getSelectionModel().select(user.getAccess());
            username.setText(user.getUserName());
            passwordField.setText(user.getPassword());
        });
    }

    private void initTable() {
        userTableView.getItems().clear();
        userTableView.getColumns().clear();
        addTableColumn("Name", "name");
        addTableColumn("Employee ID", "id");
        addTableColumn("User Name", "userName");
        addTableColumn("Access", "Access");
        userTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        userTableView.setTableMenuButtonVisible(true);
        loadTable();
        borderPane.setCenter(userTableView);
    }

    private void loadTable() {
        userTableView.getItems().clear();
        userTableView.getItems().addAll(DatabaseHelper_User.getUserList());
    }

    @FXML
    void handleAddNow() {
        if (isNewUser) {
            if (name.getText() == null || name.getText().isEmpty()
                    || username.getText() == null || username.getText().isEmpty()
                    || passwordField.getText() == null || passwordField.getText().isEmpty()
                    || accessComboBox.getValue() == null || accessComboBox.getValue().isEmpty()) {
                if (name.getText() == null || name.getText().isEmpty()) {
                    name.validate();
                }
                if (username.getText() == null || username.getText().isEmpty()) {
                    username.validate();
                }
                if (passwordField.getText() == null || passwordField.getText().isEmpty()) {
                    passwordField.validate();
                }
                if (accessComboBox.getValue() == null || accessComboBox.getValue().isEmpty()) {
                    accessComboBox.validate();
                }
            } else {

                user = new User("" + name.getText()
                        , "" + username.getText()
                        , "emp" + (DatabaseHelper_User.getUserList().size())
                        , "" + passwordField.getText()
                        , "" + accessComboBox.getValue());

                if (DatabaseHelper_User.insertNewUser(user)) {
                    mainApp.snackBar("Success", "User Added Successfully", "green");
                    clearAll();
                } else {
                    mainApp.snackBar("Failed", "User Not Added", "red");
                }

            }
        } else {
            if (name.getText() == null
                    || username.getText() == null
                    || passwordField.getText() == null
                    || accessComboBox.getValue() == null) {
                if (name.getText() == null || name.getText().isEmpty()) {
                    name.validate();
                }
                if (username.getText() == null || username.getText().isEmpty()) {
                    username.validate();
                }
                if (passwordField.getText() == null || passwordField.getText().isEmpty()) {
                    passwordField.validate();
                }
                if (accessComboBox.getValue() == null || accessComboBox.getValue().isEmpty()) {
                    accessComboBox.validate();
                }
            } else {
                user = new User("" + name.getText()
                        , "" + username.getText()
                        , emp
                        , "" + passwordField.getText()
                        , "" + accessComboBox.getValue());
                if (DatabaseHelper_User.updateUser(user)) {
                    clearAll();
                    mainApp.snackBar("Success", "User Data Updated Successfully", "green");
                } else {
                    mainApp.snackBar("Failed"
                            , "User Data Not Updated Successfully", "red");

                }
            }
        }
        loadTable();
    }

    private void clearAll() {
        name.clear();
        username.clear();
        passwordField.clear();
        name.setPromptText("Name");
        passwordField.setPromptText("Password");
        accessComboBox.getSelectionModel().clearSelection();
        accessComboBox.setPromptText("Grant Access");
        isNewUser = true;
    }

    @FXML
    public void handleBack() {
        try {
            User users = userTableView.getSelectionModel().getSelectedItem();
            boolean okay;
            if (users.getAccess().equals("admin")) {
                mainApp.snackBar("Failed", "Cannot Delete Admin User", "red");
                return;
            }
            okay = AlertMaker.showMCAlert("Confirm"
                    , "Are you sure you want to delete" + users.getName() + "'s data"
                    , mainApp);

            if (okay) {
                boolean ok = DatabaseHelper_User.deleteUser(users);
                if (ok) {
                    mainApp.snackBar("Success"
                            , "Selected User's data is deleted"
                            , "green");

                } else {
                    mainApp.snackBar("Failed"
                            , "Selected User's data is not deleted"
                            , "red");
                }
                clearAll();
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
        loadTable();
    }

    @FXML
    public void handleSpecial() {
        if (!passwordField1.getText().isEmpty()) {
            Preferences preferences = Preferences.getPreferences();
            preferences.setS("" + passwordField1.getText().hashCode());
            Preferences.setPreference(preferences);
            mainApp.snackBar("Success", "You special password is changed now.", "green");
        }
    }

    private void addTableColumn(String name, String msg) {
        TableColumn<User, String> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(msg));
        userTableView.getColumns().add(column);
    }

}
