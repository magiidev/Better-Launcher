package com.magidev.betterlauncher.ui.panels.pages.content;

import com.magidev.betterlauncher.game.mod.Mod;
import com.magidev.betterlauncher.game.instance.Instance;
import com.magidev.betterlauncher.game.instance.InstanceManager;
import com.magidev.betterlauncher.game.fetchers.CurseForgeFetcher;
import com.magidev.betterlauncher.game.mod.ModLoader;
import com.magidev.betterlauncher.ui.PanelManager;
import com.magidev.betterlauncher.ui.utils.lang.LanguageManager;
import com.magidev.betterlauncher.ui.utils.theme.ThemeManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
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

public class Mods extends ContentPanel {
    private ComboBox<String> instancesBox;
    private Instance currentInstance = null;
    private VBox modsBox;
    private CurseForgeFetcher curseForgeFetcher;
    private int currentIndex = 0;
    private int currentIndexSearch = 0;
    private final int pageSize = 20;
    private final int pageSizeSearch = 10;
    private boolean isLoading = false;
    private Label loadingIndicator;
    private ProgressIndicator bottomLoadingIndicator;
    private ProgressIndicator centralLoadingIndicator;
    // Conteneur pour le label "Loading mods..." et le progress indicator (petit)
    private HBox loadingContainer;

    public Mods() {
            curseForgeFetcher = new CurseForgeFetcher("$2a$10$46YfrDZjVZ9AP2h2XjxZ3.d3vspD2H1mQAXvrCfjS3Zb9MfRkqQni");
    }

    @Override
    public String getName() {
        return "Mod Manager";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/" + ThemeManager.getCurrentTheme().getName().toLowerCase() + "/mods.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);
        layout.getStyleClass().add("mods-layout");
        layout.setPadding(new Insets(40));
        setCanTakeAllSize(layout);

        // Création du conteneur principal
        VBox mainContent = createMainContent();
        StackPane mainContainer = createMainContainer(mainContent);
        layout.getChildren().add(mainContainer);

        // Ajout du gros progress indicator central
        centralLoadingIndicator = createCentralLoadingIndicator();
        mainContainer.getChildren().add(centralLoadingIndicator);
        StackPane.setAlignment(centralLoadingIndicator, Pos.CENTER);

        // Ajout du titre
        Label title = createTitle();
        mainContent.getChildren().add(title);

        // Création de la ligne du haut (ComboBox, champ de recherche et indicateur de chargement)
        HBox topRow = createTopRow();
        mainContent.getChildren().add(topRow);

        // Remplissage du ComboBox avec les instances compatibles
        populateInstancesComboBox();

        // Création du conteneur de mods et du ScrollPane
        modsBox = new VBox(10);
        modsBox.setAlignment(Pos.TOP_CENTER);
        ScrollPane scrollPane = createModsScrollPane(modsBox);
        mainContent.getChildren().add(scrollPane);

