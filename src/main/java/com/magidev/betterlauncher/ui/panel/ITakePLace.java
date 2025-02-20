package com.magidev.betterlauncher.ui.panel;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public interface ITakePLace
{
    default void setCanTakeAllSize(Node node)
    {
        GridPane.setHgrow(node, Priority.ALWAYS);
        GridPane.setVgrow(node, Priority.ALWAYS);
    }

    default void setAlignement(Node node, VPos vPos, HPos hPos)
    {
        GridPane.setValignment(node, vPos);
        GridPane.setHalignment(node, hPos);
    }

    default void setHalignement(Node node, HPos hPos)
    {
        GridPane.setHalignment(node, hPos);
    }

    default void setValignement(Node node, VPos vPos)
    {
        GridPane.setValignment(node, vPos);
    }

    default void setCanTakeAllWidth(Node... nodes)
    {
        for (Node n : nodes) {
            GridPane.setHgrow(n, Priority.ALWAYS);
        }
    }
}
