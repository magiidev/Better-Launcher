package com.magidev.betterlauncher.game.fetchers;

import com.magidev.betterlauncher.game.mod.Mod;
import com.magidev.betterlauncher.game.mod.ModLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CurseForgeFetcher {

    private final String apiKey;
    private final HttpClient client;

    public CurseForgeFetcher(String apiKey) {
        this.apiKey = apiKey;
        this.client = HttpClient.newHttpClient();  // Utilisation d'un client HTTP réutilisable
    }

    private static final String BASE_URL = "https://api.curseforge.com/v1/";

    public int getLatestModFile(int modId, String modLoader, String minecraftVersion) throws Exception {
        String url = BASE_URL + "mods/" + modId + "/files";
        int index = 0;
        int pageSize = 50; // Nombre maximum de fichiers par page (limite API CurseForge)
        JSONObject bestMatch = null;

        while (true) {
            HttpURLConnection conn = (HttpURLConnection) new URL(url + "?index=" + index + "&pageSize=" + pageSize).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("x-api-key", apiKey);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP error: " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            conn.disconnect();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray files = jsonResponse.getJSONArray("data");

            if (files.length() == 0) {
                break; // Pas de fichiers dans cette page, on arrête
            }

            for (int i = 0; i < files.length(); i++) {
                JSONObject file = files.getJSONObject(i);
                JSONArray gameVersions = file.getJSONArray("gameVersions");

                boolean matchesVersion = false;
                boolean matchesLoader = false;

                for (int j = 0; j < gameVersions.length(); j++) {
                    String version = gameVersions.getString(j);
                    if (version.equals(minecraftVersion)) {
                        matchesVersion = true;
                    }
                    if (modLoader != null && version.equalsIgnoreCase(modLoader)) {
                        matchesLoader = true;
                    }
                }

                if (matchesVersion && matchesLoader) {
                    return file.getInt("id");
                }

                if (matchesVersion && bestMatch == null) {
                    bestMatch = file;
                }
            }

            index += pageSize;
        }

        if (bestMatch != null) {
            return bestMatch.getInt("id");
        }

        System.out.println("Mod ID: " + modId);
        System.out.println("Modloader: " + modLoader);
        throw new RuntimeException("Aucun fichier correspondant trouvé.");
    }

    public String getModNameByID(int modId) {
        String url = String.format("https://api.curseforge.com/v1/mods/%s", modId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-api-key", apiKey)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());

                if (jsonResponse.has("data")) {
                    JSONObject data = jsonResponse.getJSONObject("data");

                    return data.optString("name", "Unknown");
                } else {
                }
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<Mod> getModsByLoaderAndVersion(String modLoader, String gameVersion, int startIndex, int pageSize) {
        if (modLoader.equals(ModLoader.FORGE.toString())) {
            modLoader = "1";  // 1 = Forge
        } else if (modLoader.equals(ModLoader.FABRIC.toString())) {
            modLoader = "4";  // 4 = Fabric
        } else if (modLoader.equals(ModLoader.NEOFORGE.toString())) {
            modLoader = "6";  // 6 = NeoForge
        }

        String url = String.format(
                "https://api.curseforge.com/v1/mods/search?gameId=432&classId=6&gameVersion=%s&modLoaderType=%s&sortField=2&sortOrder=desc&index=%d&pageSize=%d",
                gameVersion,
                modLoader,
                startIndex,
                pageSize
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-api-key", apiKey)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        List<Mod> modsList = new ArrayList<>();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());

                if (jsonResponse.has("data")) {
                    JSONArray allMods = jsonResponse.getJSONArray("data");

                    for (int i = 0; i < allMods.length(); i++) {
                        JSONObject mod = allMods.getJSONObject(i);

                        String name = mod.optString("name", "Unknown");
                        String author = mod.optJSONArray("authors") != null && mod.optJSONArray("authors").length() > 0 ?
                                mod.optJSONArray("authors").getJSONObject(0).optString("name", "Unknown") : "Unknown";
                        String description = mod.optString("summary", "No description available.");
                        String iconUrl = mod.has("logo") ? mod.getJSONObject("logo").optString("url", "") : "";

                        int latestFileId = 0;
                        String modLoaderName;

                        if (modLoader.equals("1")) {
                            modLoaderName = "Forge";
                            latestFileId = getLatestModFile(mod.getInt("id"), modLoaderName, gameVersion);
                        } else if (modLoader.equals("4")) {
                            modLoaderName = "Fabric";
                            latestFileId = getLatestModFile(mod.getInt("id"), modLoaderName, gameVersion);
                        } else if (modLoader.equals("6")) {
                            modLoaderName = "NeoForge";
                            latestFileId = getLatestModFile(mod.getInt("id"), modLoaderName, gameVersion);
                        }

                        modsList.add(new Mod(name, mod.getInt("id"), description, author, latestFileId, iconUrl));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return modsList;
    }


    public List<Mod> searchModsByLoaderAndVersionAndKeyword(String modLoader, String gameVersion, String search, int startIndex, int pageSize)
    {
        if (modLoader.equals(ModLoader.FORGE.toString())) {
            modLoader = "1";  // 1 = Forge
        } else if (modLoader.equals(ModLoader.FABRIC.toString())) {
            modLoader = "4";  // 4 = Fabric
        } else if (modLoader.equals(ModLoader.NEOFORGE.toString())) {
            modLoader = "6";  // 6 = NeoForge
        }

        String formattedSearchTerm = search.replace(" ", "+");

        String url = String.format(
                "https://api.curseforge.com/v1/mods/search?gameId=432&index=%d&classId=6&modLoaderType=%s&searchFilter=%s&gameVersion=%s&pageSize=%d&sortField=1&sortOrder=desc",
                startIndex,
                modLoader,
                formattedSearchTerm,
                gameVersion,
                pageSize
        );

        System.out.println(url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-api-key", apiKey)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        List<Mod> modsList = new ArrayList<>();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());

                if (jsonResponse.has("data")) {
                    JSONArray allMods = jsonResponse.getJSONArray("data");

                    for (int i = 0; i < allMods.length(); i++) {
                        JSONObject mod = allMods.getJSONObject(i);

                        String name = mod.optString("name", "Unknown");
                        String author = mod.optJSONArray("authors") != null && mod.optJSONArray("authors").length() > 0 ?
                                mod.optJSONArray("authors").getJSONObject(0).optString("name", "Unknown") : "Unknown";
                        String description = mod.optString("summary", "No description available.");

                        String iconUrl = mod.has("logo") ? mod.getJSONObject("logo").optString("url", "") : "";

                        int latestFileId = 0;

                        String modLoaderName; // Temporary variable to stock the name, or it won't work
                        if (modLoader.equals("1")) {
                            modLoaderName = "Forge";
                            latestFileId = getLatestModFile(mod.getInt("id"), modLoaderName, gameVersion);
                        } else if (modLoader.equals("4")) {
                            modLoaderName = "Fabric";
                            latestFileId = getLatestModFile(mod.getInt("id"), modLoaderName, gameVersion);
                        } else if (modLoader.equals("6")) {
                            modLoaderName = "NeoForge";
                            latestFileId = getLatestModFile(mod.getInt("id"), modLoaderName, gameVersion);
                        }

                        modsList.add(new Mod(name, mod.getInt("id"), description, author, latestFileId, iconUrl));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return modsList;
    }

}