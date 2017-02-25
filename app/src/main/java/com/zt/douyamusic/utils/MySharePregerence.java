package com.zt.douyamusic.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/1/25.
 * 数据存储类
 */
public class MySharePregerence {
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;

    private int index;//音乐角标

    String textProgress;
    String textMax;
    String songName;
    int seeKbarCurrentposition ;
    int seeKbarSize ;

    private int player_moudle;//播放模式
    private int clickTime;
    private boolean love_static;

    //经过验证 多次创建sp对象在手机存储里其实操作的就只是他一个对象（集不会在创建一个xml文件）
    public MySharePregerence(Context context) {
         sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
         edit = sp.edit();
    }

    public boolean isLove_static() {
        return sp.getBoolean("love_static",false);
    }

    public void setLove_static(boolean love_static) {
        edit.putBoolean("love_static", love_static);
        edit.commit();
    }

    public int getClickTime() {
        return sp.getInt("clickTime",1);
    }

    public void setClickTime(int clickTime) {
        edit.putInt("clickTime", clickTime);
        edit.commit();
    }

    public int getPlayer_moudle() {
        return sp.getInt("player_moudle",1);//默认为随机播放
    }

    public void setPlayer_moudle(int player_moudle) {
        edit.putInt("player_moudle", player_moudle);
        edit.commit();
    }

    public int getIndex() {
        return sp.getInt("index",0);
    }

    public void setIndex(int index) {
        edit.putInt("index", index);
        edit.commit();
    }

    public int getSeeKbarSize() {
        return sp.getInt("seekBarsize",0);
    }

    public void setSeeKbarSize(int seeKbarSize) {
        edit.putInt("seekBarsize", seeKbarSize);
        edit.commit();
    }

    public String getTextProgress() {
        return sp.getString("textProgress", "00:00");
    }

    public void setTextProgress(String textProgress) {
        edit.putString("textProgress", textProgress);
        edit.commit();
    }

    public String getTextMax() {
        return sp.getString("textMax", "00:00");
    }

    public void setTextMax(String textMax) {
        edit.putString("textMax", textMax);
        edit.commit();
    }

    public String getSongName() {
        return sp.getString("songName", "歌名");
    }

    public void setSongName(String songName) {
        edit.putString("songName", songName);
        edit.commit();
    }

    public int getSeeKbarCurrentposition() {
        return sp.getInt("seekBarProstion",0);
    }

    public void setSeeKbarCurrentposition(int seeKbarCurrentposition) {
        edit.putInt("seekBarProstion", seeKbarCurrentposition);
        edit.commit();
    }
}
