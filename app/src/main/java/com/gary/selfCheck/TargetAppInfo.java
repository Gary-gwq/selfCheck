package com.gary.selfCheck;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.os.Bundle;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.MultiDexContainer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 需要检测的app的基础信息
 */
public class TargetAppInfo {

    private TargetAppInfo() {
    }

    //获取所有dex文件
    public static List<DexFile> GetDex(Context context, String packageName) {
        String path = GetAppPath(context, PackageManager.GET_GIDS, packageName);
        DexFile dexFile;
        List<DexFile> list = new ArrayList<>();
        try {
            MultiDexContainer<? extends DexBackedDexFile> container =
                    DexFileFactory.loadDexContainer(new File(path), Opcodes.getDefault());
            List<String> dexList = container.getDexEntryNames();
            for (String dex : dexList) {
                dexFile = DexFileFactory.loadDexEntry(new File(path), dex, true, Opcodes.getDefault()).getDexFile();
                list.add(dexFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    //获取so文件
    public static String[] GetSoFile(Context context, String PackageName) {
        String path = GetAppInfo(PackageManager.GET_GIDS, context, PackageName).nativeLibraryDir;
        File file = new File(path);
        return file.list();
    }

    //获取App路径
    public static String GetAppPath(Context context, int flags, String PackageName) {
        try {
            return context.getPackageManager().getApplicationInfo(PackageName, flags).sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取application name
    public static String GetApplicationName(Context context, String PackageName) {
        return GetAppInfo(PackageManager.GET_GIDS, context, PackageName).className;
    }

    //获取App的配置文件
    public static JSONObject GetConfiguration(Context context) {
        JSONObject jsonObject = null;
        try {
            AssetManager assets = context.getAssets();
            InputStream is = assets.open("configuration.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            jsonObject = new JSONObject(json);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    //获取配置文件中的json并添加到list
    public static List<String> JsonToList(String name, List<String> list) {
        try {
            String names = Utils.jsonObject.getString(name);
            Utils.isConfiguration(names);
            JSONObject jsonObject = new JSONObject(names);
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String need = it.next();
                if (jsonObject.getString(need).equals("0") && !list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).equals(need)) {
                            list.remove(i);
                            i--;
                        }
                    }
                } else if (jsonObject.getString(need).equals("1")) {
                    list.add(need);
                }
            }
        } catch (JSONException | NullPointerException e) {
            Utils.isConfiguration = false;
            return list;
        }
        return list;
    }

    public static JSONObject JsonToJson(String name) {
        JSONObject jsonObject = null;
        try {
            String names = Utils.jsonObject.getString(name);
            jsonObject = new JSONObject(names);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    //获取assets
    public static String[] GetAssets(Context context, String PackageName) {
        String[] list = new String[0];
        try {
            list = context.getResources().getAssets().list("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.isEmptyObject(list, PackageName + "的assets文件夹");
        return list;
    }

    //获取Service
    public static ServiceInfo[] GetService(Context context, String PackageName) {
        return GetPackInfo(PackageManager.GET_SERVICES, context, PackageName).services;
    }

    //获取Receivers
    public static ActivityInfo[] GetReceivers(Context context, String PackageName) {
        return GetPackInfo(PackageManager.GET_RECEIVERS, context, PackageName).receivers;
    }

    //获取Providers
    public static ProviderInfo[] GetProviders(Context context, String PackageName) {
        return GetPackInfo(PackageManager.GET_PROVIDERS, context, PackageName).providers;
    }

    //获取权限
    public static String[] GetPermissions(Context context, String PackageName) {
        return GetPackInfo(PackageManager.GET_PERMISSIONS, context, PackageName).requestedPermissions;
    }

    //获取activity
    public static ActivityInfo[] GetActivities(Context context, String PackageName) {
        return GetPackInfo(PackageManager.GET_ACTIVITIES, context, PackageName).activities;
    }

    public static String GetPackageName(Context context, String PackageName) {
        ApplicationInfo applicationInfo = TargetAppInfo.GetPackInfo(PackageManager.GET_GIDS, context, PackageName).applicationInfo;
        return applicationInfo.packageName;
    }

    //获取显名和icon
    public static void GetApplicationInfo(Context context, String PackageName) {
        ApplicationInfo applicationInfo = GetPackInfo(PackageManager.GET_GIDS, context, PackageName).applicationInfo;
        CharSequence charSequence = applicationInfo.loadLabel(context.getPackageManager());
        MainActivity.appendSuccess("显名:" + charSequence);
//        Drawable drawable = applicationInfo.loadIcon(context.getPackageManager());
//        MainActivity.appendStr("icon:" + drawable.toString());
    }

    //获取metaData
    public static Set<String> GetMetaData(Context context, String PackageName) {
        Bundle metaData = GetAppInfo(PackageManager.GET_META_DATA, context, PackageName).metaData;
        if (metaData == null) {
            return null;
        }
        return metaData.keySet();
    }

    //获取PackageInfo
    public static PackageInfo GetPackInfo(int flags, Context context, String PackageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(PackageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Utils.isEmptyObject(packageInfo, PackageName);
        return packageInfo;
    }

    //获取ApplicationInfo
    public static ApplicationInfo GetAppInfo(int flags, Context context, String PackageName) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(PackageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Utils.isEmptyObject(applicationInfo, PackageName);
        return applicationInfo;
    }

}
