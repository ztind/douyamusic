package com.zt.douyamusic.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zt.douyamusic.R;
import com.zt.douyamusic.adapter.NetMusicAdapter;
import com.zt.douyamusic.entity.NetMusic;
import com.zt.douyamusic.searchnetmusic.SearchNetMusic;
import com.zt.douyamusic.utils.Config;
import com.zt.douyamusic.utils.InputListen;
import com.zt.douyamusic.utils.ZdyDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/24.
 */
public class SecondFragment extends Fragment implements View.OnClickListener{//设置viewpager里 v4包下的
    //声明控件
    private LinearLayout linearLayout1,linearLayout3;
    private RelativeLayout relativeLayout1;
    private ImageView imageButton;
    private ListView listView;
    private List<NetMusic> list;
    private int aa=1;
    private View view;
    private EditText wbk;
    private NetMusicAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (aa==1){ //保证此布局只创建一次，即从设置到网络搜索界面时onCreateView方法又被调用了一次，而我们需要的是数据的保存，不能再inflate一次
            view = inflater.inflate(R.layout.second_fragment_layout, container, false);//用布局渲染器将一个xml布局转化为一个view对象
            findView(view);
            aa = 2;
        }
        return view;
    }
    //初始化控件
    private void findView(View view) {

        list = new ArrayList<>();
        linearLayout1 = (LinearLayout) view.findViewById(R.id.second_line1);
        relativeLayout1 = (RelativeLayout) view.findViewById(R.id.second_rela1);
        linearLayout3 = (LinearLayout) view.findViewById(R.id.second_line3);
        imageButton = (ImageView) view.findViewById(R.id.search_button);
        wbk = (EditText) view.findViewById(R.id.search_edit);

        linearLayout1.setVisibility(View.VISIBLE);

        linearLayout1.setOnClickListener(this);

        imageButton.setOnClickListener(this);

        listView = (ListView) view.findViewById(R.id.second_listview);

        initDataFromInterNet();//从网络上先加载几条数据显示到listview里与此同时，隐藏relativeLayout1

        //设置listview的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                NetMusic netMusic = (NetMusic) adapter.getItem(position);

                ZdyDialog.showDialog(getActivity(),netMusic);//启动下载/播放提示界面

            }
        });

    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.second_line1: //显示搜索框
                relativeLayout1.setVisibility(View.VISIBLE);
                break;
            case R.id.search_button://搜索音乐
                searchNetMusic();
                break;
        }
    }
    private static final String SEARCH_URL = Config.BAIDU_URL + Config.SEARCH;//搜索音乐时的url
    private  String inputSongName;
    //点击搜索音乐图标搜索音乐
    private void searchNetMusic() {
        InputListen.hideInput(wbk);//隐藏输入框
        linearLayout1.setVisibility(View.GONE);
        relativeLayout1.setVisibility(View.VISIBLE);

        inputSongName = wbk.getText().toString();

        if (TextUtils.isEmpty(inputSongName)) {
            Toast.makeText(getActivity(), "请输入关键词", Toast.LENGTH_SHORT).show();
            return;
        }

        linearLayout3.setVisibility(View.VISIBLE);//显示加载圈


        //根据用户输入的关键词去网络上查询音乐***********************************
        SearchNetMusic searchNetMusic = new SearchNetMusic(inputSongName, 1); //1 表示从地1页开始解析
        searchNetMusic.getNetinterFace(new ffd()); //传入接口对象

    }
    class ffd implements SearchNetMusic.NetinterFace{
        @Override
        public void sendList(List<NetMusic> list) {
            if(list!=null && list.size()>0){
                SecondFragment.this.list.clear();

                SecondFragment.this.list.addAll(list); //集合添加集合

                adapter = new NetMusicAdapter(getActivity(), SecondFragment.this.list);

                listView.setAdapter(adapter);
                SecondFragment.this.linearLayout3.setVisibility(View.GONE);//查询成功后，隐藏加载圈
                wbk.setText(null);//清空文本输入框

            }else {
                    Toast.makeText(getActivity(),"数据加载失败,请检查你的网络连接", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //异步任务加载数据
    private void initDataFromInterNet() {

        new MyAsyn().execute(Config.BAIDU_URL+Config.BAIDU_DAYHOT);//传入百度音乐热歌榜的url地址

    }

    //第一个参数为doInBackground方法的传入参数，第三个为doInBackground的返回类型，onPostExecute接收参数

    class MyAsyn extends AsyncTask<String,Void,Integer>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            linearLayout3.setVisibility(View.VISIBLE);//显示加载圈

        }

        @Override
        protected Integer doInBackground(String... params) {
            String  url = params[0];
            //使用Jsoup框架来解析html代码，从而获取到音乐信息

            try {
                Document doucment = Jsoup.connect(url).userAgent(Config.USER_AGENT).timeout(6*1000).get();//返回html页面文档
                Elements songTitles = doucment.select("span.song-title");
                Elements artists = doucment.select("span.author_list");

                for(int i=0;i<20;i++) {//搜索20条数据
                    NetMusic netMusic = new NetMusic();
                    Elements urls = songTitles.get(i).getElementsByTag("a");//获取a超链接节点
                    netMusic.setUrl(urls.get(0).attr("href"));//获取url
                    netMusic.setName(urls.get(0).text());//歌名

                    Elements artistsElement = artists.get(i).getElementsByTag("a");
                    netMusic.setAlt(artistsElement.text());//歌手

                    netMusic.setAlbum("热歌榜");

                    list.add(netMusic);//添加到集合
                }

            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer==1){
                linearLayout3.setVisibility(View.GONE);//隐藏加载圈
                //设置适配器 显示加载到集合里的网络音乐数据
                adapter = new NetMusicAdapter(getActivity(),list);
                listView.setAdapter(adapter);
            }else {
                //Toast.makeText(getActivity(), "数据加载失败,请检查你的网络连接", Toast.LENGTH_SHORT).show();
                //linearLayout3.setVisibility(View.GONE);//隐藏加载圈
            }
        }
    }
}
