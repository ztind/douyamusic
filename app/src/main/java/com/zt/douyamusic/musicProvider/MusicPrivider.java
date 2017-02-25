package com.zt.douyamusic.musicProvider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.zt.douyamusic.entity.Music;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2016/1/24.
 * 获取本地音乐的内容提供者类
 */
public class MusicPrivider {

    private Context context;
    private ContentResolver cr;
    private ArrayList<Music> list;

    public MusicPrivider(Context context){
         cr = context.getContentResolver();//获取内容解析者来获取本地音乐数据
    }

    /**
     *根据id来查询单个音乐
     */
    public Music getMusicById(long musicId){
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//获取sd卡的uri路径
        //根据音乐的id来查询单首音乐的所以信息 DEFAULT_SORT_ORDER(默认的分类次序)
        //uri 查询字段 查询条件 条件值 排序
        Cursor cursor = cr.query(uri, null,
                MediaStore.Audio.Media._ID + "=" + musicId, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        Music music=null;

        if(cursor.moveToNext()) {
            music = new Music();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));//音乐id
            String  title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));//音乐标题(歌名，无mp3后缀)
            String  artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            String  album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));//专辑
            long  artist_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));//专辑id

            long  duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//音乐时长
            long  size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));//音乐文件大小
            String  musicUri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));//路径
            int  isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否是音乐

            if(isMusic!=0) {//说明是音乐，就封装为一个Music对象
                music.setId(id);
                music.setName(title);
                music.setAlt(artist);
                music.setAlbum(album);
                music.setAlbumid(artist_id);
                music.setDuration(duration);
                music.setSize(size);
                music.setUrl(musicUri);
                music.setIsMusic(isMusic);
            }
        }
        cursor.close();//关闭游标，节约资源
        return music;
    }
    /**
     *  查询所有音乐
     */
    public ArrayList<Music> getAllMusic(){
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//获取sd卡的uri路径

        Cursor cursor = cr.query(uri, null,
                MediaStore.Audio.Media.DURATION+">=120000", null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);//查询时长大于2分钟的歌

        Music music=null;
        list = new ArrayList<>();

        while(cursor.moveToNext()) {
            music = new Music();

            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));//音乐id
            String  title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));//音乐标题
            String  artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            String  album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));//专辑
            long  artist_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));//专辑id

            long  duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//音乐时长
            long  size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));//音乐文件大小
            String  musicUri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));//路径
            int  isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否是音乐

            if(isMusic!=0) {//说明是音乐，就封装为一个Music对象
                music.setId(id);
                music.setName(title);
                music.setAlt(artist);
                music.setAlbum(album);
                music.setAlbumid(artist_id);
                music.setDuration(duration);
                music.setSize(size);
                music.setUrl(musicUri);
                music.setIsMusic(isMusic);
                list.add(music);//添加到集里
            }
        }
        cursor.close();//关闭游标，节约资源
        return list;
    }
    /**
     * 获取专辑图片的方法
     */
    public int getAlbumPicture(){
        return 0;
    }
    /**
     * 转化音乐时长格式的方法
     */
    public String changeTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        Date date = new Date();
        date.setTime(time);
        String str = sdf.format(date);
        return str;
    }


}
