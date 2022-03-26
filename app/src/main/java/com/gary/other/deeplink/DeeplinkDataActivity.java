package com.gary.other.deeplink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.gary.selfCheck.MainActivity;
import com.gary.selfCheck.Utils;
import com.xinstall.XInstall;
import com.xinstall.listener.XInstallAdapter;
import com.xinstall.listener.XWakeUpAdapter;
import com.xinstall.model.XAppData;

import java.util.Map;


public class DeeplinkDataActivity extends Activity {
    private View image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutID =
                getResources().getIdentifier("huouniongame_splash_activity", "layout", getPackageName());
        setContentView(layoutID);
        XInstall.getInstallParam(new XInstallAdapter() {
            @Override
            public void onInstall(XAppData XAppData) {
                //获取渠道数据
                String channelCode = XAppData.getChannelCode();
                //获取数据
                Map<String, String> data = XAppData.getExtraData();
                //通过链接后面携带的参数或者通过WebSdk初始化传入的data值。
                String uo = data.get("uo");
                 //WebSdk初始，在buttonId里面定义的按钮点击携带数据
                String co = data.get("co");
                //获取时间戳
                String timeSpan = XAppData.getTimeSpan();
                //是否为第一次获取到安装参数
                boolean firstFetch = XAppData.isFirstFetch();
            }
        });
        XInstall.getWakeUpParam(this, getIntent(), wakeUpAdapter);
        initData();
        appendAnimation();
    }

    XWakeUpAdapter wakeUpAdapter = new XWakeUpAdapter() {
        @Override
        public void onWakeUp(XAppData XAppData) {
            // 获取渠道数据
            String channelCode = XAppData.getChannelCode();
            //获取数据
            Map<String, String> data = XAppData.getExtraData();
            // 通过链接后面携带的参数或者通过webSdk初始化传入的data值。
            String uo = data.get("uo");
            // webSdk初始，在buttonId里面定义的按钮点击携带数据
            String co = data.get("co");
            //获取时间戳
            String timeSpan = XAppData.getTimeSpan();

        }
    };

    private void initData() {
        if (getIntent() != null && getIntent().getData() != null) {
            DeeplinkDataUtil.channel1 = getIntent().getData().getQueryParameter("channel1");
            DeeplinkDataUtil.channel2 = getIntent().getData().getQueryParameter("channel2");
            DeeplinkDataUtil.channel3 = getIntent().getData().getQueryParameter("channel3");
            DeeplinkDataUtil.channel4 = getIntent().getData().getQueryParameter("channel4");
        } else {
            Log.e(Utils.TAG,"DeeplinkDataActivity intent data is null");
        }
    }

    private void appendAnimation() {
        AlphaAnimation ani = new AlphaAnimation(0.0f, 1.0f);
        ani.setRepeatMode(Animation.REVERSE);
        ani.setRepeatCount(0);
        ani.setDuration(2000);     //2s
        image = findViewById(
                getResources().getIdentifier("huouniongame_splash_activity_img", "id", getPackageName()));
        if (image == null) {
            Log.e("SplashActivity", "image = null");
            int defaultID =
                    getResources().getIdentifier("huouniongame_splash_activity_layout", "id", getPackageName());
            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this).inflate(defaultID, null);
            image = layout.getChildAt(0);
        }
        ani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.cancel();
                if (image != null) {
                    image.clearAnimation();
                    image = null;
                }
                startGameActivity();
            }
        });
        image.setAnimation(ani);
        ani.startNow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startGameActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        DeeplinkDataActivity.this.finish();
        startActivity(intent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}
