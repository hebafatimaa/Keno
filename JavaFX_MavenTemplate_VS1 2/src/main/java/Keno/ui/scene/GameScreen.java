package Keno.ui.scene;

import Keno.ui.AppMenu;
import Keno.ui.Theme;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.Objects;


public final class GameScreen {
    private javafx.scene.control.Button fillCardBtn, clearCardBtn;
    private javafx.scene.layout.GridPane kenoGrid;
    private final java.util.Map<Integer, javafx.scene.control.Button> numToBtn = new java.util.HashMap<>();
    private javafx.scene.control.Button startDrawingBtn, nextDrawingBtn;
    private javafx.scene.control.Label matchesLabel;
    private javafx.scene.control.TextArea drawnArea;
    private javafx.scene.control.Label totalMatchesLabel, totalWinningsLabel, sessionTotalLabel;
    private javafx.animation.SequentialTransition revealSeq;
    private boolean animating = false;
    private final BuildGame ui;
    private final Stage stage;
    private final Keno.model.GameSession session;

    public GameScreen(BuildGame ui, Stage stage) {
        this.ui = ui;
        this.stage = stage;
        this.session = ui.getSession();
    }

    public Scene create() {
        BorderPane root = new BorderPane();
        root.setTop(AppMenu.create(ui, stage, root, true));

        VBox drawingsBox = this.createOptionsGrid("1. Pick Drawings", new int[]{1,2,3,4}, new ToggleGroup(), true);
        VBox spotsBox    = this.createOptionsGrid("2. Pick Spots",    new int[]{1,4,8,10}, new ToggleGroup(), false);

        Label drawNumberLabel = new Label("Drawing Round: ");
        ui.wireDrawNumberLabel(drawNumberLabel);
        HBox drawInfo = new HBox(drawNumberLabel);
        drawInfo.setAlignment(Pos.CENTER_LEFT);

        VBox controlsBox = this.createDrawingControls();

        VBox leftColumn = new VBox(30, drawingsBox, spotsBox, drawInfo, controlsBox);
        leftColumn.setAlignment(Pos.TOP_LEFT);
        VBox.setMargin(drawInfo, new Insets(30, 0, 0, 0));

        VBox betCard = this.createBetCard("3. Bet Card");

        HBox row = new HBox(100, leftColumn, betCard);
        row.setPadding(new Insets(16));
        row.setAlignment(Pos.TOP_CENTER);
        root.setCenter(row);

        Button toWelcome = new javafx.scene.control.Button("◀ Back to Welcome");
        toWelcome.setStyle(Theme.buttonStyle());
        toWelcome.setOnAction(e -> ui.showWelcome());

        Button newBetBtn = new Button("Start New Bet ▶");
        newBetBtn.setStyle(Theme.buttonStyle());
        newBetBtn.setOnAction(e -> ui.startNewBet());

        HBox bottom = new HBox();
        bottom.setPadding(new Insets(10, 30, 15, 30));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottom.getChildren().addAll(toWelcome, spacer, newBetBtn);
        root.setBottom(bottom);

        Theme.applyGame(root, ui.isNewTheme());
        ui.updateDrawNumber();

        Image balls = new Image(Objects.requireNonNull(GameScreen.class.getResource("/kenoBalls.png")).toExternalForm());
        ImageView ballView = new ImageView(balls);
        ballView.setPreserveRatio(true);
        ballView.setFitHeight(160);

        AnchorPane overlay = new AnchorPane(ballView);
        ballView.setLayoutX(220);
        ballView.setLayoutY(600);
        overlay.setMouseTransparent(true);

        StackPane layers = new StackPane(root, overlay);

        return new Scene(layers);
    }

    private VBox createOptionsGrid(String titleText, int[] options, ToggleGroup group, boolean isDrawings) {
        Label title = new Label(titleText);

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        for (int i = 0; i < options.length; i++) {
            int value = options[i];
            ToggleButton b = new ToggleButton(String.valueOf(value));
            b.setToggleGroup(group);
            b.setPrefSize(50, 34);
            grid.add(b, i % 4, i / 4);
            b.setOnAction(e -> {
                if (b.isSelected()) {
                    if (isDrawings) {
                        session.setDrawingsChoice(value);
                    } else {
                        session.setSpotsChoice(value);
                    }
                } else {
                    if (isDrawings) {
                        session.setDrawingsChoice(0);
                    } else {
                        session.setSpotsChoice(0);
                    }
                }
                maybeEnableBetCard();
            });
        }

        Button confirm = new Button("Confirm");
        confirm.setOnAction(e -> {
            if (isDrawings) {
                session.lockDrawings();
            } else {
                session.lockSpots();
            }
            if ((isDrawings && session.isDrawingsLocked()) ||
                    (!isDrawings && session.isSpotsLocked())) {
                grid.setDisable(true);
                confirm.setDisable(true);
            }
            maybeEnableBetCard();
        });

        VBox box = new VBox(8, title, grid, confirm);
        box.setAlignment(Pos.TOP_LEFT);
        return box;
    }

