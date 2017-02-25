package com.zt.douyamusic.utils;

/**
 * Created by Administrator on 2016/1/29.
 * 配置信息类
 */
public class Config {
    //搜索音乐时的action和key
    public static final String SEARCH = "search?key";//搜索音乐的action和key
    //百度音乐主页地址
    public static final String BAIDU_URL = "http://music.baidu.com/";
    //热歌榜url,后部分
    public static final String BAIDU_DAYHOT = "top/dayhot/?pst=shouyeTop";
    //下载音乐的url
    public static final String DOWNLOAD_URL = "/download?__o=%2Fsearch%2Fson";
    //使用游览器的代理
    public static final String USER_AGENT="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.87 Safari/537.36 QQBrowser/9.2.5584.400";

    //下载 音乐的存储路径
    public static final String MUSIC_DIR = "/douya_music/music";
    //歌词的存储路径
    public static final String LRC_DIR = "/douya_music/lrc";
    /**
     * 获取agent的html代码
     *
     * <html>

     <head>
     <title>user-agent</title>
     </head>

     <body>
     <input type="button" value="user-agent" onclick="javascript:document.write(navigator.userAgent)">
     </body>

     </html>

     */
}
