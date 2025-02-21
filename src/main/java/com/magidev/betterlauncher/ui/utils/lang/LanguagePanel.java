package com.magidev.betterlauncher.ui.utils.lang;

import com.magidev.betterlauncher.Launcher;
import com.magidev.betterlauncher.Main;
import com.magidev.betterlauncher.ui.PanelManager;
import com.magidev.betterlauncher.ui.panel.Panel;
import com.magidev.betterlauncher.ui.panels.pages.App;
import com.magidev.betterlauncher.ui.panels.pages.Login;
import com.magidev.betterlauncher.ui.utils.theme.ThemeManager;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class LanguagePanel extends Panel {

    private final Saver saver = Launcher.getInstance().getSaver();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/" + ThemeManager.getCurrentTheme().getName().toLowerCase() + "/language.css";
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

        // Flag Panel Setup
        GridPane flag = new GridPane();
        flag.setMaxWidth(600);
        flag.setMinWidth(400);
        flag.setMaxHeight(400);
        flag.setMinHeight(300);
        setCanTakeAllSize(flag);
        setAlignement(flag, VPos.CENTER, HPos.CENTER);
        flag.getStyleClass().add("flag-panel");

        // Choose Language Label
        Label chooseLanguage = new Label(LanguageManager.get("choose-your-language"));
        setCanTakeAllSize(chooseLanguage);
        setAlignement(chooseLanguage, VPos.TOP, HPos.CENTER);
        chooseLanguage.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 24px;");
        chooseLanguage.setTranslateY(15);  // Optionnel, pour décaler le label

        Separator separator = new Separator();
        setCanTakeAllSize(separator);
        setAlignement(separator, VPos.TOP, HPos.CENTER);
        separator.setTranslateY(65);

        flag.getChildren().addAll(chooseLanguage, separator);

        // FlowPane to hold all the flag buttons
        FlowPane languageList = new FlowPane();
        languageList.setVgap(10);  // Vertical gap between buttons
        languageList.setHgap(10);  // Horizontal gap between buttons
        languageList.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Add flags/buttons for all available languages in LanguageManager
        for (String languageName : LanguageManager.getAvailableLanguages()) {
            Button flagButton = createFlagButton(languageName);
            flagButton.setCursor(Cursor.HAND);
            languageList.getChildren().add(flagButton);
        }

        // ScrollPane for responsiveness
        ScrollPane scrollPane = new ScrollPane(languageList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.getStyleClass().add("scroll-pane");

        // Descendre le ScrollPane pour le séparer du label
        scrollPane.setTranslateY(60);  // Décale le ScrollPane de 30px vers le bas (ajuste cette valeur selon ton besoin)

        // Ajouter le ScrollPane au layout
        flag.getChildren().add(scrollPane);
        this.layout.getChildren().add(flag);
    }

    private Button createFlagButton(String language) {
        // Ensure the image resource is loaded correctly
        String imagePath = "/images/flags/" + language.toLowerCase() + ".png";
        ImageView imageView = new ImageView(Main.class.getResource(imagePath).toExternalForm());

        imageView.setFitWidth(120);
        imageView.setFitHeight(120);

        Button button = new Button("");
        button.setGraphic(imageView);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        button.setOnAction(e -> {
            LanguageManager.setLanguage(language);

            saver.set("lang", language);
            saver.save();

            System.out.println("Selected Language: " + language);
            System.out.println(LanguageManager.get("greeting"));

            if(Launcher.getInstance().isUserAlreadyLoggedIn())
            {
                this.panelManager.showPanel(new App());
            }
            else
            {
                this.panelManager.showPanel(new Login());
            }
        });

        return button;
    }
}