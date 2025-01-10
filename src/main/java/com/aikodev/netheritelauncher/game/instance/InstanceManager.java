package com.aikodev.netheritelauncher.game.instance;

import com.aikodev.netheritelauncher.Launcher;
import com.aikodev.netheritelauncher.game.mod.ModLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class InstanceManager {
    private static InstanceManager instance;
    private static final List<Instance> instances = new ArrayList<>();
    private static final String INSTANCES_FILE = Launcher.getInstance().getInstanceDir() + "/instances.json";

    public InstanceManager() {
        loadInstances(); // Charger les instances au démarrage

        System.out.println(INSTANCES_FILE);
    }

    public static List<Instance> getInstances() {
        return instances;
    }

    public static InstanceManager getInstance() {
        if (instance == null) {
            instance = new InstanceManager();
        }
        return instance;
    }

    public static Instance getInstanceByName(String name) {
        for (Instance instance : instances) {
            if (instance.getName().equals(name)) {
                return instance;
            }
        }
        return null;
    }

    public static boolean removeInstance(Instance instance) {
        Path instancePath = instance.getInstanceDir(); // Récupérer le chemin
        try {
            // Supprimer le répertoire ou le fichier
            if (Files.exists(instancePath)) {
                Files.walk(instancePath) // Parcourir tous les fichiers et dossiers
                        .sorted((p1, p2) -> p2.compareTo(p1)) // Supprimer les fichiers avant les dossiers
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                System.err.println("Erreur lors de la suppression de : " + path + " - " + e.getMessage());
                            }
                        });
            }
        } catch (IOException e) {
            System.err.println("Impossible de supprimer l'instance : " + e.getMessage());
            return false; // Échec si la suppression échoue
        }

        // Supprimer l'instance de la liste
        boolean removed = instances.remove(instance);
        if (removed) saveInstances(); // Sauvegarder après suppression
        return removed;
    }

    public static boolean addInstance(Instance instance) {
        boolean added = instances.add(instance);
        if (added) saveInstances(); // Sauvegarder après ajout
        return added;
    }

    public static void saveInstances() {
        JSONArray instancesArray = new JSONArray();
        for (Instance instance : instances) {
            JSONObject instanceJson = new JSONObject();
            instanceJson.put("name", instance.getName());
            instanceJson.put("version", instance.getVersion());
            instanceJson.put("modLoader", instance.getModLoader().name());
            instancesArray.put(instanceJson);
        }

        try (FileWriter writer = new FileWriter(INSTANCES_FILE)) {
            writer.write(instancesArray.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadInstances() {
        File file = new File(INSTANCES_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            JSONArray instancesArray = new JSONArray(json.toString());
            for (int i = 0; i < instancesArray.length(); i++) {
                JSONObject instanceJson = instancesArray.getJSONObject(i);
                String name = instanceJson.getString("name");
                String version = instanceJson.getString("version");
                ModLoader modLoader = ModLoader.valueOf(instanceJson.getString("modLoader"));

                Instance instance = new Instance(name, version, modLoader);
                instances.add(instance);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
