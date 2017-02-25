package com.zt.douyamusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zt.douyamusic.R;
import com.zt.douyamusic.entity.Music;
import com.zt.douyamusic.musicProvider.MusicPrivider;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/1.
 * 最近播放列表 适配器
 */
public class CurrentPlayListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Music> list;
    private MusicPrivider musicprovide;
    public CurrentPlayListAdapter(Context context,ArrayList<Music> list) {
        this.list = list;
        this.context = context;
        musicprovide = new MusicPrivider(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_layout, null);
            vh = new ViewHolder();
            vh.imageview = (ImageView) convertView.findViewById(R.id.image_model);
            vh.musicName = (TextView) convertView.findViewById(R.id.musicname_model);
            vh.musicAlter = (TextView) convertView.findViewById(R.id.musicalter_model);
            vh.musicTime = (TextView) convertView.findViewById(R.id.musictime_model);

            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        Music music = list.get(position);

        //   vh.imageview.setImageResource();
        vh.musicName.setText(music.getName());//歌名
        vh.musicAlter.setText(music.getAlt());//艺术家
        vh.musicTime.setText(musicprovide.changeTime(music.getDuration()));//歌曲时长

        return convertView;
    }
    class ViewHolder{
        private ImageView imageview;
        private TextView musicName,musicAlter,musicTime;
    }
}
