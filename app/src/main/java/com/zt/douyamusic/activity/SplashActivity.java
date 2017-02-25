package com.zt.douyamusic.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.zt.douyamusic.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.waps.AppConnect;

/**
 *   1，闪屏页，可以用Thread,Hadler，h还有Timer定时器来完成
 *
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppConnect.getInstance(this);
        Timer timer = new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
               gotoMianActivity();
            }
        };
        timer.schedule(task,2000);//定时2s后跳转到主界面
    }

    private void gotoMianActivity() {
        Intent intent=new Intent(this,MainActivity.class);//通过显示意图来跳转
        this.startActivity(intent);
        this.finish();
    }
}
