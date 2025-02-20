package com.magidev.betterlauncher.ui.utils.theme;

public class Theme
{
    private String name;

    private String extraLightColor;
    private String lightColor;
    private String mediumColor;
    private String highColor;

    public Theme(String name, String extraLightColor, String lightColor, String mediumColor, String highColor)
    {
        this.name = name;
        this.extraLightColor = extraLightColor;
        this.lightColor = lightColor;
        this.mediumColor = mediumColor;
        this.highColor = highColor;
    }

    public String getName()
    {
        return name;
    }

    public String getExtraLightColor()
    {
        return extraLightColor;
    }

    public String getLightColor()
    {
        return lightColor;
    }

    public String getMediumColor()
    {
        return mediumColor;
    }

    public String getHighColor()
    {
        return highColor;
    }
}
