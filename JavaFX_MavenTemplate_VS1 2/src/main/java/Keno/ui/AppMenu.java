package Keno.ui;

import Keno.util.Popup;
import Keno.ui.scene.BuildGame;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.function.BooleanSupplier;

public final class AppMenu {
    private AppMenu() {}

    public static MenuBar create(BuildGame ui, Stage stage, BorderPane root, boolean isGameScreen) {
        return create(stage, root, isGameScreen, ui::toggleTheme, ui::isNewTheme);
    }

    public static MenuBar create(
            Stage stage,
            BorderPane root,
            boolean isGameScreen,
            Runnable toggleTheme,
            BooleanSupplier isNewTheme
    ) {
        javafx.scene.control.Menu menu = new javafx.scene.control.Menu("Menu");

        MenuItem rules = new MenuItem("Rules");
        rules.setOnAction(e -> Popup.info("KENO - How to Play", InfoBoxes.RULES));

        MenuItem odds = new MenuItem("Odds & Payouts");
        odds.setOnAction(e -> Popup.info("KENO - Odds & Payouts", InfoBoxes.ODDS));

        if (isGameScreen) {
            MenuItem theme = new MenuItem("Change Theme");
            theme.setOnAction(e -> {
                toggleTheme.run();
                Theme.applyGame(root, isNewTheme.getAsBoolean());
            });
            menu.getItems().addAll(rules, odds, theme);
        } else {
            menu.getItems().addAll(rules, odds);
        }

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> stage.close());
        menu.getItems().add(exit);

        return new MenuBar(menu);
    }
}