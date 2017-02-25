package com.zt.douyamusic.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.zt.douyamusic.R;
import com.zt.douyamusic.adapter.ViewPagerAdapter;
import com.zt.douyamusic.service.MusicPlayerService;

import cn.waps.AppConnect;

/**
 * Created by Administrator on 2016/1/24.
 * 豆芽音乐主界面 ：采用第三方UI框架PagerSlidingTabStrip（页面滑动选项卡）来设计
 *  1，导入包时的错误可以 将Project视图下的bulid.gradle里的sdk的版本目标版本改为何android视图里的版本信息一样，
 *  2，最后去除下面的apply from: 'https://raw.github.com/chrisbanes/gradle-mvn-push/master/gradle-mvn-push.gradle'
 *  3，最后 Try again
 */
public class MainActivity extends ActionBarActivity { //此处设置为有Bar的activity

    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private ViewPager viewpager;
    private ViewPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_main);

        //设置电话监听器
        TelephonyManager phoneManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        phoneManager.listen(new Listener(), PhoneStateListener.LISTEN_CALL_STATE);

        pagerSlidingTabStrip = (PagerSlidingTabStrip) this.findViewById(R.id.psts);
        viewpager = (ViewPager) this.findViewById(R.id.viewpager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(adapter);

        //最后为选项卡设置viewpager[其标题文本就会显示上]
        pagerSlidingTabStrip.setViewPager(viewpager);


        AppConnect.getInstance(this).setAdBackColor(Color.argb(20, 20, 30, 40));

        AppConnect.getInstance(this).setAdForeColor(getResources().getColor(R.color.adcolor));

        LinearLayout miniLayout =(LinearLayout) findViewById(R.id.miniAdLinearLayout);
        AppConnect.getInstance(this).showMiniAd(this, miniLayout, 10);



    }
    private MediaPlayer mpl;
    private class Listener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            mpl = MusicPlayerService.mp;
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:

                    if(mpl!=null && mpl.isPlaying()){
                        mpl.pause();
                        MusicPlayerService.actFlage = true;
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:

                    if(mpl!=null && !mpl.isPlaying() && MusicPlayerService.actFlage==true){
                        MusicPlayerService.actFlage=false;
                        mpl.start();
                    }

                    break;
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);//添加布局到actionbar里

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  //点击监听事件
        int id = item.getItemId();
        if (id==R.id.item1) {
            if (MusicPlayerService.mp == null) {  //MusicPlayerService与mp是绑定在一起的
                Intent intent = new Intent(this,MusicPlayerService.class);
                startService(intent);//开启服务
            }
            Intent intent = new Intent(this, LoveMusicActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppConnect.getInstance(this).close();
    }
}
