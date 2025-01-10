package com.aikodev.netheritelauncher.ui.panels.partials;

import com.aikodev.netheritelauncher.ui.PanelManager;
import com.aikodev.netheritelauncher.ui.panel.Panel;
import com.aikodev.netheritelauncher.utils.Constants;
import fr.flowarg.materialdesignfontfx.MaterialDesignIcon;
import fr.flowarg.materialdesignfontfx.MaterialDesignIconView;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class TopBar extends Panel
{
    private GridPane topBar;

    @Override
    public String getName()
    {
        return "TopBar";
    }

    @Override
    public void init(PanelManager panelManager)
    {
        super.init(panelManager);

        this.topBar = this.layout;
        this.layout.setStyle("-fx-background-color: #A59D84;");
        setCanTakeAllWidth(this.topBar);

        /*
         * TopBar separation
         */
        // TopBar: left side
       /* ImageView imageView = new ImageView();
        imageView.setImage(new Image("images/icon.png"));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(25);
        setLeft(imageView);
        this.layout.getChildren().add(imageView);*/

        // TopBar: center
        Label title = new Label(Constants.LAUNCHER_NAME + " Launcher");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 18f));
        title.setStyle("-fx-text-fill: white;");
        setCenterH(title);
        this.layout.getChildren().add(title);

        // TopBar: right side
        GridPane topBarButton = new GridPane();
        topBarButton.setMinWidth(100d);
        topBarButton.setMaxWidth(100d);
        setCanTakeAllSize(topBarButton);
        setRight(topBarButton);
        this.layout.getChildren().add(topBarButton);

        /*
         * TopBar buttons configuration
         */
        final var closeBtn = new MaterialDesignIconView<>(MaterialDesignIcon.W.WINDOW_CLOSE);
        final var fullscreenBtn = new MaterialDesignIconView<>(MaterialDesignIcon.W.WINDOW_MAXIMIZE);
        final var minimizeBtn = new MaterialDesignIconView<>(MaterialDesignIcon.W.WINDOW_MINIMIZE);
        setCanTakeAllWidth(closeBtn, fullscreenBtn, minimizeBtn);

        closeBtn.setFill(Color.WHITE);
        closeBtn.setOpacity(.7f);
        closeBtn.setSize("26px");
        closeBtn.setOnMouseEntered(e -> closeBtn.setOpacity(1.f));
        closeBtn.setOnMouseExited(e -> closeBtn.setOpacity(.7f));
        closeBtn.setOnMouseClicked(e -> System.exit(0));
        closeBtn.setTranslateX(70f);

        fullscreenBtn.setFill(Color.WHITE);
        fullscreenBtn.setOpacity(0.70f);
        fullscreenBtn.setSize("24px");
        fullscreenBtn.setOnMouseEntered(e -> fullscreenBtn.setOpacity(1.0f));
        fullscreenBtn.setOnMouseExited(e -> fullscreenBtn.setOpacity(0.7f));
        fullscreenBtn.setOnMouseClicked(e -> this.panelManager.getStage().setMaximized(!this.panelManager.getStage().isMaximized()));
        fullscreenBtn.setTranslateX(47.0d);

        minimizeBtn.setFill(Color.WHITE);
        minimizeBtn.setOpacity(0.70f);
        minimizeBtn.setSize("26px");
        minimizeBtn.setOnMouseEntered(e -> minimizeBtn.setOpacity(1.0f));
        minimizeBtn.setOnMouseExited(e -> minimizeBtn.setOpacity(0.7f));
        minimizeBtn.setOnMouseClicked(e -> this.panelManager.getStage().setIconified(true));
        minimizeBtn.setTranslateX(20.0d);

        topBarButton.getChildren().addAll(closeBtn, fullscreenBtn, minimizeBtn);
    }
}
