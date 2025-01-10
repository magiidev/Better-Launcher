package com.aikodev.netheritelauncher.ui.panel;

import com.aikodev.netheritelauncher.ui.PanelManager;
import javafx.scene.layout.GridPane;

public interface IPanel
{
    void init(PanelManager panelManager);
    GridPane getLayout();
    void onShow();
    String getName();
    String getStylesheetPath();
}
