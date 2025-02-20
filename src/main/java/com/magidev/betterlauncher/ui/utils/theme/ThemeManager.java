package com.magidev.betterlauncher.ui.utils.theme;

import java.util.HashMap;
import java.util.Map;

public class ThemeManager {
    private static final Map<String, Theme> themes = new HashMap<>();
    private static Theme currentTheme;

    static {
        addTheme("Purple", new String[]{"#FFDFEF", "#EABDE6", "#D69ADE", "#AA60C8"});
    }

    private static void addTheme(String name, String[] colors) {
        if (colors == null || colors.length < 4) {
            throw new IllegalArgumentException("A theme must have exactly 4 colors.");
        }
        Theme theme = new Theme(name, colors[0], colors[1], colors[2], colors[3]);
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
}
