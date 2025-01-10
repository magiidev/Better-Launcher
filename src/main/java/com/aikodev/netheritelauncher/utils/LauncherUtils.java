package com.aikodev.netheritelauncher.utils;

import com.aikodev.netheritelauncher.Launcher;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LauncherUtils
{
    public static void openURL(String url)
    {
        try
        {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            Launcher.getInstance().getLogger().warn(e.getMessage());
        }
    }
}