    private void setKenoGridEnabled(boolean on) {
        if (kenoGrid == null) return;
        for (javafx.scene.Node n : kenoGrid.getChildren()) {
            n.setDisable(!on);
        }
    }

    private void maybeEnableBetCard() {
        boolean gridReady= session.isDrawingsLocked() && session.isSpotsLocked();
        boolean picksComplete = (session.getSpotsChoice() > 0 && session.getPicks().size() == session.getSpotsChoice());
        boolean beforeAnyDraw = (session.getCurrentDrawing() == 0);

        setKenoGridEnabled(gridReady && beforeAnyDraw);

        if (fillCardBtn != null)  {
            fillCardBtn.setDisable(!(gridReady && beforeAnyDraw));
        }
        if (clearCardBtn != null) {
            clearCardBtn.setDisable(!(beforeAnyDraw && !session.getPicks().isEmpty()));
        }
        if (startDrawingBtn != null) {
            startDrawingBtn.setDisable(!(gridReady && picksComplete && beforeAnyDraw) || animating);
        }

        if (nextDrawingBtn != null) {
            boolean hasStarted = (session.getCurrentDrawing() > 0);
            boolean moreLeft   = session.hasMore();
            nextDrawingBtn.setDisable(!(hasStarted && moreLeft) || animating);
        }
    }

    private VBox createBetCard(String titleText) {
        Label title = new Label(titleText);
        kenoGrid = buildKenoGrid();
        setKenoGridEnabled(false);

        fillCardBtn  = new Button("Fill Card");
        clearCardBtn = new Button("Clear Card");
        fillCardBtn.setDisable(true);
        fillCardBtn.setOnAction(e -> quickPick());
        clearCardBtn.setDisable(true);
        clearCardBtn.setOnAction(e -> resetCard());
        maybeEnableBetCard();

        HBox actions = new HBox(30, fillCardBtn, clearCardBtn);
        actions.setAlignment(Pos.BOTTOM_RIGHT);

        double cardWidth = 450;
        drawnArea= new javafx.scene.control.TextArea("Drawn:");
        drawnArea.setEditable(false);
        drawnArea.setWrapText(true);
        drawnArea.setFocusTraversable(false);
        drawnArea.setPrefWidth(cardWidth);
        drawnArea.setMaxWidth(cardWidth);
        drawnArea.setPrefRowCount(3);
        drawnArea.setMinHeight(Region.USE_PREF_SIZE);

        matchesLabel = new Label("Matched (0):      []");
        matchesLabel.setWrapText(true);

        totalMatchesLabel = new Label("Total matched (0):        []");
        totalMatchesLabel.setWrapText(true);

        totalWinningsLabel = new Label("Amount Won: $" + session.getTotalWinnings());
        sessionTotalLabel  = new Label("Total Payout: $" + session.getSessionTotal());

        return new VBox(8, title, kenoGrid, actions, drawnArea, matchesLabel, totalMatchesLabel, totalWinningsLabel, sessionTotalLabel);
    }

