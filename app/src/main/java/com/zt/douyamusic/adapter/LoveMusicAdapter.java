package com.zt.douyamusic.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zt.douyamusic.R;
import com.zt.douyamusic.entity.Music;
import com.zt.douyamusic.musicProvider.MusicPrivider;

import java.util.List;

/**
 * Created by Administrator on 2016/1/28.
 */
public class LoveMusicAdapter extends BaseAdapter {
    private List<Music> list;
    private Context context;
    private MusicPrivider musicprovide;
    public LoveMusicAdapter(Context context,List<Music> list) {
        this.list = list;
        this.context = context;
        this.musicprovide = new MusicPrivider(context);
    }
    public LoveMusicAdapter(){}
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

        vh.imageview.setImageResource(R.mipmap.heart_48);
        vh.musicName.setText(music.getName());//歌名
        vh.musicName.setTextColor(Color.GREEN);
        vh.musicAlter.setText(music.getAlt());//艺术家
        vh.musicAlter.setTextColor(Color.GREEN);
        vh.musicTime.setText(musicprovide.changeTime(music.getDuration()));//歌曲时长
        vh.musicTime.setTextColor(Color.GREEN);

        return convertView;
    }
    //优化类
    class ViewHolder{
        private ImageView imageview;
        private TextView musicName,musicAlter,musicTime;
    }
}
