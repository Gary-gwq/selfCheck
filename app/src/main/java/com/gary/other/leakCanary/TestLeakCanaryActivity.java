package com.gary.other.leakCanary;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gary.sdk.selfCheck.R;
import com.gary.selfCheck.Utils;
import com.squareup.leakcanary.RefWatcher;

public class TestLeakCanaryActivity extends Activity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_leak_canary_activity);
        //在自己的应用初始Activity中加入如下两行代码
        RefWatcher refWatcher = LeakCanaryApplication.getRefWatcher(this);
        refWatcher.watch(this);

        textView = (TextView) findViewById(R.id.tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestLeakCanaryActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(getSpannableString("","用户协议","和","隐私协议","和","第三方",""));
                        textView.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                });
//                startAsyncTask();
            }
        });
    }

    public void setSpan(SpannableString spannableString,int start, int end,int type) {
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Log.i(Utils.TAG,"widget="+type);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#FFFF8740"));
            }
        }, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }

    public SpannableString getSpannableString(String... strings) {
        StringBuilder totalStr = new StringBuilder();
        for (String str : strings) {
            totalStr.append(str);
        }
        SpannableString spannableString = new SpannableString(totalStr.toString());
        if (strings.length>1) {
            int start1 = strings[0].length();
            int end1 = start1 +strings[1].length();
            setSpan(spannableString,start1,end1,1);
            if (strings.length>3) {
                int start2 = end1 + strings[2].length();
                int end2 = start2 + strings[3].length();
                setSpan(spannableString,start2,end2,2);
                if (strings.length>5) {
                    int start3 = end2 + strings[4].length();
                    int end3 = start3 + strings[5].length();
                    setSpan(spannableString,start3,end3,3);
                }
            }
        }
        return spannableString;
    }

    private void startAsyncTask() {
        // This async task is an anonymous class and therefore has a hidden reference to the outer
        // class MainActivity. If the activity gets destroyed before the task finishes (e.g. rotation),
        // the activity instance will leak.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Do some slow work in background
                TestLeakCanaryActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TestLeakCanaryActivity.this, "开始模拟内存泄漏任务", Toast.LENGTH_SHORT).show();
                    }
                });
                SystemClock.sleep(20000);
                return null;
            }
        }.execute();
    }
}
