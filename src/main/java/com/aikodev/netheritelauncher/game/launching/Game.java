package com.aikodev.netheritelauncher.game.launching;

import com.aikodev.netheritelauncher.Launcher;
import com.aikodev.netheritelauncher.game.instance.Instance;
import com.aikodev.netheritelauncher.game.fetchers.CurseForgeFetcher;
import com.aikodev.netheritelauncher.game.utils.JavaDownloader;
import com.aikodev.netheritelauncher.game.mod.ModLoader;
import com.aikodev.netheritelauncher.game.fetchers.VersionFetcher;
import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.download.json.CurseFileInfo;
import fr.flowarg.flowupdater.utils.UpdaterOptions;
import fr.flowarg.flowupdater.utils.Version;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.flowupdater.versions.fabric.FabricVersion;
import fr.flowarg.flowupdater.versions.fabric.FabricVersionBuilder;
import fr.flowarg.flowupdater.versions.forge.ForgeVersion;
import fr.flowarg.flowupdater.versions.forge.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.neoforge.NeoForgeVersion;
import fr.flowarg.flowupdater.versions.neoforge.NeoForgeVersionBuilder;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.JavaUtil;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Game
{
    private Instance currentInstance;
    private ILogger logger;
    private Saver saver;

    private CurseForgeFetcher curseForgeFetcher;

    public Game(Instance currentInstance, ILogger logger, Saver saver)
    {
        this.currentInstance = currentInstance;
        this.logger = logger;
        this.saver = saver;

        curseForgeFetcher = new CurseForgeFetcher("$2a$10$46YfrDZjVZ9AP2h2XjxZ3.d3vspD2H1mQAXvrCfjS3Zb9MfRkqQni");
    }

    public void startGame(String gameVersion, String modLoaderVersion)
    {
        try {
            NoFramework noFramework;
            Process process = null;

            if (currentInstance.getModLoader().equals(ModLoader.VANILLA)) {
                Path versionVanillaDir = Paths.get(Launcher.getInstance().getInstanceDir().toString(), currentInstance.getName());

                noFramework = createNoFramework(versionVanillaDir);
                process = noFramework.launch(gameVersion, currentInstance.getVersion(), NoFramework.ModLoader.VANILLA);
            } else if (currentInstance.getModLoader().equals(ModLoader.FORGE)) {
                process = handleForgeGameLaunch(gameVersion, modLoaderVersion);
            }
            else if(currentInstance.getModLoader().equals(ModLoader.FABRIC))
            {
                Path versionFabricDir = Paths.get(Launcher.getInstance().getInstanceDir().toString(), currentInstance.getName());

                noFramework = createNoFramework(versionFabricDir);
                process = noFramework.launch(gameVersion, modLoaderVersion, NoFramework.ModLoader.FABRIC);
            }
            else if(currentInstance.getModLoader().equals(ModLoader.SODIUM))
            {
                Path versionFabricDir = Paths.get(Launcher.getInstance().getInstanceDir().toString(), currentInstance.getName());

                noFramework = createNoFramework(versionFabricDir);
                process = noFramework.launch(gameVersion, modLoaderVersion, NoFramework.ModLoader.FABRIC);
            }
            else if(currentInstance.getModLoader().equals(ModLoader.NEOFORGE))
            {
                Path versionNeoForgeDir = Paths.get(Launcher.getInstance().getInstanceDir().toString(), currentInstance.getName());

                noFramework = createNoFramework(versionNeoForgeDir);
                process = noFramework.launch(gameVersion, modLoaderVersion, NoFramework.ModLoader.NEO_FORGE);
            }

            final Process finalProcess = process;
            Platform.runLater(() -> {
                try {
                    finalProcess.waitFor();
                    Platform.exit();
                } catch (InterruptedException e) {
                    Launcher.getInstance().getLogger().printStackTrace(e);
                }
            });
        } catch (Exception e) {
            Launcher.getInstance().getLogger().printStackTrace(e);
        }
    }

    public void handleForgeUpdate(String version, IProgressCallback callback ) throws Exception
    {
        VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder().withName(version).build();
        String forgeVersion = VersionFetcher.getLatestForgeVersion(version); // 1.8.9-11.15.1.2318-1.8.9

        ForgeVersion forge;

        if(doesTweak(currentInstance.getVersion()))
        {
                forge = new ForgeVersionBuilder()
                        .withForgeVersion(forgeVersion + "-" + currentInstance.getVersion())
                        .build();

        }
        else if(currentInstance.getVersion().equals("1.10"))
        {
                forge = new ForgeVersionBuilder()
                        .withForgeVersion(forgeVersion + "-1.10.0")
                        .build();
        }
        else
        {
                forge = new ForgeVersionBuilder()
                        .withForgeVersion(forgeVersion).build();

        }

        Path javaHome = null;

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.21"), Version.gen("1.21.4")))
        {
            javaHome = JavaDownloader.getJavaPath("21");
        }

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.18"), Version.gen("1.20.6")))
        {
            javaHome = JavaDownloader.getJavaPath("17");
        }

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.17"), Version.gen("1.17.1")))
        {
            javaHome = JavaDownloader.getJavaPath("16");
        }

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.7"), Version.gen("1.16.5")))
        {
            javaHome = JavaDownloader.getJavaPath("8");
        }

        assert javaHome != null;

        UpdaterOptions options = new UpdaterOptions.UpdaterOptionsBuilder().withJavaPath(javaHome.resolve("bin").resolve("java").toAbsolutePath().toString()).build();
        Path versionForgeDir = getInstanceDirectory(currentInstance.getName());

        FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanillaVersion)
                .withModLoaderVersion(forge)
                .withUpdaterOptions(options)
                .withLogger(logger)
                .withProgressCallback(callback)
                .build();

        updater.update(versionForgeDir);
        JavaUtil.setJavaCommand(null);
        System.setProperty("java.home", javaHome.toString());
        startGame(vanillaVersion.getName(), forge.getModLoaderVersion());
    }

    private Process handleForgeGameLaunch(String gameVersion, String forgeVersion) throws Exception
    {
        Path versionForgeDir = Paths.get(Launcher.getInstance().getInstanceDir().toString(), currentInstance.getName());
        NoFramework noFramework = createNoFramework(versionForgeDir);

        if (Version.gen(currentInstance.getVersion()).isBetweenOrEqual(Version.gen("1.7"), Version.gen("1.7.10")))
        {
            return noFramework.launch(gameVersion, forgeVersion.split("-")[1], NoFramework.ModLoader.VERY_OLD_FORGE);
        } else if (isOldForge(currentInstance.getVersion())) {
            if(doesTweak(currentInstance.getVersion()))
            {
                return noFramework.launch(gameVersion, forgeVersion.split("-")[1] + "-" + currentInstance.getVersion(), NoFramework.ModLoader.OLD_FORGE);
            }
            else if (currentInstance.getVersion().equals("1.10"))
            {
                return noFramework.launch(gameVersion, forgeVersion.split("-")[1]+ "-1.10.0", NoFramework.ModLoader.OLD_FORGE);
            }
            else
            {
                return noFramework.launch(gameVersion, forgeVersion.split("-")[1], NoFramework.ModLoader.OLD_FORGE);
            }
        } else {
            return noFramework.launch(gameVersion, forgeVersion.split("-")[1], NoFramework.ModLoader.FORGE);
        }
    }

    public void handleVanillaUpdate(String version, IProgressCallback callback) throws Exception {
        VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder().withName(version).build();
        Path versionVanillaDir = getInstanceDirectory(currentInstance.getName());

        Path javaHome = null;

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.21"), Version.gen("1.21.4")))
        {
            javaHome = JavaDownloader.getJavaPath("21");
        }

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.18"), Version.gen("1.20.6")))
        {
            javaHome = JavaDownloader.getJavaPath("17");
        }

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.17"), Version.gen("1.17.1")))
        {
            javaHome = JavaDownloader.getJavaPath("16");
        }

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.7"), Version.gen("1.16.5")))
        {
            javaHome = JavaDownloader.getJavaPath("8");
        }

        assert javaHome != null;


        UpdaterOptions options = new UpdaterOptions.UpdaterOptionsBuilder().withJavaPath(javaHome.resolve("bin").resolve("java").toAbsolutePath().toString()).build();

        // Exécution de la mise à jour avec les options définies
        FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanillaVersion)
                .withUpdaterOptions(options)
                .withLogger(logger)
                .withProgressCallback(callback)
                .build();

        updater.update(versionVanillaDir);
        JavaUtil.setJavaCommand(null);
        System.setProperty("java.home", javaHome.toString());
        startGame(vanillaVersion.getName(), "");
    }

    private void handleNeoForgeUpdate(String version, IProgressCallback callback) throws Exception
    {
        VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder().withName(version).build();
        String forgeVersion = VersionFetcher.getLatestForgeVersion(version); // 1.8.9-11.15.1.2318-1.8.9

        NeoForgeVersion forge = new NeoForgeVersionBuilder()
                .build();

        Path javaHome = null;

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.21"), Version.gen("1.21.4")))
        {
            javaHome = JavaDownloader.getJavaPath("21");
        }

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.18"), Version.gen("1.20.6")))
        {
            javaHome = JavaDownloader.getJavaPath("17");
        }

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.17"), Version.gen("1.17.1")))
        {
            javaHome = JavaDownloader.getJavaPath("16");
        }

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.7"), Version.gen("1.16.5")))
        {
            javaHome = JavaDownloader.getJavaPath("8");
        }

        assert javaHome != null;

        UpdaterOptions options = new UpdaterOptions.UpdaterOptionsBuilder().withJavaPath(javaHome.resolve("bin").resolve("java").toAbsolutePath().toString()).build();
        Path versionForgeDir = getInstanceDirectory(currentInstance.getName());

        FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanillaVersion)
                .withModLoaderVersion(forge)
                .withUpdaterOptions(options)
                .withLogger(logger)
                .withProgressCallback(callback)
                .build();

        updater.update(versionForgeDir);
        JavaUtil.setJavaCommand(null);
        System.setProperty("java.home", javaHome.toString());
        startGame(vanillaVersion.getName(), forge.getModLoaderVersion());
    }


    public void handleFabricUpdate(String version, IProgressCallback callback, boolean sodium) throws Exception
    {
        VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder().withName(version).build();

        FabricVersion fabric;

        if(!sodium)
        {
            fabric = new FabricVersionBuilder()
                    .build();
        }
        else
        {
            final List<CurseFileInfo> modInfos = new ArrayList<>();

            int fileID = curseForgeFetcher.getLatestModFile(394468, "Fabric", currentInstance.getVersion());

            modInfos.add(new CurseFileInfo(394468, fileID)); // IronChest

            fabric = new FabricVersionBuilder()
                    .withCurseMods(modInfos)
                    .build();
        }

        Path javaHome = null;

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.21"), Version.gen("1.21.4")))
        {
            javaHome = JavaDownloader.downloadJavaVersion("21");
        }

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.19"), Version.gen("1.20.6")))
        {
            javaHome = JavaDownloader.downloadJavaVersion("17");
        }

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.17"), Version.gen("1.17.1")))
        {
            javaHome = JavaDownloader.downloadJavaVersion("16");
        }

        if (Version.gen(version).isBetweenOrEqual(Version.gen("1.14"), Version.gen("1.16.5")))
        {
            javaHome = JavaDownloader.downloadJavaVersion("8");
        }

        assert javaHome != null;

        UpdaterOptions options = new UpdaterOptions.UpdaterOptionsBuilder().withJavaPath(javaHome.resolve("bin").resolve("java").toAbsolutePath().toString()).build();
        Path versionFabricDir = getInstanceDirectory(currentInstance.getName());

        FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanillaVersion)
                .withModLoaderVersion(fabric)
                .withUpdaterOptions(options)
                .withLogger(logger)
                .withProgressCallback(callback)
                .build();

        updater.update(versionFabricDir);
        JavaUtil.setJavaCommand(null);
        System.setProperty("java.home", javaHome.toString());
        startGame(vanillaVersion.getName(), fabric.getModLoaderVersion());
    }

    private boolean isOldForge(String version)
    {
        return Version.gen(version).isBetweenOrEqual(Version.gen("1.8"), Version.gen("1.11.2"));
    }

    private boolean doesTweak(String version)
    {
        return Version.gen(version).isBetweenOrEqual(Version.gen("1.7"), Version.gen("1.10"));
    }

    private NoFramework createNoFramework(Path versionDir)
    {

        NoFramework noFramework;

        if(Version.gen(currentInstance.getVersion()).isBetweenOrEqual(Version.gen("1.19"), Version.gen("1.21.4")))
        {
            noFramework = new NoFramework(
                    versionDir,
                    Launcher.getInstance().getAuthInfos(),
                    GameFolder.FLOW_UPDATER_1_19_SUP);
        }
        else
        {
            noFramework = new NoFramework(
                    versionDir,
                    Launcher.getInstance().getAuthInfos(),
                    GameFolder.FLOW_UPDATER);
        }

        noFramework.getAdditionalVmArgs().add(getRamArgsFromSaver());
        return noFramework;
    }

    public String getRamArgsFromSaver()
    {
        int val = 1024;
        try {
            if (saver.get("maxRam") != null) {
                val = Integer.parseInt(saver.get("maxRam"));
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException error) {
            saver.set("maxRam", String.valueOf(val));
            saver.save();
        }

        return "-Xmx" + val + "M";
    }

    private Path getInstanceDirectory(String versionDirName) throws IOException
    {
        Path instanceDir = Paths.get(Launcher.getInstance().getInstanceDir().toString(), versionDirName);
        if (!Files.exists(instanceDir)) {
            Files.createDirectory(instanceDir);
        } else {
        }
        return instanceDir;
    }

}