package com.zt.douyamusic.down;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.zt.douyamusic.entity.NetMusic;
import com.zt.douyamusic.utils.Config;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/1/30.
 * 下载音乐类
 */
public class DownMusic {
    private static final int SUCCESS_LRC=1;
    private static final int FAIL_LRC=2;

    private static final int SUCCESS_MP3=3;
    private static final int FAIL_MP3=4;

    private static final int GET_MP3_URL=5;
    private static final int GET_MP3_URLFAIL=6;

    private static final int MUSIC_EXISTS=7;
    private NetMusic netMusic;
    //开启线程联网解析获取歌曲的真正url 此处是Thread里封装了Runnable方法来开启线程，最后在发送一个handler消息实现通信
    public void download(final NetMusic netMusic) {
        this.netMusic = netMusic;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //拼接url
                String url = Config.BAIDU_URL + "song/" + netMusic.getUrl().substring(netMusic.getUrl().lastIndexOf("/") + 1) + Config.DOWNLOAD_URL;
                try {
                    Document doc = Jsoup.connect(url).userAgent(Config.USER_AGENT).timeout(6 * 1000).get();//下载界面的html文档代码

                    Elements targetElements = doc.select("a[date-btndata]");

                    if(targetElements.size()<=0) {
                        myhandler.obtainMessage(GET_MP3_URLFAIL).sendToTarget();//获取MP3 url失败
                        return ;
                    }

                    for(Element e : targetElements){
                        if(e.attr("href").contains(".mp3")) {
                            String result = e.attr("href");//获取连接
                            Message msg = myhandler.obtainMessage(GET_MP3_URL, result);//获取MP3 url成功
                            msg.sendToTarget();
                            return;
                        }
                        if(e.attr("href").startsWith("/vip")) {
                            targetElements.remove(e);
                        }
                    }
                    if(targetElements.size()<=0) {
                        myhandler.obtainMessage(GET_MP3_URLFAIL).sendToTarget();//获取MP3 url失败
                        return ;
                    }
                    String result = targetElements.get(0).attr("href");
                    Message msg = myhandler.obtainMessage(GET_MP3_URL, result);//获取MP3 url成功
                    msg.sendToTarget();


                } catch (IOException e) {
                    e.printStackTrace();
                    myhandler.obtainMessage(GET_MP3_URLFAIL).sendToTarget();//获取MP3 url失败(发生异常也 定义为失败)
                }

            }
        }).start();
    }

    private Handler myhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_LRC:
                    if(downerStaticInterface!=null){
                        downerStaticInterface.successful("歌词下载成功");
                    }
                break;
                case FAIL_LRC:
                    if(downerStaticInterface!=null){
                        downerStaticInterface.fail("歌词下载失败");
                    }
                    break;
                case SUCCESS_MP3:
                    if(downerStaticInterface!=null){
                        downerStaticInterface.successful(netMusic.getName()+"下载成功");
                    }
                    //接着下载歌词*****************
                    String lrcurl = Config.BAIDU_URL + netMusic.getUrl();
                    downloadLRC(lrcurl);

                    break;
                case FAIL_MP3:
                    if(downerStaticInterface!=null){
                        downerStaticInterface.fail(netMusic.getName()+"下载失败");
                    }
                    break;
                case GET_MP3_URL:
                        String url =(String)msg.obj;//获取到音乐歌曲真正的url
                        //根据此url去下载
                       downmusicByurl(url);
                    break;
                case GET_MP3_URLFAIL:
                    if(downerStaticInterface!=null){
                        downerStaticInterface.fail("下载失败,该歌曲为收费VIP类型");
                    }
                    break;
                case MUSIC_EXISTS:
                    if(downerStaticInterface!=null){
                        downerStaticInterface.fail(netMusic.getName()+"歌曲已经存在");
                    }
                    break;
            }
        }
    };
    //真正下载音乐的方法，也要开启线程
    public void downmusicByurl(final String url){

        new Thread(new Runnable() {
            @Override
            public void run() {

                File musicdirfile = new File(Environment.getExternalStorageDirectory()+Config.MUSIC_DIR);//存储到sd卡（相对路径）
                if(!musicdirfile.exists()) { //判断存储路径是否存在
                    musicdirfile.mkdirs();
                }
                String mp3url = Config.BAIDU_URL + url;//歌曲真正的网络url下载地址
                String targ = musicdirfile + "/" + netMusic.getName() + ".mp3";//音乐文件的存储名称(绝对路径)

                //然后根据此名称，创建文件
                File fileTager = new File(targ);

                if(fileTager.exists()){
                    myhandler.obtainMessage(MUSIC_EXISTS).sendToTarget(); //歌曲已经存在无需下载啦
                    return;
                }else {//开始下载

                    //使用OkHttpClient框架来下载
                    OkHttpClient client = new OkHttpClient();
                    Request request  = new Request.Builder().url(mp3url).build();//请求

                    try {
                        Response respone = client.newCall(request).execute();//回应
                        if(respone.isSuccessful()) {
                            PrintStream ps = new PrintStream(fileTager);
                            byte[] bytes = respone.body().bytes();

                            ps.write(bytes,0,bytes.length); //写入sd卡里的文件
                            ps.close();
                            myhandler.sendEmptyMessage(SUCCESS_MP3);//歌曲下载成功

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        myhandler.sendEmptyMessage(FAIL_MP3);//若发生异常，下载失败
                    }
                }

            }
        }).start();
    }
    //下载音乐歌词的方法(同样也要开启线程)
    public void downloadLRC(final String lrcUrl){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(lrcUrl).userAgent(Config.USER_AGENT).timeout(6000).get();

                    Elements lyrTag = doc.select("div.lyric-content");
                    String lrcURL = lyrTag.attr("data-lrclink");

                    File lrcdirFile = new File(Environment.getExternalStorageDirectory()+Config.LRC_DIR);

                    if(!lrcdirFile.exists()) {
                        lrcdirFile.mkdirs();
                    }

                    lrcURL = Config.BAIDU_URL + lrcURL;//歌词真正的网络下载地址
                    String name = lrcdirFile + "/" + netMusic.getName() + ".lrc";

                    File lrcFile = new File(name);
                   //使用OkHttpClient框架来下载

                    OkHttpClient client = new OkHttpClient();
                    Request request  = new Request.Builder().url(lrcURL).build();//请求

                    try {
                        Response respone = client.newCall(request).execute();//回应
                        if(respone.isSuccessful()) {
                            PrintStream ps = new PrintStream(lrcFile);
                            byte[] bytes = respone.body().bytes();

                            ps.write(bytes,0,bytes.length); //写入sd卡里的文件
                            ps.close();
                            myhandler.sendEmptyMessage(SUCCESS_LRC);//歌词下载成功

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        myhandler.sendEmptyMessage(FAIL_LRC);//若发生异常，下载失败
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface  DownerStaticInterface{
        public void successful(String str);
        public void fail(String error);
    }
    private DownerStaticInterface downerStaticInterface;
    //实例化接口对象
    public void getDownerStaticInterface(DownerStaticInterface downerStaticInterface) {
        this.downerStaticInterface = downerStaticInterface;
    }
}
