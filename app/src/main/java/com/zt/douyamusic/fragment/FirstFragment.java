package com.zt.douyamusic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zt.douyamusic.activity.MusicPlayerActivity;
import com.zt.douyamusic.activity.MyApplycation;
import com.zt.douyamusic.R;
import com.zt.douyamusic.adapter.ListViewAdapter;
import com.zt.douyamusic.entity.Music;
import com.zt.douyamusic.musicProvider.MusicPrivider;
import com.zt.douyamusic.service.MusicPlayerService;
import com.zt.douyamusic.utils.MySharePregerence;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/24.
 */
public class FirstFragment extends Fragment implements View.OnClickListener{//设置viewpager里 v4包下的
    private ListView listview;
    private ImageView imageview;
    public static TextView songAlter,songName;
    private ImageView upImage,playImage,nextImage;
    private ListViewAdapter adapter;
    private ArrayList<Music> list;
    private MusicPrivider musicprovider;//音乐提供者
    private MySharePregerence sp;
    private MyApplycation app;
    private int sss=1;
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(sss==1){
              view = inflater.inflate(R.layout.first_fragment_layout, container,false);//用布局渲染器将一个xml布局转化为一个view对象
            initView(view);
            sss=2;
        }
        return view;
    }

    //获取布局里的控件
    private void initView(View view) {
         app = (MyApplycation) getActivity().getApplication();
        sp = new MySharePregerence(getActivity());
        listview = (ListView) view.findViewById(R.id.firstfragment_listview);
        imageview = (ImageView) view.findViewById(R.id.firstfragment_image);

        songAlter = (TextView) view.findViewById(R.id.songAlter);
        songName = (TextView) view.findViewById(R.id.songName);

        upImage = (ImageView) view.findViewById(R.id.image1);
        playImage = (ImageView) view.findViewById(R.id.image2);
        nextImage = (ImageView) view.findViewById(R.id.image3);




        //设置播放，上下一首ImageView的点击事件(接口回调机制)

        upImage.setOnClickListener(this);
        playImage.setOnClickListener(this);
        nextImage.setOnClickListener(this);

        imageview.setOnClickListener(new View.OnClickListener() {//点击图标开启一个Activity界面,播放信息界面
            @Override
            public void onClick(View v) {
                //在跳转到MusicPlayerActivity界面时要判断播放音乐的服务是否创建，
                // 若没有这需要创建，因为用户可能在没有创建服务的情况下直接跳转界面而此时MusicPlayerActivity
                // 都需要音乐集合的index,和上下一曲，播放 都要首先开启出service所以要防止空指针异常

                if (MusicPlayerService.mp == null) {  //MusicPlayerService与mp是绑定在一起的
                    Intent intent = new Intent(getActivity(),MusicPlayerService.class);
                    getActivity().startService(intent);//开启服务
                }

                Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
                intent.putExtra("fposition", MusicPlayerService.index);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {//当Activity和Fragment里的控件共同初始化完成
        super.onActivityCreated(savedInstanceState);
        initListViewDate();

    }

    //为listview添加数据和适配器
    private void initListViewDate() {
        musicprovider = new MusicPrivider(getActivity());
        list = musicprovider.getAllMusic();  //获取所有音乐

        if(list==null || list.size()<=0){
           return;
        }

        //默认为第一首的音乐信息
        songName.setText(list.get(0).getName());
        songAlter.setText(list.get(0).getAlt());


        adapter = new ListViewAdapter(list, getActivity());//设置适配器
        listview.setAdapter(adapter);

        //设置listview的点击播放事件（发广播去开启服务）
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (MusicPlayerService.mp == null) {  //MusicPlayerService与mp是绑定在一起的
                    Intent intent = new Intent(getActivity(),MusicPlayerService.class);
                    getActivity().startService(intent);//开启服务
                }
                flage = 1;//播放状态

                Intent intent = new Intent(getActivity(), MusicPlayerService.class);
                //**********
                intent.putExtra("player", 1);//播放
                MusicPlayerService.aaa = 1;             //设置为只有在切歌时从头播放
                MusicPlayerService.index = position;//设置MusicPlayerService音乐角标
                getActivity().startService(intent);
                //更新底部UI控件的数据
                Music music = (Music) adapter.getItem(position);

                songName.setText(music.getName());
                songAlter.setText(music.getAlt());
                playImage.setImageResource(R.mipmap.player);
//                imageview.setBackground(music.get);

                //最后保存单前时间，用于做最近播放界面的使用
                saveCurrentTimer();

            }
        });

    }
    public  void saveCurrentTimer(){

        Music music  = list.get(MusicPlayerService.index);

        try {
            Music loveMusic = app.dbUtils.findFirst(Selector.from(Music.class).where("loveid", "=", music.getId()));

            if(loveMusic==null){

                music.setLoveid(music.getId());
                music.setPlayTimer(System.currentTimeMillis());//设置为当前的时间
                app.dbUtils.save(music);

            }else {
                loveMusic.setPlayTimer(System.currentTimeMillis());
                app.dbUtils.update(loveMusic,"playTimer");
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

    }


    private int position =0;//默认为第一首音乐
    private int flage =0;//音乐播放状态标记值
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(),MusicPlayerService.class);
        int id = v.getId();
        switch(id){
            case R.id.image1://上一首   要确保service是创建促来的
                flage = 1; //设置按钮图标切换

                playImage.setImageResource(R.mipmap.player);

                if(MusicPlayerService.mp==null) { //解决第一次启动时的bug
                    intent.putExtra("player",1);//服务第一次创建时默认播放第一首音乐
                    getActivity().startService(intent);//开启服务
                }else{

                    MusicPlayerService.upSong();

                    songName.setText(list.get(MusicPlayerService.index).getName());
                    songAlter.setText(list.get(MusicPlayerService.index).getAlt());
                }
                break;
            case R.id.image2://播放/暂停 flage=1为播放

                if(flage==1) {
                    playImage.setImageResource(R.mipmap.stopsong);
                    intent.putExtra("player",2);
                    flage=0;
                }else if (flage == 0){
                    playImage.setImageResource(R.mipmap.player);
                    intent.putExtra("player", 1);
                    flage = 1;
                }
                getActivity().startService(intent);
                break ;
            case R.id.image3://下一首
                flage = 1;

                playImage.setImageResource(R.mipmap.player);

                if(MusicPlayerService.mp==null) { //解决第一次启动时的bug
                    intent.putExtra("player", 1);//服务第一次创建时默认播放第一首音乐
                    getActivity().startService(intent);
                }else{
                    MusicPlayerService.nextSong();

                    songName.setText(list.get(MusicPlayerService.index).getName());
                    songAlter.setText(list.get(MusicPlayerService.index).getAlt());
                }
                break;
        }
    }

    //重写 交互方法来更新UI控件和数据
    @Override
    public void onResume() {  //界面可交互时执行
        super.onResume();

        if(MusicPlayerService.list==null || MusicPlayerService.list.size()<=0) {

            return;
        }

        if((MusicPlayerService.mp!=null) && MusicPlayerService.mp.isPlaying()){ //根据音乐的播放状态来切换播放图标
            flage =1;//解决播放图片显示的小bug
            playImage.setImageResource(R.mipmap.player);

            songName.setText(MusicPlayerService.list.get(MusicPlayerService.index).getName());
            songAlter.setText(MusicPlayerService.list.get(MusicPlayerService.index).getAlt());

        }else if((MusicPlayerService.mp!=null) && !MusicPlayerService.mp.isPlaying()){
            flage =0;//解决播放图片显示的小bug
            playImage.setImageResource(R.mipmap.stopsong);

            songName.setText(MusicPlayerService.list.get(MusicPlayerService.index).getName());
            songAlter.setText(MusicPlayerService.list.get(MusicPlayerService.index).getAlt());

        }
    }
    /**
     * 小结：音乐播放器中最核心的就是list里的音乐位置index，只要将其设置为static的公共属性，在ui界面层就可以根据他获取到Music对象，
     *       从而就可以获取到音乐对象的所以信息
     */

}
