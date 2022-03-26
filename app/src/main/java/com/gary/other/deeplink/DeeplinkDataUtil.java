package com.gary.other.deeplink;

import android.util.Log;

import com.gary.selfCheck.Utils;

public class DeeplinkDataUtil {
    public static String channel1 = "";
    public static String channel2 = "";
    public static String channel3 = "";
    public static String channel4 = "";

    public void onWindowFocusChanged(boolean b) {
        Log.i(Utils.TAG,"onWindowFocusChanged boolean"+b);
    }
    public void onWindowFocusChanged() {
        Log.i(Utils.TAG,"onWindowFocusChanged");
    }
    public void onWindowFocusChanged(String s) {
        Log.i(Utils.TAG,"onWindowFocusChanged String"+s);
    }
}
