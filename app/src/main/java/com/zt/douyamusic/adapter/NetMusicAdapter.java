package com.zt.douyamusic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zt.douyamusic.R;
import com.zt.douyamusic.entity.NetMusic;

import java.util.List;

/**
 * Created by Administrator on 2016/1/29.
 */
public class NetMusicAdapter extends BaseAdapter {
    private List<NetMusic> list;
    private Context context;
    public NetMusicAdapter(Context context,List<NetMusic> list) {
        this.list = list;
        this.context = context;
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
            convertView = View.inflate(context, R.layout.netmusiclist_layout, null);
            vh = new ViewHolder();
            vh.name = (TextView) convertView.findViewById(R.id.netmusicName);
            vh.alt = (TextView) convertView.findViewById(R.id.netmusicAliter);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        NetMusic netMusic = list.get(position);
        vh.name.setText(netMusic.getName());
        vh.alt.setText(netMusic.getAlt());

        return convertView;
    }
    //优化类
    class ViewHolder{
        TextView name,alt;
    }
}
