package sample;

import com.jfoenix.controls.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Alert.AlertMaker;
import sample.Database.DatabaseHelper;
import sample.Utils.Preferences;
import sample.model.User;
import sample.ui.addUser.AddNewUserController;
import sample.ui.history.HistoryController;
import sample.ui.login.LoginController;
import sample.ui.need.NeedController;
import sample.ui.root.RootController;
import sample.ui.stockInOutReturn.StockInOutReturnController;
import sample.ui.viewStocks.ViewStocksController;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static sample.Alert.AlertMaker.alertDialogBox;

public class Main extends Application {

    private User user = null;
    private Stage primaryStage;
    private BorderPane rootLayout;
    public boolean isLoggedIn = false;
    private RootController rootController = null;
    private JFXSpinner spinner = new JFXSpinner();


    @Override
    public void start(Stage primaryStage) throws Exception {
        double d = 50;
        spinner.setMaxSize(d, d);
        spinner.setPrefSize(d, d);

        this.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("ui/root/Root.fxml"));
        rootLayout = loader.load();
        primaryStage.setTitle("Stock Management");
        JFXDecorator decorator = new JFXDecorator(this.primaryStage, rootLayout);
        decorator.setCustomMaximize(true);
        decorator.setMaximized(true);

        Scene scene = new Scene(decorator, 1080, 720);
        scene.getStylesheets().add(Main.class.getResource("resources/CSS/" +
                Preferences.getPreferences().getTheme() + "Theme.css")
                .toExternalForm());

        primaryStage.setScene(scene);
        DatabaseHelper.setMain(Main.this);
        rootController = loader.getController();
        rootController.setMainApp(Main.this);
        primaryStage.show();
        DatabaseHelper.create();
        this.primaryStage.setOnCloseRequest(event -> {
            event.consume();
            handleClose();
        });

        try {
            checkPreferences();
            initLoginLayout();
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    //Login
    private void initLoginLayout() {
        isLoggedIn = false;
        try {
            rootLayout.getTop().setVisible(false);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("ui/login/Login.fxml"));
            StackPane anchorPane = loader.load();
            rootLayout.setCenter(anchorPane);
            LoginController loginController = loader.getController();
            loginController.setMainApp(this);
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);
        }
    }

    //Menu
    public void initMenuLayout() {
        isLoggedIn = true;
        checkPreferences();
        try {
            rootLayout.getTop().setVisible(true);
            rootController.viewStock();
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
    }

    //Stock In or Out or Return
    public void initStockInOutReturn(String action) {
        checkPreferences();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/stockInOutReturn/StockInOutReturn.fxml"));
            StackPane root = loader.load();
            rootController.setContent(root);
            StockInOutReturnController controller = loader.getController();
            controller.init(this, action);

        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
    }

    //View Stocks
    public void initViewStocks() {
        checkPreferences();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/viewStocks/ViewStocks.fxml"));
            StackPane root = loader.load();
            rootController.setContent(root);
            ViewStocksController controller = loader.getController();
            controller.setMainApp(Main.this);
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);
        }

    }

    //Import Stocks
    public void initImportStocks() {
        try {
            boolean okay;
            if (!user.getAccess().equals("admin")) {
                okay = specialPassword("Are you sure you want to \n import Stocks?");
            } else {
                okay = AlertMaker.showMCAlert("Confirm"
                        , "Are you sure you want to import these stock Items?"
                        , Main.this);
            }
            if (okay) {
                try {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.getExtensionFilters().addAll(new
                            FileChooser.ExtensionFilter("Excel", "*.xlsx"));
                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        Preferences preferences = Preferences.getPreferences();
                        preferences.setPath(file.getPath());
                        Preferences.setPreference(preferences);
                        Alert alert = alertDialogBox("Please wait "
                                , "Uploading Data to database"
                                , "Do not close the application. Please wait.",
                                Alert.AlertType.WARNING, primaryStage);

                        alert.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
                        alert.initModality(Modality.NONE);
                        alert.show();
                        JFXProgressBar jfxBar = new JFXProgressBar();
                        jfxBar.setPrefWidth(500);
                        JFXProgressBar jfxBarInf = new JFXProgressBar();
                        jfxBarInf.setPrefWidth(500);
                        jfxBarInf.setProgress(-1.0f);

                        if (DatabaseHelper.excelTOSQLite()) {
                            alert.close();

                            snackBar("Success", "All the Stock items are updated to" +
                                    " Database" + "\nYou can continue now", "green");
                            checkPreferences();
                            initViewStocks();
                        } else {
                            alert.close();
                            snackBar("Failed", "Not Updated Database", "red");
                            checkPreferences();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertMaker.showErrorMessage(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);
        }
    }

    //Export Stocks
    public void initNeed() {
        checkPreferences();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/need/need.fxml"));
            StackPane root = loader.load();
            rootController.setContent(root);
            NeedController needController = loader.getController();
            needController.setMainApp(Main.this);
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);
        }

    }

    //Stock History
    public void initStockHistory() {
        checkPreferences();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ui/history/History.fxml"));
            StackPane root = loader.load();
            rootController.setContent(root);
            HistoryController controller = loader.getController();
            controller.setMainApp(Main.this);
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);

        }

    }

    //Add New User
    public void initAddNewUser() {
        try {
            if (user.getAccess().equals("admin")) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("ui/addUser/AddNewUser.fxml"));
                StackPane root = loader.load();
                rootController.setContent(root);
                AddNewUserController controller = loader.getController();
                controller.setMainApp(Main.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);

        }
    }

    //Get User
    public User getUser() {
        return user;
    }

    //Set User
    public void setUser(User user) {
        this.user = user;
    }

    //handleClose
    private void handleClose() {
        if (AlertMaker.showMCAlert("Confirm"
                , "Are you sure you want to exit?"
                , Main.this)) {
            primaryStage.close();
        }
    }

    public void handleLogout() {
        if (AlertMaker.showMCAlert("Confirm logout?"
                , "Are you sure you want to Logout?"
                , Main.this)) {
            rootLayout.setLeft(null);
            initLoginLayout();
        }
    }

    private void checkPreferences() {
        Preferences preferences = Preferences.getPreferences();
        Set<String> s = DatabaseHelper.getTableNames();
        Set<String> p = preferences.getTableNames();
        if (p == null) return;
        if (p.size() == 0 || s.size() == 0) return;
        Set<String> remove = new HashSet<>();
        boolean l = true;
        for (String t : p) {
            if (!s.contains(t)) {
                remove.add(t);
                l = false;
            }
        }
        if (l) return;
        for (String r : remove) p.remove(r);
        preferences.setTableNames(p);
        Preferences.setPreference(preferences);

    }

    public boolean specialPassword(String message) {

        return AlertMaker.showSPAlert(message, Main.this);
    }

    public void snackBar(String title, String msg, String color) {
        Label header = new Label(title);
        Label body = new Label(msg);
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        if (!title.isEmpty())
            dialogLayout.setHeading(header);
        dialogLayout.setBody(body);
        StackPane pop = new StackPane(dialogLayout);
        if (color.equals("red")) color = "#ffcccc";
        else color = "#ccffcc";
        pop.setStyle("-fx-background-color:" + color + ";");
        pop.setPrefWidth(1080);
        pop.setPrefHeight(30);
        JFXSnackbar bar = new JFXSnackbar(rootLayout);

        bar.enqueue(new JFXSnackbar.SnackbarEvent(pop));
    }

    public void addSpinner() {
        rootLayout.getChildren().add(spinner);
    }

    public void removeSpinner() {
        rootLayout.getChildren().remove(spinner);
    }
}
