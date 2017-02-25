package com.zt.douyamusic.searchnetmusic;

import android.os.Message;
import android.text.TextUtils;

import com.zt.douyamusic.entity.NetMusic;
import com.zt.douyamusic.utils.Config;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/30.
 */
public class SearchNetMusic {
    private static final int SIZE=20;//每次查询20条音乐,因为百度音乐官网上每页最多显示20条音乐信息
    private static final String SEARCH_URL = Config.BAIDU_URL + Config.SEARCH;//搜索音乐时的url
    private String songname;
    private int page;
    private List<NetMusic> list;

    public SearchNetMusic(String songName,int page) { //page为从网页的第几页开始查询 解析
        this.songname = songName;
        this.page=page;

        new NetThread().start();//开启线程

    }

    class NetThread extends Thread{
        @Override
        public void run() {
            super.run();
            list = searchMusic(songname,page);//搜索音乐

            if(list==null) {
                myHandler.sendEmptyMessage(111);//搜索失败
            }else{
                myHandler.sendEmptyMessage(100);//搜索成功
            }
        }
    }

    public android.os.Handler myHandler=new android.os.Handler(){ //handler实现线程间通信的媒介，子线程不能访问ui控件
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 111:
                    if(netinterFace!=null){
                        netinterFace.sendList(null);
                    }
                    break;
                case 100:
                    if(netinterFace!=null){
                        netinterFace.sendList(list); //发送集合给执行方
                    }
                    break;
            }
        }
    };
    //定义接口
    public interface NetinterFace {
        public void sendList(List<NetMusic> list);
    }
    public  NetinterFace netinterFace;

    public   void getNetinterFace(NetinterFace netinterFace) {
        this.netinterFace = netinterFace;
    }

    public SearchNetMusic(){}


    private List<NetMusic> searchMusic(String songname,int page) {

        ArrayList<NetMusic> lists= lists = new ArrayList<>();

        String start = String.valueOf((page - 1) * SIZE);

        try {
            Document doc = Jsoup.connect(SEARCH_URL).data("key", songname, "start", start, "size", String.valueOf(SIZE)).
                    userAgent(Config.USER_AGENT).timeout(6 * 1000).get(); //获取到了搜索歌手音乐的html代码页面

            Elements songTitles = doc.select("div.song-item.clearfix");
            Elements songInfos;

            TAG:
            for(org.jsoup.nodes.Element song : songTitles) {
                songInfos = song.getElementsByTag("a");

                NetMusic netMusic = new NetMusic();

                for(org.jsoup.nodes.Element songinfo : songInfos) {
                    //收费歌曲
                    if(songinfo.attr("href").startsWith("http://y.baidu.com/song/")){
                        continue  TAG; //跳出到tag
                    }

                    //跳转到百度音乐盒的歌曲
                    if(songinfo.attr("href").equals("#") && !TextUtils.isEmpty(songinfo.attr("data-songdata"))){
                        continue  TAG;
                    }
                    //歌曲连接
                    if(songinfo.attr("href").startsWith("/song")){
                        netMusic.setName(songinfo.text());
                        netMusic.setUrl(songinfo.attr("href"));
                    }
                    //歌手连接
                    if(songinfo.attr("href").startsWith("/data")){
                        netMusic.setAlt(songinfo.text());
                    }
                    //专辑连接
                    if(songinfo.attr("href").startsWith("/album")){
                        netMusic.setAlbum(songinfo.text().replaceAll("《|》",""));
                    }
                }
                lists.add(netMusic);//添加到集合里

            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return lists;
    }

}
