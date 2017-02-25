package com.zt.douyamusic.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.zt.douyamusic.activity.MyApplycation;

/**
 * Created by Administrator on 2016/1/29.
 * 点击搜索图标后隐藏键盘
 */
public class InputListen {
    public static void  hideInput(View view) {
        InputMethodManager imm = (InputMethodManager)MyApplycation.context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm.isActive()) { //键盘是否激活/弹出
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);//隐藏
        }
    }

}
