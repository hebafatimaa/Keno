package Keno.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class Popup {
    private Popup() {}

    public static void info(String title, String message) {
        info(title, message, 600, 650);
    }

    public static void info(String title, String message, double width, double height) {
        Alert a = new Alert(Alert.AlertType.NONE, message, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.getDialogPane().setPrefSize(width, height);
        a.showAndWait();
    }
}