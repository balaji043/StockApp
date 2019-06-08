package sample.ui.root;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import sample.Main;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class RootController implements Initializable {
    @FXML
    public Label window;
    @FXML
    public VBox vBox;
    @FXML
    public BorderPane root;
    @FXML
    private JFXHamburger ham;

    private Main mainApp;


    private JFXDrawersStack stack = new JFXDrawersStack();
    private JFXDrawer drawer = new JFXDrawer();
    private HamburgerSlideCloseTransition hamTransition;
    @FXML
    public JFXButton addNewUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        drawer.setSidePane(vBox);
        drawer.setDefaultDrawerSize(200);
        drawer.setDirection(JFXDrawer.DrawerDirection.RIGHT);
        drawer.setOverLayVisible(false);
        drawer.setResizableOnDrag(false);
        hamTransition = new HamburgerSlideCloseTransition(ham);
        hamTransition.setRate(-1);
        ham.addEventFilter(MouseEvent.MOUSE_PRESSED, (event ->
                toggle()
        ));
    }

    private void toggle() {
        hamTransition.setRate(hamTransition.getRate() * -1);
        hamTransition.play();
        stack.toggle(drawer);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        if (mainApp.getUser() != null &&
                !mainApp.getUser().getAccess()
                        .equals("admin"))
            addNewUser.setDisable(true);
        setAccelerators();
    }

    private void setAccelerators() {
        HashMap<KeyCodeCombination, Runnable> keyCodeCombinationRunnableHashMap = new HashMap<>();
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.TAB
                , KeyCombination.CONTROL_DOWN), this::toggle);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.I
                , KeyCombination.CONTROL_DOWN), this::stockIn1);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.O
                , KeyCombination.CONTROL_DOWN), this::stockOut1);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.R
                , KeyCombination.CONTROL_DOWN), this::salesReturn1);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.V
                , KeyCombination.CONTROL_DOWN), this::viewStock1);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.L
                , KeyCombination.CONTROL_DOWN), this::logOut1);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.N
                , KeyCombination.CONTROL_DOWN), this::handleNeedToOrder1);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.U
                , KeyCombination.CONTROL_DOWN), this::handleAddNewUser1);
        keyCodeCombinationRunnableHashMap.put(new KeyCodeCombination(KeyCode.H
                , KeyCombination.CONTROL_DOWN), this::stockHistory1);
        mainApp.getPrimaryStage().getScene()
                .getAccelerators().putAll(keyCodeCombinationRunnableHashMap);

    }

    public void setContent(StackPane pane) {
        drawer.setContent(pane);
        root.setCenter(drawer);
    }

    @FXML
    private void stockIn() {
        window.setText("STOCK IN");
        toggle();
        mainApp.initStockInOutReturn("in");
    }

    private void stockIn1() {
        window.setText("STOCK IN");
        mainApp.initStockInOutReturn("in");
    }

    @FXML
    private void stockOut() {
        window.setText("STOCK OUT");
        toggle();
        mainApp.initStockInOutReturn("out");
    }

    private void stockOut1() {
        window.setText("STOCK OUT");
        mainApp.initStockInOutReturn("out");
    }

    @FXML
    public void viewStock() {
        window.setText("VIEW STOCKS");
        toggle();
        mainApp.initViewStocks();
    }

    private void viewStock1() {
        window.setText("VIEW STOCKS");
        mainApp.initViewStocks();
    }

    @FXML
    private void salesReturn() {
        window.setText("SALES RETURN");
        toggle();
        mainApp.initStockInOutReturn("return");
    }

    private void salesReturn1() {
        window.setText("SALES RETURN");
        mainApp.initStockInOutReturn("return");
    }

    @FXML
    private void importStocks() {
        toggle();
        mainApp.initImportStocks();
    }

    @FXML
    private void handleNeedToOrder() {
        window.setText("STOCKS NEEDED");
        toggle();
        mainApp.initNeed();
    }

    private void handleNeedToOrder1() {
        window.setText("STOCKS NEEDED");
        mainApp.initNeed();
    }

    @FXML
    private void stockHistory() {
        window.setText("STOCK HISTORY");
        toggle();
        mainApp.initStockHistory();
    }

    private void stockHistory1() {
        window.setText("STOCK HISTORY");
        mainApp.initStockHistory();
    }

    @FXML
    private void logOut() {
        toggle();
        mainApp.handleLogout();
    }

    private void logOut1() {
        mainApp.handleLogout();
    }

    @FXML
    private void handleAddNewUser() {
        window.setText("USER PANEL");
        toggle();
        mainApp.initAddNewUser();
    }

    private void handleAddNewUser1() {
        window.setText("USER PANEL");
        mainApp.initAddNewUser();
    }
}
