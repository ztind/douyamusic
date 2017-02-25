package com.zt.douyamusic.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zt.douyamusic.fragment.FirstFragment;
import com.zt.douyamusic.fragment.SecondFragment;
import com.zt.douyamusic.fragment.ThreeFragment;

/**
 * Created by Administrator on 2016/1/24.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private String[] str={"我的音乐","网络搜索","最近播放"};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    //标题
    @Override
    public CharSequence getPageTitle(int position) {

        return str[position];
    }

    @Override
    public Fragment getItem(int position) {  //从0开始
        switch (position){
            case 0:
                return new FirstFragment();
            case 1:
                return new SecondFragment();
            case 2:
                return new ThreeFragment();
//            case 3:
//                return new FourFragment();
        }
        return null;
    }

    @Override
    public int getCount() {//设置有多少页
        return 3;
    }
}
