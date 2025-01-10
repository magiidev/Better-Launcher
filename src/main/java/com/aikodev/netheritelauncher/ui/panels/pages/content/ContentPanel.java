package com.aikodev.netheritelauncher.ui.panels.pages.content;

import com.aikodev.netheritelauncher.ui.panel.Panel;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public abstract class ContentPanel extends Panel
{
    @Override
    public void onShow() {
        FadeTransition transition = new FadeTransition(Duration.millis(250), this.layout);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.setAutoReverse(true);
        transition.play();
    }
}
