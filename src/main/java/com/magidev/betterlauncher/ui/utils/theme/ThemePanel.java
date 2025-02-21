package com.magidev.betterlauncher.ui.utils.theme;

import com.magidev.betterlauncher.Launcher;
import com.magidev.betterlauncher.ui.PanelManager;
import com.magidev.betterlauncher.ui.panel.Panel;
import com.magidev.betterlauncher.ui.panels.pages.App;
import com.magidev.betterlauncher.ui.panels.pages.Login;
import com.magidev.betterlauncher.ui.panels.partials.TopBar;
import com.magidev.betterlauncher.ui.utils.lang.LanguageManager;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ThemePanel extends Panel {

    private final Saver saver = Launcher.getInstance().getSaver();

    @Override
    public String getName() {
        return "Theme";
    }

    @Override
    public String getStylesheetPath() {
        return "css/" + ThemeManager.getCurrentTheme().getName().toLowerCase() + "/theme.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        this.layout.getStyleClass().add("language-layout");

        // Background grid setup
        GridPane bgImage = new GridPane();
        GridPane.setVgrow(bgImage, Priority.ALWAYS);
        GridPane.setHgrow(bgImage, Priority.ALWAYS);
        bgImage.getStyleClass().add("bg-image");
        this.layout.add(bgImage, 0, 0);

        // Theme selection panel
        GridPane theme = new GridPane();
        theme.setMaxWidth(600);
        theme.setMinWidth(400);
        theme.setMaxHeight(400);
        theme.setMinHeight(300);
        setCanTakeAllSize(theme);
        setAlignement(theme, VPos.CENTER, HPos.CENTER);
        theme.getStyleClass().add("theme-panel");

        Label chooseTheme = new Label("Choose your theme");
        setCanTakeAllSize(chooseTheme);
        setAlignement(chooseTheme, VPos.TOP, HPos.CENTER);
        chooseTheme.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 24px;");
        chooseTheme.setTranslateY(15);

        Separator separator = new Separator();
        setCanTakeAllSize(separator);
        setAlignement(separator, VPos.TOP, HPos.CENTER);
        separator.setTranslateY(65);

        // Add buttons for each theme
        int row = 2;
        for (String themeName : ThemeManager.getAvailableThemes()) {
            theme.add(createThemeButton(themeName), 0, row++);
        }

        theme.getChildren().addAll(chooseTheme, separator);
        this.layout.getChildren().add(theme);
    }

    private Button createThemeButton(String themeName) {
        Button themeButton = new Button(themeName);

        themeButton.setOnAction(event -> {
            ThemeManager.setCurrentTheme(themeName);

            saver.set("theme", themeName);
            saver.save();

            switch (ThemeManager.getCurrentTheme().getName())
            {
                case "Coffee" -> panelManager.getTopBar().setTopBarStyle("-fx-background-color: #A59D84;");
                case "Ocean" -> panelManager.getTopBar().setTopBarStyle("-fx-background-color: #08519c;");
            }

            System.out.println("Selected theme: " + themeName);

            if(Launcher.getInstance().isUserAlreadyLoggedIn())
            {
                this.panelManager.showPanel(new App());
            }
            else
            {
                this.panelManager.showPanel(new Login());
            }
        });

        setCanTakeAllSize(themeButton);
        setAlignement(themeButton, VPos.CENTER, HPos.CENTER);
        themeButton.getStyleClass().add("theme-button");
        return themeButton;
    }
}
