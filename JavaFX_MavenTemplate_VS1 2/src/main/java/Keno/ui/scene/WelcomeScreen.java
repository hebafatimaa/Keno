package Keno.ui.scene;

import Keno.ui.AppMenu;
import Keno.ui.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public final class WelcomeScreen {
    private WelcomeScreen() {}

    public static Scene create(Stage stage, Runnable onStart) {
        BorderPane root = new BorderPane();

        root.setTop(AppMenu.create(stage, root, false, () -> {}, () -> false));

        Label title = new Label("Welcome to Keno!");
        title.setStyle("-fx-font-size: 50px; -fx-font-family: 'Impact'; -fx-font-weight: bold; -fx-text-fill: #0e2157;");

        Label subtitle = new Label("Let's begin playing!");
        subtitle.setStyle("-fx-font-size: 17px; -fx-font-family: 'Impact'; -fx-text-fill: #2a195c;");

        Image logo = new Image(Objects.requireNonNull(WelcomeScreen.class.getResource("/kenoLogo.png")).toExternalForm());
        ImageView logoView = new ImageView(logo);
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(300);

        Button start = new Button("Start Playing ▶");
        start.setStyle("-fx-font-size: 15px; -fx-font-family: 'Impact'; -fx-font-weight: bold; -fx-background-radius: 6;");
        start.setOnAction(e -> onStart.run());

        VBox center = new VBox(12, logoView, title, subtitle, start);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(24));
        root.setCenter(center);

        Theme.applyWelcome(root);

        return new Scene(root);
    }
}