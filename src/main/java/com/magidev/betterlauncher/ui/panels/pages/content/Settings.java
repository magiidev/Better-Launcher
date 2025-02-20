package com.magidev.betterlauncher.ui.panels.pages.content;

import com.magidev.betterlauncher.Launcher;
import com.magidev.betterlauncher.ui.PanelManager;
import com.magidev.betterlauncher.ui.panels.pages.App;
import com.magidev.betterlauncher.ui.panels.pages.LanguagePanel;
import com.magidev.betterlauncher.utils.LanguageManager;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

public class Settings extends ContentPanel {
    private final Saver saver = Launcher.getInstance().getSaver();
    GridPane contentPane = new GridPane();

    @Override
    public String getName() {
        return "settings";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/settings.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        // Background
        this.layout.getStyleClass().add("settings-layout");
        this.layout.setPadding(new Insets(40));
        setCanTakeAllSize(this.layout);

        // Content
        contentPane.getStyleClass().add("content-pane");
        setCanTakeAllSize(contentPane);
        this.layout.getChildren().add(contentPane);

        // Titre
        Label title = new Label(LanguageManager.get("settings"));
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        title.getStyleClass().add("settings-title");
        setLeft(title);
        setCanTakeAllSize(title);
        setTop(title);
        title.setTextAlignment(TextAlignment.LEFT);
        title.setTranslateY(40d);
        title.setTranslateX(25d);
        contentPane.getChildren().add(title);

        // Bouton Languages
        var lang = new MaterialDesignIconView<>(MaterialDesignIcon.T.TRANSLATE);
        lang.setFill(Color.WHITE);

        Button languagesBtn = new Button(LanguageManager.get("languages"));
        languagesBtn.getStyleClass().add("languages-btn");
        setCanTakeAllSize(languagesBtn);
        setTop(languagesBtn);
        languagesBtn.setTranslateX(25d);
        languagesBtn.setTranslateY(100d);
        languagesBtn.setGraphic(lang);
        languagesBtn.setCursor(Cursor.HAND);
        languagesBtn.setOnMouseClicked(e -> {
            this.panelManager.showPanel(new LanguagePanel());
        });
        contentPane.getChildren().add(languagesBtn);

        // RAM
        Label ramLabel = new Label(LanguageManager.get("max-ram"));
        ramLabel.getStyleClass().add("settings-labels");
        setLeft(ramLabel);
        setCanTakeAllSize(ramLabel);
        setTop(ramLabel);
        ramLabel.setTextAlignment(TextAlignment.LEFT);
        ramLabel.setTranslateX(25d);
        ramLabel.setTranslateY(150d);
        contentPane.getChildren().add(ramLabel);

        // RAM Slider
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getStyleClass().add("ram-selector");
        for (int i = 512; i <= Math.ceil(memory.getTotal() / Math.pow(1024, 2)); i += 512) {
            comboBox.getItems().add(i / 1024.0 + " Go");
        }

        int val = 1024;
        try {
            if (saver.get("maxRam") != null) {
                val = Integer.parseInt(saver.get("maxRam"));
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException error) {
            saver.set("maxRam", String.valueOf(val));
            saver.save();
        }

        if (comboBox.getItems().contains(val / 1024.0 + " Go")) {
            comboBox.setValue(val / 1024.0 + " Go");
        } else {
            comboBox.setValue("1.0 Go");
        }

        setLeft(comboBox);
        setCanTakeAllSize(comboBox);
        setTop(comboBox);
        comboBox.setTranslateX(35d);
        comboBox.setTranslateY(180d);
        contentPane.getChildren().add(comboBox);

        // Save Button
        Button saveBtn = new Button(LanguageManager.get("save"));
        saveBtn.getStyleClass().add("save-btn");
        final var iconView = new MaterialDesignIconView<>(MaterialDesignIcon.F.FLOPPY);
        iconView.getStyleClass().add("save-icon");
        saveBtn.setGraphic(iconView);
        setCanTakeAllSize(saveBtn);
        setBottom(saveBtn);
        setCenterH(saveBtn);
        saveBtn.setOnMouseClicked(e -> {
            double _val = Double.parseDouble(comboBox.getValue().replace(" Go", ""));
            _val *= 1024;
            saver.set("maxRam", String.valueOf((int) _val));
        });
        contentPane.getChildren().add(saveBtn);
    }
}
