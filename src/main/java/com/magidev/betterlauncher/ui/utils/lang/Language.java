package com.magidev.betterlauncher.ui.utils.lang;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Language {
    private String name;
    private Properties translations;

    public Language(String name, String resourcePath) throws IOException {
        this.name = name;
        this.translations = new Properties();
        loadTranslations(resourcePath);
    }

    private void loadTranslations(String resourcePath) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
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
