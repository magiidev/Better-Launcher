package com.magidev.betterlauncher.game.instance;

import com.magidev.betterlauncher.Launcher;
import com.magidev.betterlauncher.game.mod.ModLoader;
import fr.flowarg.flowupdater.download.json.CurseFileInfo;
import fr.flowarg.flowupdater.download.json.Mod;
import fr.flowarg.flowupdater.integrations.curseforgeintegration.CurseForgeIntegration;
import fr.flowarg.flowupdater.utils.IOUtils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Instance
{
    private String name;
    private String version;
    private ModLoader modloader;

    public Instance(String name, String version, ModLoader modloader)
    {
        this.name = name;
        this.version = version;
        this.modloader = modloader;
    }

    public void downloadCurseMod(Mod mod) throws Exception
    {
        if (!Files.exists(Paths.get(getInstanceDir().toString(), "/mods/" + mod.getName())))
        {
            IOUtils.download(Launcher.getInstance().getLogger(), new URL(mod.getDownloadURL()), Paths.get(getInstanceDir().toString(), "/mods/" + mod.getName()));
        }
        else
        {
            System.out.println(mod.getName() + " already exists");
        }
    }

    public void downloadCurseMod(CurseFileInfo curseFileInfo) throws Exception
    {
        CurseForgeIntegration cfi = new CurseForgeIntegration(Launcher.getInstance().getLogger(), Paths.get(getInstanceDir().toString(), "/mods"));
        Mod mod = cfi.fetchMod(curseFileInfo);

        if (!Files.exists(Paths.get(getInstanceDir().toString(), "/mods/" + mod.getName())))
        {
            IOUtils.download(Launcher.getInstance().getLogger(), new URL(mod.getDownloadURL()), Paths.get(getInstanceDir().toString(), "/mods/" + mod.getName()));
        }
        else
        {
            System.out.println(mod.getName() + " already exists");
        }
    }

    public String getName()
    {
        return name;
    }

    public ModLoader getModLoader()
    {
        return modloader;
    }

    public String getVersion()
    {
        return version;
    }

    public Path getInstanceDir()
    {
        return Paths.get(Launcher.getInstance().getInstanceDir().toString(), this.name);
    }
}
