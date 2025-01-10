package com.aikodev.netheritelauncher.ui.panels.pages.content;

import com.aikodev.netheritelauncher.game.mod.Mod;
import com.aikodev.netheritelauncher.game.instance.Instance;
import com.aikodev.netheritelauncher.game.instance.InstanceManager;
import com.aikodev.netheritelauncher.game.fetchers.CurseForgeFetcher;
import com.aikodev.netheritelauncher.game.mod.ModLoader;
import com.aikodev.netheritelauncher.ui.PanelManager;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mods extends ContentPanel
{
    ComboBox<String> instancesBox;

    Instance currentInstance = null;

    private VBox modsBox;

    private CurseForgeFetcher curseForgeFetcher;

    private int currentIndex = 0;
    private final int pageSize = 20;
    private boolean isLoading = false;

    private Label loadingIndicator;
    private ProgressIndicator centralLoadingIndicator;


    public Mods()
    {
        curseForgeFetcher = new CurseForgeFetcher("$2a$10$46YfrDZjVZ9AP2h2XjxZ3.d3vspD2H1mQAXvrCfjS3Zb9MfRkqQni");
    }

    @Override
    public String getName()
    {
        return "Mod Manager";
    }

    @Override
    public String getStylesheetPath()
    {
        return "css/content/mods.css";
    }

    @Override
    public void init(PanelManager panelManager)
    {
        super.init(panelManager);

        this.layout.getStyleClass().add("mods-layout");
        this.layout.setPadding(new Insets(40));
        setCanTakeAllSize(this.layout);

        VBox mainContent = new VBox();
        mainContent.setSpacing(20);
        mainContent.setAlignment(Pos.TOP_LEFT);
        setCanTakeAllSize(mainContent);

        // Conteneur pour la superposition
        StackPane mainContainer = new StackPane();
        mainContainer.getChildren().add(mainContent);
        this.layout.getChildren().add(mainContainer);

        // Cercle de chargement central
        centralLoadingIndicator = new ProgressIndicator();
        centralLoadingIndicator.setMaxSize(100, 100);
        centralLoadingIndicator.setVisible(false); // Caché par défaut
        mainContainer.getChildren().add(centralLoadingIndicator);
        StackPane.setAlignment(centralLoadingIndicator, Pos.CENTER);

        Label title = new Label("Mods");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        title.getStyleClass().add("instances-title");
        title.setTextAlignment(TextAlignment.LEFT);
        mainContent.getChildren().add(title);

        // Create HBox for ComboBox and SearchField
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        instancesBox = new ComboBox<>();
        instancesBox.setPromptText("Select an instance");
        instancesBox.getStyleClass().add("instance-box");
        setCanTakeAllSize(instancesBox);

        TextField searchField = new TextField();
        searchField.setPromptText("Search mods...");
        searchField.getStyleClass().add("search-box");
        searchField.setPrefWidth(250);
        setCanTakeAllSize(searchField);
        searchField.setOnAction(event -> {
            String searchQuery = searchField.getText();
            System.out.println(searchQuery);
            if (!searchQuery.isEmpty()) {
                searchMods(searchQuery);
            } else {
                reloadMods();
            }
        });

        topRow.getChildren().addAll(instancesBox, searchField);
        mainContent.getChildren().add(topRow);

        List<ModLoader> modLoaders = Arrays.asList(ModLoader.FORGE, ModLoader.NEOFORGE, ModLoader.FABRIC);
        List<String> instanceNames = new ArrayList<>();

        for (Instance instance : InstanceManager.getInstances()) {
            if (modLoaders.contains(instance.getModLoader())) {
                instanceNames.add(instance.getName());
            }
        }

        instancesBox.getItems().addAll(instanceNames);

        this.modsBox = new VBox(10);
        this.modsBox.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(this.modsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0 && !isLoading) {
                loadMoreMods();
            }
        });
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.getStyleClass().add("scroll-pane");

        mainContent.getChildren().add(scrollPane);

        instancesBox.setOnAction(event -> {
            String selectedInstanceName = instancesBox.getValue();

            if (selectedInstanceName != null)
            {
                currentInstance = InstanceManager.getInstanceByName(selectedInstanceName);

                if (currentInstance != null)
                {
                    showCentralLoading(true); // Afficher le cercle de chargement
                    reloadMods();
                    showCentralLoading(false); // Masquer le cercle après le rechargement

                    System.out.println("Selected instance: " + currentInstance.getName());
                } else {
                    System.out.println("No instance found for the selected name.");
                }
            }
        });

        this.loadingIndicator = new Label("Loading mods...");
        this.loadingIndicator.setFont(Font.font("Consolas", FontWeight.NORMAL, FontPosture.ITALIC, 14));
        this.loadingIndicator.setTextFill(Color.GRAY);
        this.loadingIndicator.setVisible(false);

        this.modsBox.getChildren().add(loadingIndicator);
    }


    private void reloadMods() {
        if (this.modsBox != null) {
            showCentralLoading(true); // Affiche le cercle de chargement
            this.modsBox.getChildren().clear();
            currentIndex = 0;
            loadMoreMods();
            showCentralLoading(false); // Masque le cercle de chargement
            System.out.println("Instances reloaded.");
        } else {
            System.err.println("modsBox is null.");
        }
    }

    private void loadMoreMods()
    {
        if (currentInstance == null) return;

        isLoading = true;
        loadingIndicator.setVisible(true);

        List<Mod> mods = curseForgeFetcher.getModsByLoaderAndVersion(
                currentInstance.getModLoader().toString(),
                currentInstance.getVersion(),
                currentIndex,
                pageSize
        );

        if (!mods.isEmpty()) {
            mods.forEach(mod -> this.modsBox.getChildren().add(mod.createModView(currentInstance)));
            currentIndex += mods.size();
        }

        loadingIndicator.setVisible(false); // Masquer le spinner
        isLoading = false;
    }

    private void searchMods(String searchQuery) {
        if (currentInstance == null) return;

        isLoading = true;
        loadingIndicator.setVisible(true);

        // Modifier l'appel à CurseForgeFetcher pour passer la recherche en paramètre
        List<Mod> mods = curseForgeFetcher.searchModsByLoaderAndVersionAndKeyword(
                currentInstance.getModLoader().toString(),
                currentInstance.getVersion(),
                searchQuery,
                currentIndex,
                pageSize
        );

        System.out.println("aaa");

        if (!mods.isEmpty()) {
            this.modsBox.getChildren().clear();  // Vider la liste des mods avant d'ajouter les nouveaux résultats
            mods.forEach(mod -> this.modsBox.getChildren().add(mod.createModView(currentInstance)));

            currentIndex += pageSize;
        }

        loadingIndicator.setVisible(false);  // Masquer le spinner
        isLoading = false;
    }

    private void showCentralLoading(boolean show) {
        centralLoadingIndicator.setVisible(show);
    }


}