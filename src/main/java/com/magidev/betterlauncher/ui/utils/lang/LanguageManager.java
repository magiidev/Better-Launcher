package com.magidev.betterlauncher.ui.utils.lang;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private static Map<String, Language> languages = new HashMap<>();
    public static Language currentLanguage;

    static {
        // Automatically initialize languages at startup
        addLanguage("English", "lang/english.properties");
        addLanguage("French", "lang/french.properties");
        addLanguage("Spanish", "lang/spanish.properties");
    }

    private static void addLanguage(String name, String filePath) {
        try {
            Language lang = new Language(name, LanguageManager.class.getClassLoader().getResource(filePath).getPath());
            languages.put(name, lang);
            if (currentLanguage == null) {
                currentLanguage = lang; // Set the first added language as default
            }
        } catch (IOException e) {
        }
    }

    public static void setLanguage(String name) {
        if (languages.containsKey(name)) {
            currentLanguage = languages.get(name);
        } else {
        }
    }

    public static String get(String key) {
        if (currentLanguage == null) {
            return "No language selected";
        }
        return currentLanguage.getTranslation(key);
    }

    // Added method to get available languages
    public static String[] getAvailableLanguages() {
        return languages.keySet().toArray(new String[0]);
    }

    // New method to get Language object by name
    public static Language getLanguageByName(String name) {
        if (languages.containsKey(name)) {
            return languages.get(name);
        } else {
            return null; // or throw an exception if you prefer
        }
    }
}
