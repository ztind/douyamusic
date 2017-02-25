package com.zt.douyamusic.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zt.douyamusic.R;
import com.zt.douyamusic.adapter.LoveMusicAdapter;
import com.zt.douyamusic.entity.Music;
import com.zt.douyamusic.musicProvider.MusicPrivider;
import com.zt.douyamusic.service.MusicPlayerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/29.
 */
public class LoveMusicActivity extends BaseActivity implements View.OnClickListener{
    private ImageView exit;
    private ListView listview;
    private LoveMusicAdapter adapter;
    private ArrayList<Music> list;
    private DbUtils dbUtils;
    private MyApplycation app;
    private TextView textView;
    private MusicPrivider musicpr;
    private TextView topTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.lovemusic_layout);
        exit = (ImageView) findViewById(R.id.exit_image);
        exit.setOnClickListener(this);
        listview = (ListView) findViewById(R.id.love_listview);
        textView = (TextView) findViewById(R.id.tisi_text);
        topTextView = (TextView) findViewById(R.id.lovemusic_topText);
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT){
            topTextView.setVisibility(View.GONE);
        }
        musicpr = new MusicPrivider(this);
        initData();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = (Music) adapter.getItem(position);
                int idss = MusicPlayerService.playByString(music.getName());
                saveCurrentTimer(idss);//保存时间
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = (Music) adapter.getItem(position);
                showdeleteDialog(music);
                return false;
            }
        });
    }
    public  void saveCurrentTimer(int id) {
        ArrayList<Music> allList = musicpr.getAllMusic();

        Music music = allList.get(id);//此处的集合为所有音乐的集合

        try {
            Music loveMusic = app.dbUtils.findFirst(Selector.from(Music.class).where("loveid", "=", music.getLoveid()));

            if (loveMusic == null) {

                music.setPlayTimer(System.currentTimeMillis());//设置为当前的时间
                app.dbUtils.save(music);

            } else {
                loveMusic.setPlayTimer(System.currentTimeMillis());
                app.dbUtils.update(loveMusic, "playTimer");
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

    }


    private void initData() {
         app = (MyApplycation) getApplication();
        try {
            List<Music> lista = app.dbUtils.findAll(Selector.from(Music.class).where("isLove", "=", 1));//查询收藏喜爱的音乐

            if(lista==null){
                textView.setVisibility(View.VISIBLE);
                return ;
            }
            if(lista.size()==0){
                textView.setVisibility(View.VISIBLE);
            }
            list = (ArrayList<Music>)lista;
            adapter = new LoveMusicAdapter(this, list);
            listview.setAdapter(adapter);

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        this.finish();
    }
    public void showdeleteDialog(final Music music){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("消息");
        dialog.setMessage("确定从我的收藏里移除此歌吗？");
        dialog.setCancelable(true);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    music.setIsLove(0);

                    app.dbUtils.update(music,"isLove");//更新状态值
                    initData();//在此处进行刷新动作
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).create().show();
    }
}
