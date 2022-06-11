package com.gary.other.screenHW;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gary.selfCheck.Utils;

public class RNScreenpxModule{

    private int w = 0; // 当前设备的宽度方向 像素数
    private int h = 0; // 当前设备的宽度方向 像素数
    private volatile static boolean mHasCheckAllScreen;
    private volatile static boolean mIsAllScreenDevice;   // 是否全面屏
    private volatile static boolean notNavigationBarExist; // 不存在虚拟导航器 (即真正的使用全面屏)

    public RNScreenpxModule(Context reactApplicationContext) {
        WindowManager wd = (WindowManager) reactApplicationContext.getSystemService(Context.WINDOW_SERVICE);
        // 1. 获取当前安卓设备的真实像素
        w = RNScreenpxModule.getRealPXFromScreenW(reactApplicationContext);
        h = RNScreenpxModule.getRealPXFromScreenH(reactApplicationContext);
        // 2. 判断是否为全面屏
        mIsAllScreenDevice = RNScreenpxModule.isAllScreenDevice(reactApplicationContext);
        Log.e(Utils.TAG, mIsAllScreenDevice + "===0=== 全面屏吗??????");
        if (mIsAllScreenDevice) {
            Log.e(Utils.TAG, notNavigationBarExist + "===0=== 开启了吗??????");
            notNavigationBarExist = Settings.Global.getInt(reactApplicationContext.getContentResolver(), "force_fsg_nav_bar", 0) != 0;
            Log.e(Utils.TAG, notNavigationBarExist + "===0=== 开启了吗??????");

        }
    }

    /**
     * 获取当前安卓设备的真实像素
     *
     * @param context
     */
    public static int getRealPXFromScreenH(Context context) {
        WindowManager wd = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = wd.getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        int x = point.x;
        int y = point.y;
        return y;
    }


    /**
     * 获取当前安卓设备的真实像素
     *
     * @param context
     */
    public static int getRealPXFromScreenW(Context context) {
        WindowManager wd = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = wd.getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        int x = point.x;
        int y = point.y;
        return x;
    }

    /**
     * 判断 安卓设置是否 全面屏
     *
     * @param context
     * @return
     */
    public static boolean isAllScreenDevice(Context context) {
        if (mHasCheckAllScreen) {
            return mIsAllScreenDevice;
        }
        mHasCheckAllScreen = true;
        mIsAllScreenDevice = false;
        // 低于 API 21的，都不会是全面屏。。。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            float width, height;
            if (point.x < point.y) {
                width = point.x;
                height = point.y;
            } else {
                width = point.y;
                height = point.x;
            }
            if (height / width >= 1.97f) {
                mIsAllScreenDevice = true;
            }
        }
        return mIsAllScreenDevice;
    }

    public static void isAllScreen(Activity activity) {
        Window window = activity.getWindow();
        View decorView = window.getDecorView();
        //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//        decorView.setSystemUiVisibility(option);
        int systemUiVisibility = decorView.getSystemUiVisibility();
        Log.i(Utils.TAG, "option="+option);
        Log.i(Utils.TAG, "systemUiVisibility="+systemUiVisibility);
//        if (option == systemUiVisibility) {
//            Log.i(Utils.TAG, "全面屏");
//        } else {
//            Log.i(Utils.TAG, "非全面屏");
//        }
    }


}