package com.zt.douyamusic.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

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
        requsetPermis();
    }

    private void gotoMianActivity() {
        Intent intent=new Intent(this,MainActivity.class);//通过显示意图来跳转
        this.startActivity(intent);
        this.finish();
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void requsetPermis() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},33);
        }else {
            enterAty();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode,grantResults);
    }
    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == 33) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                enterAty();
            } else {
                // Permission Denied
                Toast.makeText(this, "请授予该权限，否则无法正常使用app", Toast.LENGTH_LONG).show();
                requsetPermis();
            }
        }
    }
    private void enterAty(){
        Timer timer = new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                gotoMianActivity();
            }
        };
        timer.schedule(task,2000);//定时2s后跳转到主界面
    }
}