package com.zt.douyamusic.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.zt.douyamusic.activity.MusicPlayerActivity;
import com.zt.douyamusic.entity.Music;
import com.zt.douyamusic.fragment.FirstFragment;
import com.zt.douyamusic.musicProvider.MusicPrivider;
import com.zt.douyamusic.utils.MySharePregerence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * 后台音乐播放服务类
 * 实现方法：
 * 1，播放，暂停，上一首，下一首，快进 快退，还有多种播放模式的设置（其标记值存储在sp中，因为设置过一次下次启动时，不变）
 * 2，实现三个监听器 异步准备 播放错误 播放完成监听接口。记住还要注册哦
 */
public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    public static MediaPlayer mp;
    public static ArrayList<Music> list;
    private MusicPrivider musicprovider;
    private static final int ORDER_MOUDLE = 1;  //顺序播放
    private static final int SINGAL_MOUDLE = 2; //单曲循环
    private static final int RANDLM_MOUDLE = 3; //随机播放
    private MySharePregerence sp;
    public  static boolean actFlage;

    @Override

    public void onCreate() { //创建服务的生命周期方法，只执行一次
        super.onCreate();
        mp = new MediaPlayer();//在服务初始化方法中创建播放器模板对象
        musicprovider = new MusicPrivider(getApplicationContext());
        list = musicprovider.getAllMusic();//获取音乐集合
        mp.setOnPreparedListener(this);
        mp.setOnErrorListener(this);
        mp.setOnCompletionListener(this);

        sp = new MySharePregerence(getApplicationContext());

        //注册广播接收者
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.zt.seekBarProgress");
        registerReceiver(new MyServiceBroadcast(), filter);
    }

    @Override
    public IBinder onBind(Intent intent) {//此方法用于绑定服务时用，和android接口回调是使用
        return null;
    }

    public static int index;//音乐角标【设置为公共的静态属性，最核心】

    public static int aaa = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//执行任务的方法 多次执行
        //根据action的标记值来做播放选择


        if(MusicPlayerService.list==null || MusicPlayerService.list.size()<=0) {
            return 0;
        }
        int state = intent.getIntExtra("player", 0);
        switch (state) {
            case 1:
                if (aaa == 1) {
                    play(index);//播放,
                    aaa = 2;
                } else {
                    onPrepared(mp);//从暂停处开始播放
                }
                break;
            case 2:
                pause();//暂停
                break;
            case 3:
                upSong();//上一首
                break;
            case 4:
                nextSong();//下一首
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onPrepared(MediaPlayer mp) { //开启播放mp.startt()的所在地
        mp.start();//播放
        new MyMusicThread().start();//开启线程发送进度值
    }

    @Override
    public void onCompletion(MediaPlayer mp) {//用于音乐播放完成的执行地

        //playerMoudle为播放模式 1为顺序播放(默认) 2为单曲循环 3为随机播放

        switch (sp.getPlayer_moudle()) { //同过sp获得
            case ORDER_MOUDLE:
                nextSong();//顺序播放也就是一直下一首，播完最后一首又从头开始播放

                break;
            case SINGAL_MOUDLE:
                play(index);//单曲

                break;
            case RANDLM_MOUDLE:
                randomPlayer();//随机播放
                break;
        }
        //更新歌名 歌手
        FirstFragment.songName.setText(list.get(index).getName());
        FirstFragment.songAlter.setText(list.get(index).getAlt());
        //(此设置，可能会包空指针异常,因为MusicPlayerActivity界面没有点开创建。所以要先创建一个,或者是不为null)
        if (MusicPlayerActivity.songname != null) {
            MusicPlayerActivity.songname.setText(list.get(index).getName());
            MusicPlayerActivity.songAlter.setText(list.get(MusicPlayerService.index).getAlt());
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {//播放过程中出现异常或错误的执行地
        if (mp.isPlaying()) {
            mp.stop();
        }
        mp.reset();
        return false;
    }

    /**
     * 创建一个线程类用来发送音乐播放进度数据(在准备播放方法中 开启此线程)
     */
    class MyMusicThread extends Thread {
        @Override
        public void run() {
            while (mp != null && mp.isPlaying()) {//播放时菜发送进度值，注意要睡眠sleep否则 接收者忙不过来，导致界面卡死
                try {
                    Thread.sleep(1000);
                    Intent intent = new Intent("com.zt.servicebr.progress");//频道
                    intent.putExtra("currentposition", mp.getCurrentPosition());
                    intent.putExtra("musicsize", mp.getDuration());
                    intent.putExtra("musicName", list.get(index).getName().toString());//歌名
                    intent.putExtra("index", index);
                    sendBroadcast(intent);//发送
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //创建一个广播接收者
    class MyServiceBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.zt.seekBarProgress")) {
                int cpro = intent.getIntExtra("seekBarCurrrentProgress", 0);
                if (mp != null) {
                    mp.seekTo(cpro);//设置到播放进度里
                }
            }
        }
    }

    //播放音乐的方法
    public static void play(int index) {


        String path = list.get(index).getUrl();//获取音乐文件的数据路径
        if (mp != null) {
            mp.reset();//重置
            try {
                mp.setDataSource(path);
                mp.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //播放音乐的方法
    public static int playByString(String name) {
        String path = null;
        for (int i = 0; i < list.size(); i++) {

            if ((list.get(i).getName()).equals(name)) {
                index = i;
                path = list.get(i).getUrl();
                break;
            }
        }
        if (mp != null && path != null) {
            mp.reset();//重置
            try {
                mp.setDataSource(path);
                mp.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return index;
    }

    //暂停音乐的方法
    public static void pause() {
        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    //上一首
    public static void upSong() {
        if (index >= 0 && index < list.size()) {
            if (index == 0) {
                index = list.size() - 1;
            } else {
                index--;
            }
        }
        play(index);
    }

    //下一首
    public static void nextSong() {
        if (index >= 0 && index < list.size()) {
            index++;
        }
        if (index == list.size()) {
            index = 0;
        }
        play(index);
    }

    @Override
    public void onDestroy() {//服务销毁的方法，执行一次
        super.onDestroy();
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }
            mp.release();//释放播放模板
        }
    }

    //随机播放的方法
    private void randomPlayer() {
        Random random = new Random();
        index = random.nextInt(list.size()); //产生0~list.size()-1 大的随机数
        play(index);
    }

}
