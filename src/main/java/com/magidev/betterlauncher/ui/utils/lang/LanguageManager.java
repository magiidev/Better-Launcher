package com.magidev.betterlauncher.ui.utils.lang;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private static final Map<String, Language> languages = new HashMap<>();
    public static Language currentLanguage;

    static {
        // Charge les langues au démarrage
        addLanguage("English", "lang/english.properties");
        addLanguage("French", "lang/french.properties");
        addLanguage("Spanish", "lang/spanish.properties");
    }

    private static void addLanguage(String name, String resourcePath) {
        try {
            Language lang = new Language(name, resourcePath); // Correction ici
            languages.put(name, lang);
            if (currentLanguage == null) {
                currentLanguage = lang; // Définit la première langue comme langue par défaut
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la langue: " + name + " (" + resourcePath + ")");
            e.printStackTrace();
        }
    }

    public static void setLanguage(String name) {
        if (languages.containsKey(name)) {
            currentLanguage = languages.get(name);
        } else {
            System.err.println("Langue non trouvée: " + name);
        }
    }

    public static String get(String key) {
        if (currentLanguage == null) {
            return "No language selected";
        }
        return currentLanguage.getTranslation(key);
    }

    public static String[] getAvailableLanguages() {
        return languages.keySet().toArray(new String[0]);
    }

    public static Language getLanguageByName(String name) {
        return languages.getOrDefault(name, null);
    }
}
