package com.example.frank.util;

import android.graphics.Color;

/**
 * Created by frank on 2016/1/31.
 */
public class Utils {
    public static final String ACTIVITY_PACKAGE_PATH = "com.example.frank.activity";
    public static final String GAME_TTF = "tu.ttf";
    public static final String SCORE_TTF = "SnapITC.TTF";
    public static final int COLOR_BKG = Color.rgb(47,47,47);
    private boolean soundOn = true;
    private boolean effectOn = true;
    private boolean viberateOn = true;

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }

    public boolean isEffectOn() {
        return effectOn;
    }

    public void setEffectOn(boolean effectOn) {
        this.effectOn = effectOn;
    }

    public boolean isViberateOn() {
        return viberateOn;
    }

    public void setViberationOn(boolean viberationOn) {
        this.viberateOn = viberationOn;
    }
}
