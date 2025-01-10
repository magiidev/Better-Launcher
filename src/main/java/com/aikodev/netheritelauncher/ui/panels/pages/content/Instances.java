package com.aikodev.netheritelauncher.ui.panels.pages.content;

import com.aikodev.netheritelauncher.Launcher;
import com.aikodev.netheritelauncher.game.mod.ModLoader;
import com.aikodev.netheritelauncher.game.fetchers.VersionFetcher;
import com.aikodev.netheritelauncher.game.instance.Instance;
import com.aikodev.netheritelauncher.game.instance.InstanceManager;
import com.aikodev.netheritelauncher.ui.PanelManager;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.*;
import java.util.List;

public class Instances extends ContentPanel
{
    private final Saver saver = Launcher.getInstance().getSaver();
    private final GridPane contentPane = new GridPane();
    private VBox instancesBox;

    @Override
    public String getName()
    {
        return "Instances";
    }

    @Override
    public String getStylesheetPath()
    {
        return "css/content/instances.css";
    }

    @Override
    public void init(PanelManager panelManager)
    {
        super.init(panelManager);
        this.layout.getStyleClass().add("instances-layout");
        this.layout.setPadding(new Insets(40));
        setCanTakeAllSize(this.layout);

        InstanceManager.getInstance();

        VBox mainContent = new VBox();
        mainContent.setSpacing(20);
        mainContent.setAlignment(Pos.TOP_LEFT);
        setCanTakeAllSize(mainContent);
        this.layout.getChildren().add(mainContent);

        Label title = new Label("Instances");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        title.getStyleClass().add("instances-title");
        title.setTextAlignment(TextAlignment.LEFT);
        mainContent.getChildren().add(title);

        Button createInstanceBtn = new Button("Create a new instance");
        createInstanceBtn.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        final var createInstanceIcon = new MaterialDesignIconView<>(MaterialDesignIcon.A.ARCHIVE_PLUS);
        createInstanceIcon.setFill(Color.WHITE);
        createInstanceBtn.getStyleClass().add("instance-btn");
        setCanTakeAllSize(createInstanceBtn);
        createInstanceBtn.setGraphic(createInstanceIcon);
        createInstanceBtn.setCursor(Cursor.HAND);
        createInstanceBtn.setOnAction(event -> handleCreateInstanceButtonClick());
        mainContent.getChildren().add(createInstanceBtn);

        this.instancesBox = new VBox(10);
        this.instancesBox.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(this.instancesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.getStyleClass().add("scroll-pane");

        mainContent.getChildren().add(scrollPane);
        reloadInstances();
    }

    private VBox createInstanceView(Instance instance)
    {
        VBox instanceBox = new VBox();
        instanceBox.setAlignment(Pos.TOP_LEFT);
        instanceBox.setPadding(new Insets(10));
        instanceBox.setSpacing(5);
        instanceBox.setBackground(new Background(new BackgroundFill(Color.rgb(193, 186, 161), new CornerRadii(5), Insets.EMPTY)));
        instanceBox.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT)));

        Label nameLabel = new Label(instance.getName());
        nameLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 20));
        nameLabel.setStyle("-fx-text-fill: white;");

        Label versionLabel = new Label("Version : " + instance.getVersion());
        versionLabel.setFont(Font.font("Consolas", FontWeight.NORMAL, 20));
        versionLabel.setStyle("-fx-text-fill: white;");

        Label modLoaderLabel = new Label("ModLoader : " + instance.getModLoader());
        modLoaderLabel.setFont(Font.font("Consolas", FontWeight.NORMAL, 20));
        modLoaderLabel.setStyle("-fx-text-fill: white;");

        ImageView modLoaderImageView = new ImageView();
        modLoaderImageView.setPreserveRatio(true);

        VBox.setVgrow(modLoaderImageView, Priority.ALWAYS);
        modLoaderImageView.setFitWidth(48);
        modLoaderImageView.setFitHeight(Double.MAX_VALUE);

       /* switch (instance.getModLoader())
        {
            case FORGE -> modLoaderImageView.setImage(new Image("images/icons/forge.png"));
            case FABRIC -> modLoaderImageView.setImage(new Image("images/icons/fabric.png"));
            case VANILLA -> modLoaderImageView.setImage(new Image("images/icons/vanilla.png"));
            case NEOFORGE -> modLoaderImageView.setImage(new Image("images/icons/neoforge.png"));
            case SODIUM -> modLoaderImageView.setImage(new Image("images/icons/sodium.png"));
            default -> System.err.println("ModLoader non reconnu : " + instance.getModLoader());
        }*/

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        final var deleteBtnIcon = new MaterialDesignIconView<>(MaterialDesignIcon.A.ARCHIVE_REMOVE);
        deleteBtnIcon.setFill(Color.WHITE);

        final var modsBtnIcon = new MaterialDesignIconView<>(MaterialDesignIcon.P.PACKAGE);
        modsBtnIcon.setFill(Color.WHITE);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        deleteBtn.getStyleClass().add("instance-btn");
        deleteBtn.setGraphic(deleteBtnIcon);
        deleteBtn.setCursor(Cursor.HAND);

        deleteBtn.setOnAction(event -> handleDeleteButtonClick(instance));

        if (instance.getModLoader().equals(ModLoader.FORGE) ||
                instance.getModLoader().equals(ModLoader.NEOFORGE) ||
                instance.getModLoader().equals(ModLoader.FABRIC))
        {

            Button modsBtn = new Button("Mods");
            modsBtn.setFont(Font.font("Consolas", FontWeight.NORMAL, 12));
            modsBtn.getStyleClass().add("instance-btn");
            modsBtn.setCursor(Cursor.HAND);
            modsBtn.setGraphic(modsBtnIcon);
            modsBtn.setOnAction(event ->
            {
                try
                {
                    handleModsButtonClick(instance);
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            });
            buttonBox.getChildren().add(modsBtn);
        }

        buttonBox.getChildren().add(deleteBtn);

        instanceBox.getChildren().addAll(nameLabel, versionLabel, modLoaderLabel, buttonBox);

      //  HBox imageBox = new HBox();
     //   imageBox.setAlignment(Pos.TOP_RIGHT);
      //  imageBox.getChildren().add(modLoaderImageView);

     //   instanceBox.getChildren().add(imageBox);

        return instanceBox;
    }

    private void handleModsButtonClick(Instance instance) throws IOException
    {
        try
        {
            File modsFolder = new File(instance.getInstanceDir() + "/mods");

            Desktop.getDesktop().open(modsFolder);
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, "Cannot open the mod folder because the instance has not been launched at least once.").showAndWait();
        }
    }

    private void handleDeleteButtonClick(Instance instance)
    {
        boolean removed = InstanceManager.removeInstance(instance);

        if (removed)
        {
            System.out.println("Instance removed: " + instance.getName());

            reloadInstances();
        } else {
            System.err.println("Failed to remove instance: " + instance.getName());
        }
    }

    private void reloadInstances()
    {
        if (this.instancesBox != null)
        {
            this.instancesBox.getChildren().clear();

            for (Instance instance : InstanceManager.getInstances())
            {
                this.instancesBox.getChildren().add(createInstanceView(instance));
            }

            System.out.println("Instances reloaded.");
        } else {
            System.err.println("instancesBox is null.");
        }
    }

    private void handleCreateInstanceButtonClick() {
        // Créer une nouvelle fenêtre principale (Stage)
        Stage newInstanceWindow = new Stage();
        newInstanceWindow.initStyle(StageStyle.TRANSPARENT);
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Champ de texte pour le nom de l'instance
        TextField nameField = new TextField();
        nameField.setPromptText("Enter instance name");
        nameField.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        nameField.setStyle("-fx-text-fill: black;");
        nameField.getStyleClass().add("text-field");

        // ComboBox pour choisir la version
        ComboBox<String> versionChoiceBox = new ComboBox<>();
        List<String> versions = VersionFetcher.fetchVersions();
        versionChoiceBox.getItems().addAll(versions);
        versionChoiceBox.setValue(versions.getFirst());
        versionChoiceBox.getStyleClass().add("combo-box");

        // ChoiceBox pour choisir le mod loader
        ComboBox<ModLoader> modLoaderChoiceBox = new ComboBox<>();
        modLoaderChoiceBox.getItems().addAll(ModLoader.VANILLA, ModLoader.FORGE, ModLoader.NEOFORGE, ModLoader.FABRIC, ModLoader.SODIUM);
        modLoaderChoiceBox.setValue(ModLoader.VANILLA);
        modLoaderChoiceBox.getStyleClass().add("combo-box");

        // Bouton pour créer l'instance
        Button createButton = new Button("Create");
        createButton.setDefaultButton(true);
        createButton.getStyleClass().add("create-btn");

        // Ajouter les composants à la mise en page
        layout.getChildren().addAll(
                new Label("Instance Name:"), nameField,
                new Label("Version:"), versionChoiceBox,
                new Label("ModLoader:"), modLoaderChoiceBox,
                createButton
        );

        // Créer la scène pour la nouvelle fenêtre
        Scene newInstanceScene = new Scene(layout);
        newInstanceScene.getStylesheets().add("css/content/instances.css"); // Assurez-vous que le chemin vers le CSS est correct
        newInstanceScene.setFill(Color.TRANSPARENT);
        newInstanceWindow.setScene(newInstanceScene);
        newInstanceWindow.setTitle("Create a new Instance");
        newInstanceWindow.setResizable(false);

        // Action lorsque le bouton "Create" est cliqué
        createButton.setOnAction(event -> {
            String instanceName = nameField.getText();
            String version = versionChoiceBox.getValue();
            ModLoader modLoader = modLoaderChoiceBox.getValue();

            if (instanceName == null || instanceName.trim().isEmpty() || InstanceManager.getInstanceByName(instanceName) != null) {
                new Alert(Alert.AlertType.ERROR, "Instance name cannot be empty or already exists!").showAndWait();
                return;
            }

            Instance newInstance = new Instance(instanceName, version, modLoader);
            InstanceManager.addInstance(newInstance);
            System.out.println("Instance created: " + instanceName);

            reloadInstances();  // Recharger la liste des instances
            newInstanceWindow.close();  // Fermer la nouvelle fenêtre
        });

        // Afficher la nouvelle fenêtre
        newInstanceWindow.show();
    }


}
