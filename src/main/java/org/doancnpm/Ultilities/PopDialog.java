package org.doancnpm.Ultilities;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

public class PopDialog {
    static public void popSuccessDialog(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo!");
        alert.getDialogPane().setId("successDialog");
        alert.setHeaderText(message);
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setId("okButton"); // Set the ID for the OK button
        alert.showAndWait();
    }
    static public void popErrorDialog(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Thông báo!!!");
        alert.setHeaderText(message);
        alert.getDialogPane().setId("errorDialog");
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setId("okButton"); // Set the ID for the OK button
        alert.showAndWait();
    }
    static public void popErrorDialog(String message, String errorMessage){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Thông báo!!!");
        alert.setHeaderText(message);
        alert.setContentText("Lỗi: " + errorMessage);
        alert.getDialogPane().setId("errorDialog");
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setId("okButton");
        alert.showAndWait();
    }
}
