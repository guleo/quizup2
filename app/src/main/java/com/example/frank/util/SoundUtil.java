package com.example.frank.util;

import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.example.frank.test.R;
import com.example.model.SetEntity;
import org.cocos2d.sound.SoundEngine;

/**
 * Created by frank on 2016/2/1.
 */
public class SoundUtil {
    //音乐相关
    public static final int MUSIC_BACKGROUND = R.raw.match;
    public static final int MUSIC_BATTLE = R.raw.battle;

    //音效相关
    public static final int EFFECT_PASS = R.raw.pass;
    public static final int EFFECT_LOSE = R.raw.lose;
    public static final int EFFECT_MENU = R.raw.menuenter;
    public static final int EFFECT_BUTTON = R.raw.button;
    public static final int EFFECT_SELECT = R.raw.select;

    //播放音乐
    public static final void playMusic(Context context, SetEntity entity) {
        if (entity != null && entity.isSoundOn()) {
            switch (Utils.getTopActivity(context)) {
                case "MatchRandActivity":
                    SoundEngine.sharedEngine()
                            .playSound(context, SoundUtil.MUSIC_BATTLE, true);
                    break;
                case "MatchExtActivity":
                    SoundEngine.sharedEngine()
                            .playSound(context, SoundUtil.MUSIC_BATTLE, true);
                    break;
                default:
                    SoundEngine.sharedEngine().playSound(context, SoundUtil.MUSIC_BACKGROUND, true);
                    break;
            }
        }
    }

    //暂停音乐
    public static final void pauseMusic(Context context, SetEntity entity) {
        if (!entity.isSoundOn())
            SoundEngine.sharedEngine().pauseSound();
    }

    //继续音乐
    public static final void resumeMusic(Context context, SetEntity entity) {
        if (entity.isSoundOn())
            SoundEngine.sharedEngine().resumeSound();
    }

    public static final void stopMusic() {
        SoundEngine.sharedEngine().pauseSound();
    }

    //播放音效
    public static final void playEffect(Context context, SetEntity entity,View view) {
        if (entity.isEffectOn()) {
            if (view instanceof ImageButton || view instanceof Button)
                SoundEngine.sharedEngine().playEffect(context, SoundUtil.EFFECT_BUTTON);
            else
                SoundEngine.sharedEngine().playEffect(context, SoundUtil.EFFECT_SELECT);
        }
    }

    //设置振动
    public static final void playViberate(Context context, SetEntity entity) {
        if (entity.isViberateOn()) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }
    }
}
