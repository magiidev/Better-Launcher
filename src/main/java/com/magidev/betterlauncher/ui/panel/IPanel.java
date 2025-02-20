package com.magidev.betterlauncher.ui.panel;

import com.magidev.betterlauncher.ui.PanelManager;
import javafx.scene.layout.GridPane;

public interface IPanel
{
    void init(PanelManager panelManager);
    GridPane getLayout();
    void onShow();
    String getName();
    String getStylesheetPath();
}
