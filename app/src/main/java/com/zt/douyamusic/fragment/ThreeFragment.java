package com.zt.douyamusic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zt.douyamusic.activity.MyApplycation;
import com.zt.douyamusic.R;
import com.zt.douyamusic.adapter.CurrentPlayListAdapter;
import com.zt.douyamusic.entity.Music;
import com.zt.douyamusic.service.MusicPlayerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/24.
 */
public class ThreeFragment extends Fragment {//设置viewpager里 v4包下的
    private int dd = 1;
    private View view;
    private ListView listView;
    private CurrentPlayListAdapter adapter;
    private MyApplycation app;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(dd==1){ //保证中显示一次
             view = inflater.inflate(R.layout.three_fragment_layout, container, false);//用布局渲染器将一个xml布局转化为一个view对象
            initView(view);
            dd = 2;
            if (MusicPlayerService.mp == null) {  //MusicPlayerService与mp是绑定在一起的
                Intent intent = new Intent(getActivity(),MusicPlayerService.class);
                getActivity().startService(intent);//开启服务
            }
        }
        return view;
    }

    private void initView(View view) {
         app = (MyApplycation) getActivity().getApplication();
        listView = (ListView) view.findViewById(R.id.three_fragment_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = (Music) adapter.getItem(position);
                MusicPlayerService.playByString(music.getName());
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDate();
    }
    //加载最近播放的音乐数据
    private void initDate() {
        try {
            List<Music> list = app.dbUtils.findAll(Selector.from(Music.class).where("playTimer", "!=", 0).orderBy("playTimer", true).limit(10));

            if(list==null ) {
                return;
            }else {
                ArrayList<Music> li = (ArrayList<Music>) list;
                adapter = new CurrentPlayListAdapter(getActivity(), li);
                listView.setAdapter(adapter);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