        // Événement de sélection d'une instance
        instancesBox.setOnAction(event -> {
            String selectedInstanceName = instancesBox.getValue();
            if (selectedInstanceName != null) {
                currentInstance = InstanceManager.getInstanceByName(selectedInstanceName);
                if (currentInstance != null) {
                    // Affiche l'indicateur de chargement
                    loadingIndicator.setVisible(true);
                    bottomLoadingIndicator.setVisible(true);
                    reloadMods();
                }
            }
        });
    }

    // Crée le conteneur principal VBox
    private VBox createMainContent() {
        VBox mainContent = new VBox();
        mainContent.setSpacing(20);
        mainContent.setAlignment(Pos.TOP_LEFT);
        setCanTakeAllSize(mainContent);
        return mainContent;
    }

    // Crée un StackPane pour superposer le contenu et le central loading indicator
    private StackPane createMainContainer(VBox mainContent) {
        StackPane container = new StackPane();
        container.getChildren().add(mainContent);
        return container;
    }

    // Crée le gros progress indicator central
    private ProgressIndicator createCentralLoadingIndicator() {
        ProgressIndicator pi = new ProgressIndicator();
        pi.setMaxSize(100, 100);
        pi.setVisible(false);
        return pi;
    }

    // Crée le titre "Mods"
    private Label createTitle() {
        Label title = new Label("Mods");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
        title.getStyleClass().add("instances-title");
        title.setTextAlignment(TextAlignment.LEFT);
        return title;
    }

    // Crée la ligne du haut contenant le ComboBox, le champ de recherche et le container de chargement
    private HBox createTopRow() {
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        instancesBox = createInstancesComboBox();
        TextField searchField = createSearchField();
        loadingContainer = createLoadingContainer();

        topRow.getChildren().addAll(instancesBox, searchField, loadingContainer);
        return topRow;
    }

    // Crée le ComboBox pour la sélection d'instance
    private ComboBox<String> createInstancesComboBox() {
        ComboBox<String> combo = new ComboBox<>();
        combo.setPromptText(LanguageManager.get("select-instance"));
        combo.getStyleClass().add("instance-box");
        setCanTakeAllSize(combo);
        return combo;
    }

    // Crée le champ de recherche
    private TextField createSearchField() {
        TextField search = new TextField();
        search.setPromptText(LanguageManager.get("search-mods"));
        search.getStyleClass().add("search-box");
        search.setPrefWidth(250);
        setCanTakeAllSize(search);
        search.setOnAction(event -> {
            String searchQuery = search.getText();
            if (!searchQuery.isEmpty()) {
                searchMods(searchQuery);
            } else {
                reloadMods();
            }
        });
        return search;
    }

    // Crée le conteneur de chargement (label + petit progress indicator)
    private HBox createLoadingContainer() {
        HBox container = new HBox(5);
        container.setAlignment(Pos.CENTER_LEFT);

        loadingIndicator = new Label(LanguageManager.get("loading-mods"));
        loadingIndicator.setFont(Font.font("Consolas", FontWeight.NORMAL, FontPosture.ITALIC, 20f));
        loadingIndicator.setTextFill(Color.GRAY);
        loadingIndicator.setVisible(false);

        bottomLoadingIndicator = new ProgressIndicator();
        bottomLoadingIndicator.setMaxSize(30, 30);
        bottomLoadingIndicator.setVisible(false);

        container.getChildren().addAll(loadingIndicator, bottomLoadingIndicator);
        return container;
    }

    // Crée le ScrollPane pour afficher les mods
    private ScrollPane createModsScrollPane(VBox modsBox) {
        ScrollPane scrollPane = new ScrollPane(modsBox);
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
        return scrollPane;
    }

    // Remplit le ComboBox avec les instances compatibles
    private void populateInstancesComboBox() {
        List<ModLoader> modLoaders = Arrays.asList(ModLoader.FORGE, ModLoader.NEOFORGE, ModLoader.FABRIC);
        List<String> instanceNames = new ArrayList<>();
        for (Instance instance : InstanceManager.getInstances()) {
            if (modLoaders.contains(instance.getModLoader())) {
                instanceNames.add(instance.getName());
            }
        }
        instancesBox.getItems().addAll(instanceNames);
    }

    /**
     * Recharge les mods pour l'instance sélectionnée en arrière-plan.
     */
    private void reloadMods() {
        if (modsBox != null && currentInstance != null) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    Platform.runLater(() -> modsBox.getChildren().clear());
                    currentIndex = 0;
                    List<Mod> mods = curseForgeFetcher.getModsByLoaderAndVersion(
                            currentInstance.getModLoader().toString(),
                            currentInstance.getVersion(),
                            currentIndex,
                            pageSize
                    );
                    if (!mods.isEmpty()) {
                        Platform.runLater(() -> {
                            mods.forEach(mod -> modsBox.getChildren().add(mod.createModView(currentInstance)));
                        });
                        currentIndex += mods.size();
                    }
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                // Cache les indicateurs de chargement après chargement
                loadingIndicator.setVisible(false);
                bottomLoadingIndicator.setVisible(false);
            });
            task.setOnFailed(e -> {
                loadingIndicator.setVisible(false);
                bottomLoadingIndicator.setVisible(false);
            });
            new Thread(task).start();
        }
    }

    /**
     * Charge les mods suivants (scroll infini) en arrière-plan.
     */
    private void loadMoreMods() {
        if (currentInstance == null) return;
        isLoading = true;
        loadingIndicator.setVisible(true);
        bottomLoadingIndicator.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<Mod> mods = curseForgeFetcher.getModsByLoaderAndVersion(
                        currentInstance.getModLoader().toString(),
                        currentInstance.getVersion(),
                        currentIndex,
                        pageSize
                );
                if (!mods.isEmpty()) {
                    Platform.runLater(() -> mods.forEach(mod -> modsBox.getChildren().add(mod.createModView(currentInstance))));
                    currentIndex += mods.size();
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            bottomLoadingIndicator.setVisible(false);
            isLoading = false;
        });
        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            bottomLoadingIndicator.setVisible(false);
            isLoading = false;
        });
        new Thread(task).start();
    }

    /**
     * Recherche les mods selon un mot-clé, en arrière-plan.
     */
    private void searchMods(String searchQuery) {
        if (currentInstance == null) return;
        isLoading = true;
        loadingIndicator.setVisible(true);
        bottomLoadingIndicator.setVisible(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<Mod> mods = curseForgeFetcher.searchModsByLoaderAndVersionAndKeyword(
                        currentInstance.getModLoader().toString(),
                        currentInstance.getVersion(),
                        searchQuery,
                        currentIndexSearch,
                        pageSizeSearch
                );
                if (!mods.isEmpty()) {
                    Platform.runLater(() -> {
                        modsBox.getChildren().clear();
                        mods.forEach(mod -> modsBox.getChildren().add(mod.createModView(currentInstance)));
                    });
                    currentIndex += pageSize;
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            loadingIndicator.setVisible(false);
            bottomLoadingIndicator.setVisible(false);
            isLoading = false;
        });
        task.setOnFailed(e -> {
            loadingIndicator.setVisible(false);
            bottomLoadingIndicator.setVisible(false);
            isLoading = false;
        });
        new Thread(task).start();
    }
}
