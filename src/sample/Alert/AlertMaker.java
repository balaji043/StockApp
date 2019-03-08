package sample.Alert;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import sample.Main;
import sample.Utils.Preferences;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class AlertMaker {

    public static void showErrorMessage(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Occurred");
        alert.setHeaderText("Error Occurred");
        alert.setContentText(ex.getLocalizedMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    //Alert Dialog Box
    public static Alert alertDialogBox(@NotNull String title, @NotNull String header
            , @NotNull String content, @NotNull Alert.AlertType s, Stage owner) {
        Alert alert = new Alert(s);
        alert.initOwner(owner);
        if (!title.isEmpty())
            alert.setTitle(title);
        if (!header.isEmpty())
            alert.setHeaderText(header);
        if (!content.isEmpty())
            alert.setContentText(content);
        return alert;
    }

    public static boolean showMCAlert(String header
            , String body, Main main) {
        JFXAlert<ButtonType> alert = new JFXAlert<>(main.getPrimaryStage());
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        alert.setAnimation(JFXAlertAnimation.BOTTOM_ANIMATION);
        alert.initOwner(main.getPrimaryStage());
        JFXButton okayButton = new JFXButton("Okay");
        okayButton.setDefaultButton(true);
        okayButton.setOnAction(addEvent -> {
            alert.setResult(ButtonType.OK);
            alert.hideWithAnimation();
        });

        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.setOnAction(closeEvent -> {
            alert.setResult(ButtonType.CANCEL);
            alert.hideWithAnimation();
        });

        dialogLayout.setHeading(new Label(header));
        dialogLayout.setBody(new Label(body));
        dialogLayout.setActions(okayButton, cancelButton);
        alert.setContent(dialogLayout);
        dialogLayout.setBody(new Label(body));
        Optional<ButtonType> optional = alert.showAndWait();

        return optional.isPresent() && optional.get().equals(ButtonType.OK);
    }

    public static boolean showSPAlert(String msg, Main main) {
        RequiredFieldValidator requiredField = new RequiredFieldValidator("* Try again");

        JFXAlert<Boolean> alert = new JFXAlert<>(main.getPrimaryStage());
        JFXDialogLayout dialogLayout = new JFXDialogLayout();


        JFXTextField password = new JFXTextField();
        JFXButton okayButton = new JFXButton("OKAY");
        okayButton.setDefaultButton(true);
        JFXButton cancelButton = new JFXButton("CANCEL");

        password.getValidators().addAll(requiredField);
        password.setPromptText("Special Password");

        StackPane pane = new StackPane();
        VBox box = new VBox();
        Label label = new Label(msg);
        box.getChildren().addAll(label, password);
        box.setSpacing(10);
        box.setPadding(new Insets(5, 5, 5, 5));
        box.setAlignment(Pos.CENTER_LEFT);
        label.setAlignment(Pos.CENTER);
        box.setFillWidth(true);
        pane.getChildren().add(box);

        okayButton.setOnAction(addEvent -> {
            if (password.getText() == null || password.getText().isEmpty()) {
                password.validate();
            } else if (!("" + password.getText().hashCode()).equals(Preferences.getPreferences().getS())) {
                okayButton.setText("Retry");
            } else {
                alert.setResult(true);
                alert.hideWithAnimation();
            }
        });
        cancelButton.setOnAction(closeEvent -> {
            alert.setResult(false);
            alert.hideWithAnimation();
        });


        dialogLayout.setActions(okayButton, cancelButton);

        dialogLayout.setHeading(new Text("Special Password Required"));
        dialogLayout.setBody(pane);
        alert.setContent(dialogLayout);
        Optional<Boolean> optional = alert.showAndWait();
        return optional.isPresent() && optional.get();
    }

}