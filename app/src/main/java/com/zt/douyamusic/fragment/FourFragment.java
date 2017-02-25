package com.zt.douyamusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zt.douyamusic.R;

/**
 * Created by Administrator on 2016/1/24.
 */
public class FourFragment extends Fragment {//设置viewpager里 v4包下的
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.four_fragment_layout, container,false);//用布局渲染器将一个xml布局转化为一个view对象
        return view;
    }
}
