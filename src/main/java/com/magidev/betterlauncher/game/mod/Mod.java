package com.magidev.betterlauncher.game.mod;

import com.magidev.betterlauncher.Launcher;
import com.magidev.betterlauncher.game.fetchers.CurseForgeFetcher;
import com.magidev.betterlauncher.game.instance.Instance;
import fr.flowarg.flowupdater.download.json.CurseFileInfo;
import fr.flowarg.flowupdater.integrations.curseforgeintegration.CurseForgeIntegration;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Mod
{
    private final String name;
    private final int id;
    private final String description;
    private final String author;
    private final int fileID;
    private final String iconURL;
    private int dependencyID;

    private fr.flowarg.flowupdater.download.json.Mod mod;

    private final CurseForgeFetcher cff = new CurseForgeFetcher("$2a$10$46YfrDZjVZ9AP2h2XjxZ3.d3vspD2H1mQAXvrCfjS3Zb9MfRkqQni");

    public Mod(String name, int id, String description, String author, int fileID, String iconURL, int dependencyID)
    {
        this.name = name;
        this.id = id;
        this.description = description;
        this.author = author;
        this.fileID = fileID;
        this.iconURL = iconURL;
        this.dependencyID = dependencyID;
    }

    public VBox createModView(Instance currentInstance)
    {
        VBox modBox = new VBox();
        modBox.setAlignment(Pos.TOP_LEFT);
        modBox.setPadding(new Insets(10));
        modBox.setSpacing(10);
        modBox.setBackground(new Background(new BackgroundFill(Color.rgb(193, 186, 161), new CornerRadii(5), Insets.EMPTY)));
        modBox.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT)));

        ImageView iconImageView = new ImageView();
        iconImageView.setPreserveRatio(true);
        iconImageView.setFitWidth(48);
        iconImageView.setFitHeight(48);

        try {
            Image iconImage = new Image(getIconURL(), true);
            iconImageView.setImage(iconImage);
        } catch (Exception e) {
        }

        Label nameLabel = new Label(getName());
        nameLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
        nameLabel.setStyle("-fx-text-fill: white;");

        Label authorLabel = new Label("by " + getAuthor());
        authorLabel.setFont(Font.font("Consolas", FontWeight.NORMAL, 16));
        authorLabel.setStyle("-fx-text-fill: white; -fx-background-color: rgba(255, 255, 255, 0.2); -fx-padding: 0 5 0 5;");
        authorLabel.setPadding(new Insets(2, 5, 2, 5));

        final var addButtonIcon = new MaterialDesignIconView<>(MaterialDesignIcon.P.PACKAGE_VARIANT_CLOSED_PLUS);
        addButtonIcon.setFill(Color.WHITE);

        final var addButtonIconAlready = new MaterialDesignIconView<>(MaterialDesignIcon.P.PACKAGE_VARIANT_CLOSED_CHECK);
        addButtonIconAlready.setFill(Color.WHITE);

        Button addButton = new Button("Add to " + currentInstance.getName());
        addButton.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
        addButton.setStyle("-fx-background-color: #A59D84; -fx-text-fill: white; -fx-padding: 5 10;");
        addButton.setCursor(Cursor.HAND);

        try
        {
            CurseForgeIntegration cfi = new CurseForgeIntegration(Launcher.getInstance().getLogger(), Paths.get(currentInstance.getInstanceDir().toString(), "/mods"));
            mod = cfi.fetchMod(new CurseFileInfo(getID(), getFileID()));

            if (!Files.exists(Paths.get(currentInstance.getInstanceDir().toString(), "/mods/" + mod.getName())))
            {
                addButton.setGraphic(addButtonIcon);
            }
            else
            {
                addButton.setGraphic(addButtonIconAlready);
                addButton.setText("Already added!");
                addButton.setDisable(true);
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        addButton.setOnAction(actionEvent -> {
            try
            {
                currentInstance.downloadCurseMod(mod);

                if(dependencyID != -1)
                {
                    currentInstance.downloadCurseMod(new CurseFileInfo(dependencyID, cff.getLatestModFile(dependencyID, currentInstance.getModLoader().toString(), currentInstance.getVersion())));
                }
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            }

            addButton.setText("Added!");
            addButton.setDisable(true);
        });

        HBox titleBox = new HBox(5);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.getChildren().addAll(nameLabel, authorLabel);

        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.TOP_LEFT);
        topBox.getChildren().addAll(titleBox);

        HBox.setHgrow(topBox, Priority.ALWAYS);

        HBox buttonBox = new HBox(addButton);
        buttonBox.setAlignment(Pos.TOP_RIGHT);

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(iconImageView, topBox, buttonBox);

        Label descriptionLabel = new Label(getDescription());
        descriptionLabel.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
        descriptionLabel.setStyle("-fx-text-fill: white;");
        descriptionLabel.setWrapText(true);

        var dependencyName = cff.getModNameByID(dependencyID);
        if(dependencyName != null && dependencyName.contains("Fabric API") && currentInstance.getModLoader() != ModLoader.FABRIC)
        {
            dependencyName = null;
        }

        Label dependencyLabel = new Label("Need Dependency: " + dependencyName);
        dependencyLabel.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
        dependencyLabel.setStyle("-fx-text-fill: white; -fx-background-color: rgba(255, 255, 255, 0.2); -fx-padding: 0 5 0 5;");
        dependencyLabel.setPadding(new Insets(2, 5, 2, 5));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER_LEFT);

        if (dependencyName != null)
        {
            bottomBox.getChildren().addAll(descriptionLabel, spacer, dependencyLabel);
        }
        else
        {
            bottomBox.getChildren().addAll(descriptionLabel);
        }

        modBox.getChildren().addAll(headerBox, bottomBox);

        return modBox;
    }


    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getAuthor()
    {
        return author;
    }

    public int getFileID()
    {
        return fileID;
    }

    public int getID()
    {
        return id;
    }

    public String getIconURL()
    {
        return iconURL;
    }

    public int getDependencyID()
    {
        return dependencyID;
    }
}