package com.gary.other.screenHW;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.gary.selfCheck.Utils;

public class ScreenHeightAndWidth {

    //通常情况下，刘海的高就是状态栏的高
    public static int heightForDisplayCutout(Context context) {
        int resID = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resID > 0) {
            return context.getResources().getDimensionPixelSize(resID);
        }
        return 0;
    }

    public static int getWidthScreen(Context context) {
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        Log.i(Utils.TAG, "getWidthScreen=" + widthPixels);
        return widthPixels;
    }

    public static int getHeightScreen(Context context) {
        int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
        Log.i(Utils.TAG, "getHeightScreen=" + heightPixels);
        return heightPixels;
    }

    public static int getWidthAllScreen(Activity activity) {
        int width = activity.getWindow().getDecorView().getWidth();
        Log.i(Utils.TAG, "getWidthAllScreen=" + width);
        return width;
    }

    public static int getHeightAllScreen(Activity activity) {
        int height = activity.getWindow().getDecorView().getHeight();
        Log.i(Utils.TAG, "getHeightAllScreen=" + height);
        return height;
    }

    public static boolean isAllScreen(Activity activity) {
        boolean isAllScreen = false;
        if (getWidthScreen(activity) != getWidthAllScreen(activity)) {
            isAllScreen = true;
        }
        if (getHeightScreen(activity) != getHeightAllScreen(activity)) {
            isAllScreen = true;
        }
        Log.i(Utils.TAG, "isAllScreen=" + (isAllScreen ? "在刘海 内布局" : "在刘海 外布局"));
        return isAllScreen;
    }
}
