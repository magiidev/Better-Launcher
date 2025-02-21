package com.magidev.betterlauncher.ui.utils.theme;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ThemeManager {
    private static final Map<String, Theme> themes = new HashMap<>();
    public static Theme currentTheme;

    static {
        addTheme("Coffee");
        addTheme("Ocean");
        addTheme("Grass");
        addTheme("Blackberry");
    }

    private static void addTheme(String name) {
        Theme theme = new Theme(name);
        themes.put(name, theme);
        if (currentTheme == null) {
            currentTheme = theme; // Set first added theme as default
        }
    }

    public static Theme getTheme(String name) {
        return themes.get(name);
    }

    public static void setCurrentTheme(String name) {
        if (themes.containsKey(name)) {
            currentTheme = themes.get(name);
        } else {
            throw new IllegalArgumentException("Theme not found: " + name);
        }
    }

    public static Theme getCurrentTheme() {
        return currentTheme;
    }

    public static Set<String> getAvailableThemes() {
        return themes.keySet();
    }
}