package com.magidev.betterlauncher.game.utils;

import com.magidev.betterlauncher.Launcher;
import com.magidev.betterlauncher.utils.Constants;
import fr.flowarg.azuljavadownloader.*;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class JavaDownloader
{
    static final AzulJavaDownloader downloader = new AzulJavaDownloader(System.out::println);
    static final Path javas = Launcher.getInstance().getJavaDir();

    public static Path downloadJavaVersion(String version) throws IOException {
        Path javaHomePath = javas.resolve("jdk" + version);

        if (Files.exists(javaHomePath))
        {
            System.out.println("Java " + version + " is already downloaded at: " + javaHomePath.toAbsolutePath());
            return javaHomePath;
        }

        final AzulJavaBuildInfo buildInfo = downloader.getBuildInfo(new RequestedJavaInfo(version, AzulJavaType.JDK, AzulJavaOS.WINDOWS, AzulJavaArch.X64).setJavaFxBundled(false));
        javaHomePath = downloader.downloadAndInstall(buildInfo, javas);

        Files.move(
                javaHomePath.toAbsolutePath(),
                javas.resolve("jdk" + version),
                StandardCopyOption.REPLACE_EXISTING
        );

        return javaHomePath.toAbsolutePath();
    }

    public static void downloadAllJavaVersions() {
        try {
            downloadJavaVersion("8");
            downloadJavaVersion("16");
            downloadJavaVersion("17");
            downloadJavaVersion("21");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while downloading Java.", Constants.LAUNCHER_NAME + " Launcher", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static Path getJavaPath(String version)
    {
        return javas.resolve("jdk" + version);
    }
}
