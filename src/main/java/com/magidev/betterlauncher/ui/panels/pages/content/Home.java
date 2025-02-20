package com.magidev.betterlauncher.ui.panels.pages.content;

import com.magidev.betterlauncher.Launcher;
import com.magidev.betterlauncher.game.launching.Game;
import com.magidev.betterlauncher.game.mod.ModLoader;
import com.magidev.betterlauncher.game.utils.JavaDownloader;
import com.magidev.betterlauncher.game.instance.Instance;
import com.magidev.betterlauncher.game.instance.InstanceManager;
import com.magidev.betterlauncher.ui.PanelManager;
import com.magidev.betterlauncher.utils.LanguageManager;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.Step;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Home extends ContentPanel
{
    private final Saver saver = Launcher.getInstance().getSaver();
    GridPane boxPane = new GridPane();
    ProgressBar progressBar = new ProgressBar();
    Label stepLabel = new Label();
    Label fileLabel = new Label();
    boolean isDownloading = false;

    ComboBox<String> instanceBox;

    Instance currentInstance = null;

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getStylesheetPath() {
        return "css/content/home.css";
    }

    @Override
    public void init(PanelManager panelManager)
    {
        super.init(panelManager);

        InstanceManager instanceManager = InstanceManager.getInstance();

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        rowConstraints.setMinHeight(75);
        rowConstraints.setMaxHeight(75);
        this.layout.getRowConstraints().addAll(rowConstraints, new RowConstraints());
        boxPane.getStyleClass().add("box-pane");
        setCanTakeAllSize(boxPane);
        boxPane.setPadding(new Insets(20));
        this.layout.add(boxPane, 0, 0);
        this.layout.getStyleClass().add("home-layout");

        progressBar.getStyleClass().add("download-progress");
        stepLabel.getStyleClass().add("download-status");
        fileLabel.getStyleClass().add("download-status");

        progressBar.setTranslateY(-15);
        setCenterH(progressBar);
        setCanTakeAllWidth(progressBar);

        stepLabel.setTranslateY(5);
        setCenterH(stepLabel);
        setCanTakeAllSize(stepLabel);

        fileLabel.setTranslateY(20);
        setCenterH(fileLabel);
        setCanTakeAllSize(fileLabel);

        this.showPlayButton();
        this.showInstancesBox(boxPane);
    }

    private void showPlayButton()
    {
        boxPane.getChildren().clear();
        Button playBtn = new Button(LanguageManager.get("play"));
        final var playIcon = new MaterialDesignIconView<>(MaterialDesignIcon.G.GAMEPAD);
        playIcon.getStyleClass().add("play-icon");
        setCanTakeAllSize(playBtn);
        setCenterH(playBtn);
        setCenterV(playBtn);
        playBtn.getStyleClass().add("play-btn");
        playBtn.setGraphic(playIcon);
        playBtn.setOnMouseClicked(e -> {
            if(instanceBox.getValue() == null)
            {
                new Alert(Alert.AlertType.ERROR, LanguageManager.get("select-instance-please")).showAndWait();
                return;
            }
            else
            {
                this.play();
            }
        });
        boxPane.getChildren().add(playBtn);
    }

    private void showInstancesBox(GridPane gridPane)
    {
        instanceBox = new ComboBox<>();
        instanceBox.setPromptText(LanguageManager.get("select-instance"));
        instanceBox.getStyleClass().add("instance-box");
        setCanTakeAllSize(instanceBox);
        setLeft(instanceBox);

        List<String> instanceNames = new ArrayList<>();
        for (Instance instance : InstanceManager.getInstances()) {
            instanceNames.add(instance.getName());
        }
        instanceBox.getItems().addAll(instanceNames);

        gridPane.getChildren().addAll(instanceBox);
    }


    private void play() {
        isDownloading = true;
        boxPane.getChildren().clear();
        setProgress(0, 0);
        boxPane.getChildren().addAll(progressBar, stepLabel, fileLabel);

        new Thread(this::update).start();
    }

    public void update()
    {
        checkJava();

        currentInstance = InstanceManager.getInstanceByName(instanceBox.getValue());
        String version = currentInstance.getVersion();

        Game game = new Game(currentInstance, logger, saver);

        IProgressCallback callback = createProgressCallback();

        try {
            if (currentInstance.getModLoader().equals(ModLoader.FORGE)) {
                game.handleForgeUpdate(version, callback);
            } else if (currentInstance.getModLoader().equals(ModLoader.FABRIC)) {
                game.handleFabricUpdate(version, callback, false);
            } else if (currentInstance.getModLoader().equals(ModLoader.VANILLA)) {
                game.handleVanillaUpdate(version, callback);
            } else if (currentInstance.getModLoader().equals(ModLoader.SODIUM)) {
                game.handleFabricUpdate(version, callback, true);
            }

        } catch (Exception e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
            Platform.runLater(() -> this.panelManager.getStage().show());
        }
    }

    private IProgressCallback createProgressCallback() {
        return new IProgressCallback() {
            private final DecimalFormat decimalFormat = new DecimalFormat("#.#");
            private String stepTxt = "";
            private String percentTxt = "0.0%";

            @Override
            public void step(Step step) {
                Platform.runLater(() -> {
                    stepTxt = StepInfo.valueOf(step.name()).getDetails();

                    setStatus(String.format("%s (%s)", stepTxt, percentTxt));
                });
            }

            @Override
            public void update(DownloadList.DownloadInfo info) {
                Platform.runLater(() -> {
                    percentTxt = decimalFormat.format(info.getDownloadedBytes() * 100.0 / info.getTotalToDownloadBytes()) + "%";
                    setStatus(String.format("%s (%s)", stepTxt, percentTxt));
                    setProgress(info.getDownloadedBytes(), info.getTotalToDownloadBytes());
                });
            }

            @Override
            public void onFileDownloaded(Path path) {
                Platform.runLater(() -> {
                    String p = path.toString();
                    fileLabel.setText("..." + p.replace(Launcher.getInstance().getLauncherDir().toFile().getAbsolutePath(), ""));
                });
            }
        };
    }

    public void checkJava()
    {
        int[] javaVersions = {8, 16, 17, 21};

        boolean isAnyVersionMissing = false;

        // Vérification de chaque version Java
        for (int version : javaVersions) {
            Path javaPath = JavaDownloader.getJavaPath(String.valueOf(version));
            if (!Files.exists(javaPath)) {
                System.out.println("Java version " + version + " is missing at: " + javaPath);
                isAnyVersionMissing = true;
            } else {
                System.out.println("Java version " + version + " exists at: " + javaPath);
            }
        }

        if (isAnyVersionMissing) {
            System.out.println("One or more Java versions are missing. Downloading all versions...");
            JavaDownloader.downloadAllJavaVersions();
        } else {
            System.out.println("All Java versions are already available.");
        }
    }

    public void setStatus(String status) {
        this.stepLabel.setText(status);
    }

    public void setProgress(double current, double max) {
        this.progressBar.setProgress(current / max);
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public enum StepInfo
    {
        READ("Reading the JSON file..."),
        DL_LIBS("Downloading libraries..."),
        DL_ASSETS("Downloading assets..."),
        EXTRACT_NATIVES("Extracting natives..."),
        FORGE("Installing Forge..."),
        FABRIC("Installing Fabric..."),
        MODS("Downloading mods..."),
        EXTERNAL_FILES("Downloading external files..."),
        POST_EXECUTIONS("Post-installation execution..."),
        MOD_LOADER("Installing mod loader..."),
        INTEGRATION("Integrating mods..."),
        END("Finished!");

        final String details;

        StepInfo(String details) {
            this.details = details;
        }

        public String getDetails() {
            return details;
        }
    }
}
