package com.zt.douyamusic.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.zt.douyamusic.activity.MyApplycation;
import com.zt.douyamusic.entity.NetMusic;
import com.zt.douyamusic.down.DownMusic;

/**
 * Created by Administrator on 2016/1/30.
 * 下载音乐对话框类
 */
public class ZdyDialog  {


    public static void showDialog(final Context context,final NetMusic netMusic) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("消息");
        dialog.setCancelable(true);
        dialog.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "正在下载: "+netMusic.getName(), Toast.LENGTH_SHORT).show();
                downMusic(netMusic);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        }).create().show();
    }
    //下载音乐
    private static void downMusic(NetMusic netMusic) {

        DownMusic downMusic = new DownMusic(); //下载类
        downMusic.getDownerStaticInterface(new HH());//注册接口对象

        downMusic.download(netMusic);//下载传入下载对象)

    }
    //接口回调机制
    public static class HH implements DownMusic.DownerStaticInterface{

        @Override
        public void successful(String str) { //下载成功
            Toast.makeText(MyApplycation.context, "消息："+str, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void fail(String error) { //下载失败
            Toast.makeText(MyApplycation.context, "消息："+error, Toast.LENGTH_SHORT).show();

        }
    }
}