    private VBox createDrawingControls() {
        startDrawingBtn = new Button("Start Drawing");
        nextDrawingBtn  = new Button("Next Drawing");
        startDrawingBtn.setStyle(Theme.buttonStyle());
        nextDrawingBtn.setStyle(Theme.buttonStyle());
        startDrawingBtn.setDisable(true);
        nextDrawingBtn.setDisable(true);
        startDrawingBtn.setOnAction(e -> startDrawing());
        nextDrawingBtn.setOnAction(e -> nextDrawing());

        VBox box = new VBox(20, startDrawingBtn, nextDrawingBtn);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private GridPane buildKenoGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(6); grid.setVgap(6); grid.setPadding(new Insets(10));
        int n = 1;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 8; col++) {
                final int num = n++;
                Button b = new Button(String.valueOf(num));
                b.setDisable(true);
                b.setPrefWidth(48); b.setPrefHeight(36);
                b.setOnAction(e -> togglePick(num));
                numToBtn.put(num, b);
                grid.add(b, col, row);
            }
        }
        return grid;
    }

    private void togglePick(int num) {
        Button b = numToBtn.get(num);
        if (b == null || b.isDisabled()) return;
        if (session.getPicks().contains(num)) {
            session.removePick(num);
            b.setStyle("");
        } else {
            if (session.isSpotsLocked() && session.getSpotsChoice() > 0
                    && session.getPicks().size() >= session.getSpotsChoice()) {
                return;
            }
            session.addPick(num);
            b.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-font-weight: bold;");
        }
        if (clearCardBtn != null) clearCardBtn.setDisable(session.getPicks().isEmpty());
        maybeEnableBetCard();
    }

    private void resetCard() {
        if (revealSeq != null) {
            revealSeq.stop();
            revealSeq = null;
        }
        animating = false;
        session.resetCardState();
        for (java.util.Map.Entry<Integer, Button> e : numToBtn.entrySet()) {
            e.getValue().setStyle("");
        }
        if (drawnArea != null) {
            drawnArea.setText("Drawn:");
        }
        if (matchesLabel != null) {
            matchesLabel.setText("Matched (0):        []");
        }
        if (totalMatchesLabel != null) {
            totalMatchesLabel.setText("Total matched (0):     []");
        }
        if (totalWinningsLabel != null) {
            totalWinningsLabel.setText("Amount Won: $0");
        }
        if (clearCardBtn != null) {
            clearCardBtn.setDisable(true);
        }
        ui.updateDrawNumber();
        maybeEnableBetCard();
    }

    private void quickPick() {
        if (kenoGrid == null || !session.isDrawingsLocked() || !session.isSpotsLocked() || session.getCurrentDrawing() != 0 || session.getSpotsChoice() <= 0) {
            return;
        }
        for (Integer n : new java.util.HashSet<>(session.getPicks())) {
            Button b = numToBtn.get(n);
            if (b != null) {
                b.setStyle("");
            }
        }
        session.clearPicks();
        java.util.List<Integer> pool = new java.util.ArrayList<>();
        for (int i = 1; i <= 80; i++) {
            pool.add(i);
        }
        java.util.Collections.shuffle(pool);
        for (int i = 0; i < session.getSpotsChoice(); i++) {
            int num = pool.get(i);
            session.addPick(num);
            Button b = numToBtn.get(num);
            if (b != null) {
                b.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-font-weight: bold;");
            }
        }
        if (clearCardBtn != null) clearCardBtn.setDisable(false);
        maybeEnableBetCard();
    }

    private void startDrawing() {
        if (!(session.isDrawingsLocked() && session.isSpotsLocked())) return;
        if (session.getCurrentDrawing() == 0) {
            session.startIfFirst();
            ui.updateDrawNumber();
            revealCurrentDrawing();
        }
    }

    private void nextDrawing() {
        if (session.getCurrentDrawing() > 0 && session.hasMore()) {
            session.next();
            ui.updateDrawNumber();
            revealCurrentDrawing();
        }
    }

    private void revealCurrentDrawing() {
        java.util.List<Integer> d = Keno.game.PrizePool.draw20();

        drawnArea.setText("Draw " + session.getCurrentDrawing() + " of " + session.getDrawingsChoice() + ":");
        matchesLabel.setText("Matched (0):     []");
        animating = true;
        maybeEnableBetCard();

        revealSeq = new SequentialTransition();
        final StringBuilder liveLine = new StringBuilder();

        for (int i = 0; i < d.size(); i++) {
            final int idx = i;
            javafx.animation.PauseTransition p = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.00));
            p.setOnFinished(ev -> {
                if (idx > 0 && idx != 10) liveLine.append("   ");
                liveLine.append(d.get(idx));
                if (idx == 9) liveLine.append("\n");
                drawnArea.setText("Draw " + session.getCurrentDrawing() + " of " + session.getDrawingsChoice() + ":\n" + liveLine);
            });
            revealSeq.getChildren().add(p);
        }

        revealSeq.setOnFinished(ev -> {
            java.util.List<Integer> matches = new java.util.ArrayList<>();
            for (Integer p : session.getPicks()) if (d.contains(p)) matches.add(p);
            java.util.Collections.sort(matches);

            matchesLabel.setText("Matched (" + matches.size() + "):     " + matches);

            session.addTotalMatches(matches);
            session.updateTotals(matches.size());

            totalMatchesLabel.setText("Total matched (" + session.getTotalMatches().size() + "): " + session.getTotalMatches());
            totalWinningsLabel.setText("Amount Won: $" + session.getTotalWinnings());
            sessionTotalLabel.setText("Total Payout: $" + session.getSessionTotal());

            animating = false;
            setKenoGridEnabled(false);
            maybeEnableBetCard();
        });

        revealSeq.play();
    }
}