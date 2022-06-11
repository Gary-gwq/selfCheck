package com.gary.selfCheck;


import static com.gary.selfCheck.Utils.DISPENSE;
import static com.gary.selfCheck.Utils.TAG;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gary.other.deeplink.DeeplinkDataUtil;
//import com.gary.other.oaid.ParamImpl1013;
import com.gary.other.oaid.ParamImpl1025;
import com.gary.sdk.selfCheck.R;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private String tag = "selfCheck";
    private EditText etPackageName;
    private Button btStart;
    private static TextView tvSuccess;
    private static TextView tvErr;
    private static TextView tvHint;
    private static int flagSuccess = 0;
    private static int flagErr = 0;
    private String packageName;
    private LocalService mLocalService;
    public DeeplinkDataUtil deeplinkDataUtil;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {//真正的沉浸式模式
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.mainContext = this;
        deeplinkDataUtil = new DeeplinkDataUtil();
        etPackageName = findViewById(R.id.et_packageName);
        btStart = findViewById(R.id.bt_start);
        tvSuccess = findViewById(R.id.tv_success);
        tvErr = findViewById(R.id.tv_err);
        tvHint = findViewById(R.id.tv_hint);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        btStart.setOnClickListener(v -> {
            if (!tvHint.getText().toString().isEmpty() && !tvHint.getText().toString().contains("检测完成")) {
                Toast.makeText(MainActivity.this, "请等待上一次检测完成！", Toast.LENGTH_SHORT).show();
                return;
            }
            doEmpty();
            packageName = etPackageName.getText().toString();
            if (packageName.isEmpty()) {
                appendErr("请输入包名");
                Toast.makeText(MainActivity.this, "请输入包名", Toast.LENGTH_LONG).show();
                return;
            }
            appendSuccess("包名：" + packageName);
            if (isAppInstalled(MainActivity.this, packageName)) {
                Utils.application_dispense = DISPENSE;
                stopService();
                startCheckService(packageName);
            } else {
                appendErr("需要检测的App未安装");
                Toast.makeText(MainActivity.this, "需要检测的App未安装", Toast.LENGTH_LONG).show();
            }
        });
    }

//    private void getOaid1013() {
//        try {
//            Class<?> cls = Class.forName("com.bun.miitmdid.core.JLibrary");
//            Method method = cls.getMethod("InitEntry", Context.class);
//            method.invoke(null, Utils.mainContext);
//        } catch (Exception e) {
//            Log.e("selfCheck", "未添加oaid插件");
//        }
//
//        Log.i(TAG,"1013 oaid=");
//        String className1013 = "com.gary.other.oaid.ParamImpl1013";
//        try {
//            ParamImpl1013 iParam = (ParamImpl1013) Class.forName(className1013).newInstance();
//            Map map = iParam.getParam(Utils.mainContext);
//            Log.i(TAG,"oaid1013="+map.get("device-oa_id"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void getOaid1025() {
        Log.i(TAG,"1025 oaid=");
        String className1025 = "com.gary.other.oaid1025.ParamImpl";
        try {
            ParamImpl1025 iParam = (ParamImpl1025) Class.forName(className1025).newInstance();
            Map map = iParam.getParam(Utils.mainContext);
            Log.i(TAG,"oaid1025="+map.get("device-oa_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doEmpty() {
        tvSuccess.setText("");
        tvErr.setText("");
        tvHint.setText("");
        flagSuccess = 0;
        flagErr = 0;
    }

    public void stopService() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningServices.size(); i++) {
            String name = runningServices.get(i).service.getClassName();
            if (mLocalService != null && name.equals("com.gary.selfCheck.LocalService")) {
                unbindService(connection);
            }
        }
    }

    private void startCheckService(String packageName) {
        Intent intent = new Intent(this, LocalService.class);
        intent.putExtra("packageName", packageName);
        bindService(intent, connection, Service.BIND_AUTO_CREATE);
        startService(intent);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("selfCheck", "连接服务");
            LocalService.PlayBinder playBinder = (LocalService.PlayBinder) service;
            mLocalService = playBinder.getPlayService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("selfCheck", "断开服务");
        }
    };

    //包名
    public boolean isAppInstalled(Context context, String packageName) {
        List<PackageInfo> pinfo = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            String pn = pinfo.get(i).packageName;
            if (pn.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static void appendSuccess(final String s) {
        ((MainActivity) Utils.mainContext).runOnUiThread(() -> {
            flagSuccess++;
            tvSuccess.append(flagSuccess + ":" + s + "\n");
        });
    }

    public static void appendErr(final String s) {
        ((MainActivity) Utils.mainContext).runOnUiThread(() -> {
            flagErr++;
            tvErr.append(flagErr + ":" + s + "\n");
        });
    }

    public static void appendHint(final String s) {
        ((MainActivity) Utils.mainContext).runOnUiThread(() -> {
            tvHint.setText("");
            tvHint.append(s + "\n");
            if ("检测完成!".equals(s)) {

            }
        });
    }
}
