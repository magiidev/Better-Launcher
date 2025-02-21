package com.magidev.betterlauncher;

import com.magidev.betterlauncher.ui.PanelManager;
import com.magidev.betterlauncher.ui.panels.pages.App;
import com.magidev.betterlauncher.ui.utils.lang.LanguagePanel;
import com.magidev.betterlauncher.ui.panels.pages.Login;
import com.magidev.betterlauncher.ui.utils.theme.ThemeManager;
import com.magidev.betterlauncher.utils.Constants;
import com.magidev.betterlauncher.ui.utils.lang.LanguageManager;
import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.util.GameDirGenerator;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class Launcher extends Application
{
    private static Launcher instance;
    private PanelManager panelManager;
    private final ILogger logger;
    private final Path launcherDir = GameDirGenerator.createGameDir(Constants.LAUNCHER_NAME.toLowerCase() + "-launcher", true);
    private final Saver saver;
    private AuthInfos authInfos = null;

    public Launcher() throws IOException
    {
        instance = this;

        System.setProperty("file.encoding", "UTF-8");

        this.logger = new Logger("[" + Constants.LAUNCHER_NAME + " Launcher]", this.launcherDir.resolve("launcher.log"));
        if (Files.notExists(this.launcherDir))
        {
            try
            {
                Files.createDirectory(this.launcherDir);
            } catch (IOException e)
            {
                this.logger.err("Unable to create launcher folder");
                this.logger.printStackTrace(e);
            }
        }

        if(Files.notExists(this.launcherDir.resolve("java")))
        {
            try
            {
                Files.createDirectory(this.launcherDir.resolve("java"));
            } catch (IOException e)
            {
                this.logger.err("Unable to create launcher folder");
                this.logger.printStackTrace(e);
            }
        }

        if(Files.notExists(this.launcherDir.resolve("instance")))
        {
            try
            {
                Files.createDirectory(this.launcherDir.resolve("instance"));
            } catch (IOException e)
            {
                this.logger.err("Unable to create launcher folder");
                this.logger.printStackTrace(e);
            }
        }

        saver = new Saver(this.launcherDir.resolve("config.properties"));
        saver.load();
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        this.logger.info("Starting launcher");

        ThemeManager.currentTheme = ThemeManager.getTheme(saver.get("theme"));

        panelManager = new PanelManager(this, stage);
        panelManager.init();

        if (this.isUserAlreadyLoggedIn() && saver.get("lang") != null)
        {
            logger.info("Hello " + authInfos.getUsername());

            LanguageManager.currentLanguage = LanguageManager.getLanguageByName(saver.get("lang"));

            this.panelManager.showPanel(new App());
        } else {
            if(saver.get("lang") != null)
            {
                LanguageManager.currentLanguage = LanguageManager.getLanguageByName(saver.get("lang"));

                this.panelManager.showPanel(new Login());
            }
            else
            {
                this.panelManager.showPanel(new LanguagePanel());
            }
        }
    }

    public boolean isUserAlreadyLoggedIn()
    {
        if (saver.get("msAccessToken") != null && saver.get("msRefreshToken") != null)
        {
            try {
                MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                MicrosoftAuthResult response = authenticator.loginWithRefreshToken(saver.get("msRefreshToken"));

                saver.set("msAccessToken", response.getAccessToken());
                saver.set("msRefreshToken", response.getRefreshToken());
                saver.save();
                this.setAuthInfos(new AuthInfos(
                        response.getProfile().getName(),
                        response.getAccessToken(),
                        response.getProfile().getId(),
                        response.getXuid(),
                        response.getClientId()
                ));
                return true;
            } catch (MicrosoftAuthenticationException e) {
                saver.remove("msAccessToken");
                saver.remove("msRefreshToken");
                saver.save();
            }
        } else if (saver.get("offline-username") != null) {
            this.authInfos = new AuthInfos(saver.get("offline-username"), UUID.randomUUID().toString(), UUID.randomUUID().toString());
            return true;
        }

        return false;
    }

    public static Launcher getInstance()
    {
        return instance;
    }

    public AuthInfos getAuthInfos()
    {
        return authInfos;
    }

    public void setAuthInfos(AuthInfos authInfos)
    {
        this.authInfos = authInfos;
    }

    @Override
    public void stop()
    {
        Platform.exit();
        System.exit(0);
    }

    public ILogger getLogger()
    {
        return logger;
    }

    public Saver getSaver()
    {
        return saver;
    }

    public Path getLauncherDir()
    {
        return launcherDir;
    }

    public Path getJavaDir()
    {
        return launcherDir.resolve("java");
    }

    public Path getInstanceDir() { return launcherDir.resolve("instance"); }
}
