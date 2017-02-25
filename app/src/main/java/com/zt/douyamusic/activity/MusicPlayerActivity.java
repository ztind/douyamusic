package com.zt.douyamusic.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zt.douyamusic.R;
import com.zt.douyamusic.entity.Music;
import com.zt.douyamusic.musicProvider.MusicPrivider;
import com.zt.douyamusic.service.MusicPlayerService;
import com.zt.douyamusic.utils.MySharePregerence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2016/1/25.
 * 音乐播放界面
 *  实现功能：
 * 1，歌名的更新
 * 2，进度的更新和滑动快退键
 * 3，播放/暂停按钮的切换
 */
public class MusicPlayerActivity extends BaseActivity implements View.OnClickListener{
    public static TextView songname,songAlter;
    private SeekBar seekBar;
    private TextView topTextView;
    private ImageView player_image_static,back_image;
    private ImageView up_image,play_image, next_image;
    private TextView currentPosition,musicSize;
    private MySharePregerence sp;
    private ArrayList<Music> list;
    private MusicPrivider musicpd;
    private ImageView love;
    private MyApplycation myapp;//***************应用程序类对象 ，存放公共的类或数据
   // private LoveMusicDb loveMusicDb;//生成数据库类
    // 【因为用户可能多次进入此activity所以不能再onCreate()里创建LoveMusicDb对象，否则会多次创建数据库，而我们只要一个数据库】

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.musicplayer_layout);
        findAllData();
    }
    //获取布局里的所有控件
    private void findAllData() {
        //获取自定义的应用程序类对象
        myapp = (MyApplycation)getApplication();

        //动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.zt.servicebr.progress");
        registerReceiver(new MyRecriver(), filter);

        topTextView = (TextView) findViewById(R.id.musicplayer_topText);
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT){
            topTextView.setVisibility(View.GONE);
        }
        songname = (TextView) findViewById(R.id.current_musicn_name);
        songAlter = (TextView) findViewById(R.id.current_musicn_alter);

        currentPosition = (TextView) findViewById(R.id.currentPosition);//当前音乐播放进度
        musicSize = (TextView) findViewById(R.id.musicSize);//音乐时长
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent = new Intent("com.zt.seekBarProgress");
                intent.putExtra("seekBarCurrrentProgress", seekBar.getProgress());
                sendBroadcast(intent);//停止滑动时发送广播
            }
        });

        player_image_static = (ImageView) findViewById(R.id.player_static_image);//顺序..播放图片

        up_image = (ImageView) findViewById(R.id.music_upsong);
        play_image = (ImageView) findViewById(R.id.music_playsong);
        next_image = (ImageView) findViewById(R.id.music_nextsong);
        back_image = (ImageView) findViewById(R.id.back);
        love = (ImageView) findViewById(R.id.love_iamge);//爱心
        //注册监听器
        player_image_static.setOnClickListener(this);
        up_image.setOnClickListener(this);
        play_image.setOnClickListener(this);
        next_image.setOnClickListener(this);
        back_image.setOnClickListener(this);
        love.setOnClickListener(this);

        sp = new MySharePregerence(this);


        show();//进入界面时更新UI


    }

    /**
     * 定义广播接收者来接收数据
     */
    SimpleDateFormat sdf1 = new SimpleDateFormat("mm:ss");
    SimpleDateFormat sdf2 = new SimpleDateFormat("mm:ss");

    Date date1 = new Date();
    Date date2 = new Date();

    public String progress;
    public String max;
    public String name;
    public int currentposition ;
    public int musicsize ;

    public int index;

   public  class MyRecriver extends BroadcastReceiver{

        String progress;
        String max;
        String name;
        int currentposition ;
        int musicsize ;

        int index;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//获取频道
            if (action.equals("com.zt.servicebr.progress")) {

                currentposition = intent.getIntExtra("currentposition",0);
                musicsize = intent.getIntExtra("musicsize", 0);
                index = intent.getIntExtra("index", 0);
                name = intent.getStringExtra("musicName");

                date1.setTime(currentposition);
                progress = sdf1.format(date1);//进度

                date2.setTime(musicsize);
                max = sdf2.format(date2);//时长

                seekBar.setMax(musicsize);//先确定大小在设置进度值
                seekBar.setProgress(currentposition);


                currentPosition.setText(progress);
                musicSize.setText(max);

            }
            //在内部存储（如果是在onDestory方法里最后存储的话，但用户在暂停音乐后退出，又回到暂停界面，此时又退出时，其成员属性为空。所以存储的sp属性就为空啦）
            //  sp.setSongName(name); // 有点操蛋
            sp.setSeeKbarCurrentposition(currentposition);
            sp.setSeeKbarSize(musicsize);

            sp.setTextProgress(progress);
            sp.setTextMax(max);

        }
    }
    private void show() {//恢复播放数据
        musicpd = new MusicPrivider(this);
        list = musicpd.getAllMusic();

        if(list==null || list.size()<=0){
            return;
        }

        int position = this.getIntent().getIntExtra("fposition", 0);

        songname.setText(list.get(position).getName());//歌名 or songname.setText(list.get(MusicPlayerService.index).getName());//歌名
        songAlter.setText(list.get(position).getAlt());
        //***********注意seekBar控件要先确定大小，在进行进度设置，否则进度不会更新**************
        seekBar.setMax(sp.getSeeKbarSize());
        seekBar.setProgress(sp.getSeeKbarCurrentposition());


        currentPosition.setText(sp.getTextProgress());
        musicSize.setText(sp.getTextMax());

        //根据音乐的播放状态来切换播放图标
        if (MusicPlayerService.mp != null && MusicPlayerService.mp.isPlaying()) {
            play_image.setImageResource(R.mipmap.player);
        } else {
            play_image.setImageResource(R.mipmap.stopsong);
        }

        //根据设置的播放模式来更新 图标
        if(sp.getPlayer_moudle()==1){
            player_image_static.setImageResource(R.mipmap.order);
        }else if(sp.getPlayer_moudle()==2){
            player_image_static.setImageResource(R.mipmap.single);
        }else if(sp.getPlayer_moudle()==3){
            player_image_static.setImageResource(R.mipmap.random);
        }
        //判断爱心图片
        checkMusicisLove();
        //  Toast.makeText(this, sp.getSongName(), Toast.LENGTH_SHORT).show();
    }

    private void checkMusicisLove(){
        Music mp3  = list.get(MusicPlayerService.index);
        try {
            Music lovemusci = myapp.dbUtils.findFirst(Selector.from(Music.class).where("loveid", "=", mp3.getId()));//根据新增的字段来查询
            if(lovemusci==null){ //没有添加到数据库，就要添加
                love.setImageResource(R.mipmap.xin_bai);
            }else{
                int islove = lovemusci.getIsLove();
                if(islove==1){
                    love.setImageResource(R.mipmap.xin_hong);
                }else{
                    love.setImageResource(R.mipmap.xin_bai);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int ckciktiem =sp.getClickTime()+1;
        switch (id) {
            case R.id.player_static_image://设置音乐播放的模式，1 顺序播放（默认），2 单曲循环，3 随机播放

                if(ckciktiem==1) {
                    Toast.makeText(this, R.string.order_play, Toast.LENGTH_SHORT).show();
                    sp.setPlayer_moudle(1);
                    player_image_static.setImageResource(R.mipmap.order);
                    sp.setClickTime(ckciktiem);
                }else if(ckciktiem==2){
                    Toast.makeText(this, R.string.single_play, Toast.LENGTH_SHORT).show();
                    sp.setPlayer_moudle(2);
                    player_image_static.setImageResource(R.mipmap.single);
                    sp.setClickTime(ckciktiem);

                }else if(ckciktiem==3){
                    Toast.makeText(this, R.string.random_play, Toast.LENGTH_SHORT).show();
                    sp.setPlayer_moudle(3);
                    player_image_static.setImageResource(R.mipmap.random);
                    sp.setClickTime(ckciktiem);
                }

                if(sp.getClickTime()==3){
                    sp.setClickTime(0);
                }
                return ;//结束方法
            case R.id.back:
                this.finish();
                return;
            case R.id.love_iamge://添加喜爱（从数据库里添加/删除）

                Music music  = list.get(MusicPlayerService.index);

                try {
                    Music loveMusic = myapp.dbUtils.findFirst(Selector.from(Music.class).where("loveid", "=", music.getId()));

                    if(loveMusic==null){

                 music.setLoveid(music.getId());//注意是为music赋值，不是为loveMusic赋值，因为他是null的【且封装对象的属性不能为数据库的关键字】
                        music.setIsLove(1);//设置为喜爱

                        myapp.dbUtils.save(music); //保存的是music对象哦哦 *******重要*******
                        love.setImageResource(R.mipmap.xin_hong);
                        Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                    }else {
                        int islove = loveMusic.getIsLove();

                        if (islove==1) {
                            loveMusic.setIsLove(0);//移除喜爱
                            love.setImageResource(R.mipmap.xin_bai);
                            Toast.makeText(this, "移除收藏", Toast.LENGTH_SHORT).show();
                        }else{
                            loveMusic.setIsLove(1);//添加喜爱
                            love.setImageResource(R.mipmap.xin_hong);
                            Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                        }

                        myapp.dbUtils.update(loveMusic,"isLove");//更新loveMusic这一条音乐的字段记录

                    }

                } catch (DbException e) {
                    e.printStackTrace();
                }


                return ;
            case R.id.music_upsong:

                MusicPlayerService.upSong();//上一首

                checkMusicisLove();

                //更新歌名，按钮
                play_image.setImageResource(R.mipmap.player);
                songname.setText(list.get(MusicPlayerService.index).getName());
                songAlter.setText(list.get(MusicPlayerService.index).getAlt());

                break;
            case R.id.music_playsong:
                Intent intent2 = new Intent(this,MusicPlayerService.class);
                if((MusicPlayerService.mp!=null) && MusicPlayerService.mp.isPlaying()){ //根据音乐的播放状态来切换播放图标
                    play_image.setImageResource(R.mipmap.stopsong);
                    intent2.putExtra("player",2);
                }else if((MusicPlayerService.mp!=null) && !MusicPlayerService.mp.isPlaying()){
                    play_image.setImageResource(R.mipmap.player);
                    intent2.putExtra("player", 1);
                }
                startService(intent2);
                break;
            case R.id.music_nextsong:

                MusicPlayerService.nextSong();//下一首

                checkMusicisLove();

                //更新歌名，按钮
                play_image.setImageResource(R.mipmap.player);
                songname.setText(list.get(MusicPlayerService.index).getName());
                songAlter.setText(list.get(MusicPlayerService.index).getAlt());
                break;
        }
    }

}
