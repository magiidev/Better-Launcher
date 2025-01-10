package com.aikodev.netheritelauncher.game.fetchers;

import com.aikodev.netheritelauncher.Launcher;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.flowarg.flowlogger.ILogger;
import javafx.scene.control.Alert;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VersionFetcher {

    private static final String FORGE_PROMOTIONS_URL = "https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json";
    private static final String MOJANG_VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";

    public static String getLatestForgeVersion(String minecraftVersion) {
        try {
            String response = fetchUrlContent(FORGE_PROMOTIONS_URL);
            if (response == null) return null;

            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            JsonObject promos = jsonObject.getAsJsonObject("promos");

            String versionKey = minecraftVersion + "-latest";
            if (promos.has(versionKey)) {
                return minecraftVersion + "-" + promos.get(versionKey).getAsString();
            } else {
                System.out.println("Minecraft version " + minecraftVersion + " not found in promotions.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> fetchVersions() {
        ILogger logger = Launcher.getInstance().getLogger();
        List<String> versions = new ArrayList<>();

        try {
            String response = fetchUrlContent(MOJANG_VERSION_MANIFEST_URL);
            if (response != null) {
                parseAndAddVersions(response, versions);
            }
        } catch (Exception e) {
            handleFetchError(logger, e, versions);
        }
        return versions;
    }

    private static String fetchUrlContent(String urlString) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                return content.toString();
            }
        }
        return null;
    }

    private static void parseAndAddVersions(String jsonContent, List<String> versions) {
        JSONObject jsonObj = new JSONObject(jsonContent);
        JSONArray versionsArray = jsonObj.getJSONArray("versions");

        for (int i = 0; i < versionsArray.length(); i++) {
            JSONObject version = versionsArray.getJSONObject(i);
            if ("release".equals(version.getString("type"))) {
                versions.add(version.getString("id"));
            }
        }
    }

    private static void handleFetchError(ILogger logger, Exception e, List<String> versions) {
        logger.err(e.toString());
        new Alert(Alert.AlertType.ERROR, "Failed to fetch versions. Attempting local download.").showAndWait();

        try {
            String localFilePath = downloadFile(MOJANG_VERSION_MANIFEST_URL, Launcher.getInstance().getLauncherDir() + "/version_manifest.json");
            if (localFilePath != null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(localFilePath))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    parseAndAddVersions(content.toString(), versions);
                }
            }
        } catch (Exception downloadException) {
            logger.err(downloadException.toString());
            new Alert(Alert.AlertType.ERROR, "Failed to fetch versions after download attempt.").showAndWait();
        }
    }


    private static String downloadFile(String urlString, String destinationPath) {
        try (InputStream inputStream = new URL(urlString).openStream();
             FileOutputStream fos = new FileOutputStream(destinationPath)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            return destinationPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}