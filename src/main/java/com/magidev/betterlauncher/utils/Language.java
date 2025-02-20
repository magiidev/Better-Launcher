package com.magidev.betterlauncher.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Language {
    private String name;
    private Properties translations;

    public Language(String name, String filePath) throws IOException {
        this.name = name;
        this.translations = new Properties();
        loadTranslations(filePath);
    }

    private void loadTranslations(String filePath) throws IOException {
        try (FileInputStream input = new FileInputStream(filePath)) {
            translations.load(input);
        }
    }

    public String getName() {
        return name;
    }

    public String getTranslation(String key) {
        return translations.getProperty(key, "Not found");
    }
}
