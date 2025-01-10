package com.aikodev.netheritelauncher.ui.panels.pages;

import com.aikodev.netheritelauncher.Launcher;
import com.aikodev.netheritelauncher.ui.PanelManager;
import com.aikodev.netheritelauncher.ui.panel.Panel;
import com.aikodev.netheritelauncher.ui.panels.pages.content.*;
import com.aikodev.netheritelauncher.utils.Constants;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class App extends Panel
{
    GridPane sidemenu = new GridPane();
    GridPane navContent = new GridPane();

    Node activeLink = null;
    ContentPanel currentPage = null;

    Button homeBtn, instancesBtn, modsBtn, settingsBtn;

    Saver saver = Launcher.getInstance().getSaver();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/app.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        // Background
        this.layout.getStyleClass().add("app-layout");
        setCanTakeAllSize(this.layout);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(350);
        columnConstraints.setMaxWidth(350);
        this.layout.getColumnConstraints().addAll(columnConstraints, new ColumnConstraints());

        // Side menu
        this.layout.add(sidemenu, 0, 0);
        sidemenu.getStyleClass().add("sidemenu");
        setLeft(sidemenu);
        setCenterH(sidemenu);
        setCenterV(sidemenu);

        // Background Image
        GridPane bgImage = new GridPane();
        setCanTakeAllSize(bgImage);
        bgImage.getStyleClass().add("bg-image");
        this.layout.add(bgImage, 1, 0);

        // Nav content
        this.layout.add(navContent, 1, 0);
        navContent.getStyleClass().add("nav-content");
        setLeft(navContent);
        setCenterH(navContent);
        setCenterV(navContent);

        /*
         * Side menu
         */
        // Titre
        Label title = new Label(Constants.LAUNCHER_NAME + " Launcher");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 30f));
        title.getStyleClass().add("home-title");
        setCenterH(title);
        setCanTakeAllSize(title);
        setTop(title);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setTranslateY(30d);
        sidemenu.getChildren().add(title);

        var home = new MaterialDesignIconView<>(MaterialDesignIcon.H.HOME);
        home.setFill(Color.WHITE);

        var archive = new MaterialDesignIconView<>(MaterialDesignIcon.A.ARCHIVE);
        archive.setFill(Color.WHITE);

        var mod = new MaterialDesignIconView<>(MaterialDesignIcon.P.PACKAGE);
        mod.setFill(Color.WHITE);

        var settings = new MaterialDesignIconView<>(MaterialDesignIcon.C.COG);
        settings.setFill(Color.WHITE);

        // Navigation
        homeBtn = new Button("Home");
        homeBtn.getStyleClass().add("sidemenu-nav-btn");
        homeBtn.setGraphic(home);
        setCanTakeAllSize(homeBtn);
        setTop(homeBtn);
        homeBtn.setTranslateY(90d);
        homeBtn.setOnMouseClicked(e -> setPage(new Home(), homeBtn));

        instancesBtn = new Button("Instance Manager");
        instancesBtn.getStyleClass().add("sidemenu-nav-btn");
        instancesBtn.setGraphic(archive);
        setCanTakeAllSize(instancesBtn);
        setTop(instancesBtn);
        instancesBtn.setTranslateY(130d);
        instancesBtn.setOnMouseClicked(e -> setPage(new Instances(), instancesBtn));

        modsBtn = new Button("Mod Manager");
        modsBtn.getStyleClass().add("sidemenu-nav-btn");
        modsBtn.setGraphic(mod);
        setCanTakeAllSize(modsBtn);
        setTop(modsBtn);
        modsBtn.setTranslateY(170d);
        modsBtn.setOnMouseClicked(e -> setPage(new Mods(), modsBtn));

        settingsBtn = new Button("Settings");
        settingsBtn.getStyleClass().add("sidemenu-nav-btn");
        settingsBtn.setGraphic(settings);
        setCanTakeAllSize(settingsBtn);
        setTop(settingsBtn);
        settingsBtn.setTranslateY(210d);
        settingsBtn.setOnMouseClicked(e -> setPage(new Settings(), settingsBtn));

        sidemenu.getChildren().addAll(homeBtn, instancesBtn, modsBtn, settingsBtn);

        if (Launcher.getInstance().getAuthInfos() != null) {
            // Pseudo + avatar
            GridPane userPane = new GridPane();
            setCanTakeAllWidth(userPane);
            userPane.setMaxHeight(80);
            userPane.setMinWidth(80);
            userPane.getStyleClass().add("user-pane");
            setBottom(userPane);

            String avatarUrl = "https://minotar.net/avatar/" + (
                    saver.get("offline-username") != null ?
                            "MHF_Steve.png" :
                            Launcher.getInstance().getAuthInfos().getUuid() + ".png"
            );
            ImageView avatarView = new ImageView();
            Image avatarImg = new Image(avatarUrl);
            avatarView.setImage(avatarImg);
            avatarView.setPreserveRatio(true);
            avatarView.setFitHeight(50d);
            setCenterV(avatarView);
            setCanTakeAllSize(avatarView);
            setLeft(avatarView);
            avatarView.setTranslateX(15d);
            userPane.getChildren().add(avatarView);

            Label usernameLabel = new Label(Launcher.getInstance().getAuthInfos().getUsername());
            usernameLabel.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 25f));
            setCanTakeAllSize(usernameLabel);
            setCenterV(usernameLabel);
            setLeft(usernameLabel);
            usernameLabel.getStyleClass().add("username-label");
            usernameLabel.setTranslateX(75d);
            setCanTakeAllWidth(usernameLabel);
            userPane.getChildren().add(usernameLabel);

            Button logoutBtn = new Button();
            final var logoutIcon = new MaterialDesignIconView<>(MaterialDesignIcon.L.LOGOUT);
            logoutIcon.getStyleClass().add("logout-icon");
            setCanTakeAllSize(logoutBtn);
            setCenterV(logoutBtn);
            setRight(logoutBtn);
            logoutBtn.getStyleClass().add("logout-btn");
            logoutBtn.setGraphic(logoutIcon);
            logoutBtn.setOnMouseClicked(e -> {
                if (currentPage instanceof Home && ((Home) currentPage).isDownloading()) {
                    return;
                }
                saver.remove("accessToken");
                saver.remove("clientToken");
                saver.remove("offline-username");
                saver.remove("msAccessToken");
                saver.remove("msRefreshToken");
                saver.save();
                Launcher.getInstance().setAuthInfos(null);
                this.panelManager.showPanel(new Login());
            });
            userPane.getChildren().add(logoutBtn);

            sidemenu.getChildren().add(userPane);
        }
    }

    @Override
    public void onShow() {
        super.onShow();
        setPage(new Home(), homeBtn);
    }

    public void setPage(ContentPanel panel, Node navButton) {
        if (currentPage instanceof Home && ((Home) currentPage).isDownloading()) {
            return;
        }
        if (activeLink != null)
            activeLink.getStyleClass().remove("active");
        activeLink = navButton;
        activeLink.getStyleClass().add("active");

        this.navContent.getChildren().clear();
        if (panel != null) {
            this.navContent.getChildren().add(panel.getLayout());
            currentPage = panel;
            if (panel.getStylesheetPath() != null) {
                this.panelManager.getStage().getScene().getStylesheets().clear();
                this.panelManager.getStage().getScene().getStylesheets().addAll(
                        this.getStylesheetPath(),
                        panel.getStylesheetPath()
                );
            }
            panel.init(this.panelManager);
            panel.onShow();
        }
    }
}
