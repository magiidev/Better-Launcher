package com.magidev.betterlauncher;

import com.magidev.betterlauncher.utils.Constants;
import javafx.application.Application;

import javax.swing.*;
import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        try
        {
            Class.forName("javafx.application.Application");
            Application.launch(Launcher.class, args);
        }
        catch (ClassNotFoundException e)
        {
            JOptionPane.showMessageDialog(
                    null,
                    "JavaFX not found",
                    Constants.LAUNCHER_NAME + " Launcher - Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
