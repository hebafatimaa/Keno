package Keno.ui.scene;

import Keno.model.GameSession;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class BuildGame extends Application {

    private final GameSession session = new GameSession();

    private Scene welcomeScene, gameScene;
    private boolean newTheme = false;
    private Label drawNumberLabel;
    private Stage primaryStage;
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("KENO");
        welcomeScene = WelcomeScreen.create(stage, () -> stage.setScene(gameScene));
        gameScene = new GameScreen(this, stage).create();
        stage.setScene(welcomeScene);
        stage.setWidth(1200f);
        stage.setHeight(900);
        stage.show();
    }

    void updateDrawNumber() {
        drawNumberLabel.setText("Drawing Round: " + (session.getCurrentDrawing() > 0 ? String.valueOf(session.getCurrentDrawing()) : "0"));
    }

    private void newBet() {
        session.newBet();
        gameScene = new GameScreen(this, primaryStage).create();
        primaryStage.setScene(gameScene);
    }

    public void wireDrawNumberLabel(Label label) {
        this.drawNumberLabel = label;
    }

    public void showWelcome() {
        if (primaryStage != null && welcomeScene != null) {
            primaryStage.setScene(welcomeScene);
        }
    }

    public boolean isNewTheme() {
        return newTheme;
    }

    public void toggleTheme() {
        newTheme = !newTheme;
    }

    public Keno.model.GameSession getSession() {
        return session;
    }

    public void startNewBet() {
        newBet();
    }


}

