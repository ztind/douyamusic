package com.zt.douyamusic.entity;

/**
 * Created by Administrator on 2016/1/29.
 * 封装从网络上下载来的音乐信息
 */
public class NetMusic {
    private String name;//歌名
    private String alt;//艺术家
    private String album;//专辑
    private String url;//地址

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
