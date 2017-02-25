package com.zt.douyamusic.entity;

/**
 * Created by Administrator on 2016/1/24.
 * //listview Item的实体类对象
 */
public class Music {
    private long id;
    private long loveid;//新添加一列字段
    private String image;//专辑图片
    private String name;//歌名
    private String alt;//歌手
    private String album;//专辑
    private long albumid;//专辑id
    private long duration;//时长
    private long size;//大小
    private String url;//路径
    private int isMusic;//是否为音乐
    private int isLove; //1为收藏 0为没有
    private long playTimer;//最近播放时间


    public long getPlayTimer() {
        return playTimer;
    }

    public void setPlayTimer(long playTimer) {
        this.playTimer = playTimer;
    }

    public int getIsLove() {
        return isLove;
    }

    public void setIsLove(int isLove) {
        this.isLove = isLove;
    }

    public long getLoveid() {
        return loveid;
    }

    public void setLoveid(long loveid) {
        this.loveid = loveid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
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

    public long getAlbumid() {
        return albumid;
    }

    public void setAlbumid(long albumid) {
        this.albumid = albumid;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }

}
