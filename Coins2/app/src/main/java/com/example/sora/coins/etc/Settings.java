package com.example.sora.coins.etc;

/**
 * Created by sora on 2016-04-14.
 */
public class Settings {

    private static Settings settings;

    static boolean Login = false;

    private Settings() {}

    public static synchronized Settings getInstance() {
        if(settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    public boolean getLogin() {
        return Login;
    }

    public void setLogin(boolean bool) {
        Login = bool;
    }
}
