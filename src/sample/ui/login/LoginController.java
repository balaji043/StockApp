package sample.ui.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import sample.Database.DatabaseHelper;
import sample.Main;


public class LoginController {

    @FXML
    private JFXTextField textFieldUserName;
    @FXML
    private JFXPasswordField textFieldPassword;
    private RequiredFieldValidator validator = new RequiredFieldValidator();

    private Main mainApp;

    @FXML
    private void handleSignIn() {
        try {
            mainApp.addSpinner();
            String user = textFieldUserName.getText(), password = textFieldPassword.getText();
            if (!user.isEmpty() && !password.isEmpty()) {
                if (DatabaseHelper.valid(user, password)) {
                    mainApp.snackBar("","Welcome "+user, "green");
                    mainApp.setUser(DatabaseHelper.getUserInfo(user));
                    mainApp.initMenuLayout();
                } else {
                    mainApp.snackBar("","Wrong!\nTry Again!", "red");
                }
            } else {
                if (textFieldUserName.getText().isEmpty() &&
                        textFieldPassword.getText().isEmpty()) {
                    mainApp.snackBar("","Enter Both Fields", "red");
                    textFieldPassword.validate();
                    textFieldPassword.validate();
                } else if (textFieldPassword.getText().isEmpty()) {
                    mainApp.snackBar("","Enter Password","red");
                    textFieldPassword.validate();
                } else {
                    mainApp.snackBar("","Enter UserName","red");
                    textFieldUserName.validate();
                }
            }
            mainApp.removeSpinner();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        validator.setMessage("*");
        textFieldUserName.requestFocus();
        textFieldUserName.getValidators().add(validator);
        textFieldUserName.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) textFieldUserName.validate();
        });

        textFieldPassword.getValidators().add(validator);
        textFieldPassword.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) textFieldPassword.validate();
        });
        mainApp.getPrimaryStage().getScene().setOnKeyPressed(e -> {
            if (!mainApp.isLoggedIn && e.getCode() == KeyCode.ENTER) {
                handleSignIn();
            }
        });
    }
}
