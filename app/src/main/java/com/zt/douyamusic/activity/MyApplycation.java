package com.zt.douyamusic.activity;

import android.app.Application;
import android.content.Context;

import com.lidroid.xutils.DbUtils;

/**
 * 应用程序类:在清单文件里创建，只会创建一次，所以在此类里创建 收藏数据库最合适（数据库只要创建一次即可）
 *
 * /**
 * 收藏喜欢的音乐类
 * 思想：将音乐点击的收藏喜爱音乐添加到数据里
 * 1，使用xUtils框架下的DbUtils类，调用此类的Create()方法他会自动创建一个数据库
 * 2，DbUtils最牛逼的地方是他会将存储的对象封装为数据库里的一张表，会根据此对象的成员属性去生成字段
 * 3，其访问数据库的方法都封装在此类（DbUtils）里，只需调用即可
 *
 * 4,简记：DbUtils会根据存储类自动创建字段（可以用SqlitStudoi数据库软件来查看详细信息）
 * 5，,首先要导入xUtils的jar包到libs目录下
 * 6,切记要在清单文件的application里声明属性 : android:name=".db.MyApplycation"
 *
 */

public class MyApplycation extends Application{ //全局的公共的应用程序类，里面可以存放一些公共的类或属性，eg:sp , 收藏数据库啊
    public static DbUtils dbUtils;
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        dbUtils = DbUtils.create(getApplicationContext(), "loveMp3.db");//创建收藏喜爱音乐的数据库
        context = getApplicationContext();

    }
    //注意获取实例化此对象通过:getApplication()方法即可获得。

}
