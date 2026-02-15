package Keno.ui;

import javafx.scene.layout.Pane;

public final class Theme {

    private Theme() {}

    public static void applyWelcome(Pane root) {
        root.setStyle(
                "-fx-background-color: radial-gradient(center 50% 50%, radius 60%, " + "#9382b6 0%, #401568 45%, #00184a 100%);" + "-fx-base: #ffffff;" + "-fx-control-inner-background: #ffffff;" + "-fx-text-fill: #0e1678;" + "-fx-font-family: 'Impact';"
        );
    }

    public static void applyGame(Pane root, boolean newTheme) {
        if (newTheme) {
            root.setStyle("-fx-background-color: #00040b;" + "-fx-base: #530e13;" + "-fx-control-inner-background: #0e0b0b;" + "-fx-text-fill: #530e13;" + "-fx-font-family: 'Mukta Malar';" + "-fx-font-size: 15px;");
        } else {
            root.setStyle("-fx-background-color: #010439;" + "-fx-base: #00184a;" + "-fx-control-inner-background: #00184a;" +  "-fx-text-fill: #021060;" +  "-fx-font-family: 'Mukta Malar';" +  "-fx-font-size: 15px;");
        }
    }

    public static String buttonStyle() {
        return "-fx-font-size: 15px; -fx-font-family: 'Impact'; -fx-font-weight: bold; -fx-background-radius: 6;";
    }
}