package com.magidev.betterlauncher.ui;

import com.magidev.betterlauncher.Launcher;
import com.magidev.betterlauncher.ui.panel.IPanel;
import com.magidev.betterlauncher.ui.panels.partials.TopBar;
import com.magidev.betterlauncher.utils.Constants;
import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import fr.flowarg.flowcompat.Platform;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PanelManager
{
    private final Launcher launcher;
    private final Stage stage;
    private GridPane layout;
    private final GridPane contentPane = new GridPane();
    private TopBar topBar;

    public PanelManager(Launcher launcher, Stage stage)
    {
        this.launcher = launcher;
        this.stage = stage;
    }

    public void init()
    {
        this.stage.setTitle(Constants.LAUNCHER_NAME + " Launcher");
        this.stage.setMinWidth(854);
        this.stage.setMinHeight(480);
        this.stage.setWidth(1280);
        this.stage.setHeight(720);
        this.stage.centerOnScreen();
        this.stage.getIcons().add(new Image("/images/icon.png"));

        this.layout = new GridPane();

        if (Platform.isOnLinux())
        {
            Scene scene = new Scene(this.layout);
            this.stage.setScene(scene);
        } else {
            this.stage.initStyle(StageStyle.UNDECORATED);

            topBar = new TopBar();
            BorderlessScene scene = new BorderlessScene(this.stage, StageStyle.UNDECORATED, this.layout);
            scene.setResizable(true);
            scene.setMoveControl(topBar.getLayout());
            scene.removeDefaultCSS();
            this.stage.setScene(scene);

            RowConstraints topPaneContraints = new RowConstraints();
            topPaneContraints.setValignment(VPos.TOP);
            topPaneContraints.setMinHeight(25);
            topPaneContraints.setMaxHeight(25);
            this.layout.getRowConstraints().addAll(topPaneContraints, new RowConstraints());
            this.layout.add(topBar.getLayout(), 0, 0);
            topBar.init(this);
        }

        this.layout.add(this.contentPane, 0, 1);
        GridPane.setVgrow(this.contentPane, Priority.ALWAYS);
        GridPane.setHgrow(this.contentPane, Priority.ALWAYS);

        this.stage.show();
    }

    public void showPanel(IPanel panel)
    {
        this.contentPane.getChildren().clear();
        this.contentPane.getChildren().add(panel.getLayout());

        if (panel.getStylesheetPath() != null)
        {
            this.stage.getScene().getStylesheets().clear();
            this.stage.getScene().getStylesheets().add(panel.getStylesheetPath());
        }

        panel.init(this);
        panel.onShow();
    }

    public Stage getStage()
    {
        return stage;
    }

    public Launcher getLauncher()
    {
        return launcher;
    }

    public TopBar getTopBar()
    {
        return topBar;
    }
}
