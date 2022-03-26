package com.gary.selfCheck;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 获取配置文件中需要检测的东西
 */
public class ConfigInfo {

    private static ConfigInfo mConfigInfo;
    private static Context mContext;
    private static String mPackageName;
    private List<String> mPermissions;
    private List<String> mMetaDatas;
    private List<String> mActivities;
    private List<String> mProviders;
    private List<String> mReceivers;
    private List<String> mServices;
    private List<String> mAssets;
    private List<String> mCodes;
    private List<String> mSoFile;
    private List<String> mCodeNoScan;
    public static JSONObject mProvidersJS = new JSONObject();

    private ConfigInfo() {
    }

    public static ConfigInfo getInstance(Context context) {
        if (mConfigInfo == null) {
            mConfigInfo = new ConfigInfo();
        }
        mPackageName = context.getPackageName();
        mContext = Utils.getContext(context, mPackageName);
        return mConfigInfo;
    }

    public List<String> configCodeNoScan() {
        mCodeNoScan = new ArrayList<>();
        List<String> codeNoScan = TargetAppInfo.JsonToList("code_no_scan", mCodeNoScan);
        if (codeNoScan.isEmpty()) {
            MainActivity.appendErr("请配置code_no_scan,否则会影响结果");
            mCodeNoScan.add("com");
            return mCodeNoScan;
        }
        mCodeNoScan = Utils.filterList(codeNoScan);
        return mCodeNoScan;
    }

    public JSONObject configApi(String api) {
        JSONObject join_api = null;
        try {
            String names = Utils.jsonObject.getString("code_api");
            Utils.isConfiguration(names);
            JSONObject json_code = new JSONObject(names);
            String s = json_code.getString(api);
            join_api = new JSONObject(s);
        } catch (Exception e) {
            Utils.isConfiguration = false;
        }
        return join_api;
    }

    public String configApplicationName() {
        String application = null;
        try {
            String names = Utils.jsonObject.getString("code_application_name");
            Utils.isConfiguration(names);
            JSONObject jsonObject = new JSONObject(names);
            Utils.application_name = application = jsonObject.getString("name");
//            SelfCheckUtil.application_dispense = jsonObject.getInt("dispense");
        } catch (JSONException | NullPointerException e) {
            Utils.isConfiguration = false;
        }
        return application;
    }

    public List<String> configSoFile() {
        mSoFile = new ArrayList<>();
        String[] strings = TargetAppInfo.GetSoFile(mContext, mPackageName);
        if (strings != null) {
            mSoFile.addAll(Utils.stringsToList(strings));
        }
        List<String> soFile = TargetAppInfo.JsonToList("soFile", mSoFile);
        mSoFile = Utils.filterList(soFile);
        return mSoFile;
    }

    @Deprecated
    public List<String> configClass() {
        mCodes = new ArrayList<>();
        mCodes.add("Lcom/Application;");
        mCodes.add("Lcom/API;");
        return mCodes;
    }

    public List<String> configAssets() {
        mAssets = new ArrayList<>();
        List<String> strings = Utils.stringsToList(TargetAppInfo.GetAssets(mContext, mPackageName));
        mAssets.addAll(Utils.listAddString(strings, mAssets, "configuration.json"));
        List<String> assets = TargetAppInfo.JsonToList("assets", mAssets);
        mAssets = Utils.filterList(assets);
        return mAssets;
    }

    public List<String> configServices() {
        mServices = new ArrayList<>();
        ServiceInfo[] serviceInfos = TargetAppInfo.GetService(mContext, mPackageName);
        mServices.addAll(Utils.listAddComponentInfos(serviceInfos, mServices, "com.gary.selfCheck.LocalService"));
        List<String> services = TargetAppInfo.JsonToList("services", mServices);
        mServices = Utils.filterList(services);
        return mServices;
    }

    public List<String> configReceivers() {
//        mReceivers.clear();
//        mReceivers.add("");
        return mReceivers;
    }

    public List<String> configPermissions() {
        mPermissions = new ArrayList<>();
        String[] strings = TargetAppInfo.GetPermissions(mContext, mPackageName);
        if (strings != null) {
            mPermissions.addAll(Utils.stringsToList(strings));
        }
        List<String> permission = TargetAppInfo.JsonToList("permission", mPermissions);
        mPermissions = Utils.filterList(permission);
        return mPermissions;
    }

    public List<String> configMetaDatas() {
        mMetaDatas = new ArrayList<>();
        Set setMDs = TargetAppInfo.GetMetaData(mContext, mPackageName);
        if (setMDs != null) {
            mMetaDatas.addAll(setMDs);
        }
        List<String> metaData = TargetAppInfo.JsonToList("metaData", mMetaDatas);
        mMetaDatas = Utils.filterList(metaData);
        return mMetaDatas;
    }

    public List<String> configActivities() {
        mActivities = new ArrayList<>();
        ActivityInfo[] activityInfos = TargetAppInfo.GetActivities(mContext, mPackageName);
        mActivities.addAll(Utils.listAddComponentInfos(activityInfos, mAssets, "com.gary.selfCheck.MainActivity"));
        List<String> activity = TargetAppInfo.JsonToList("activity", mActivities);
        mActivities = Utils.filterList(activity);
        return mActivities;
    }

    public List<String> configProviders(String packageName) {
        mProviders = new ArrayList<>();
        try {
            ProviderInfo[] providerInfos = TargetAppInfo.GetProviders(mContext, mPackageName);
            if (providerInfos != null) {
                for (ProviderInfo p : providerInfos) {
                    String name = p.name;
                    String authority = p.authority;
                    authority = authority.replace(mPackageName, packageName);
                    mProviders.add(name);
                    mProvidersJS.put(name, authority);
                }
            }
            String names = Utils.jsonObject.getString("provider");
            Utils.isConfiguration(names);
            JSONObject jsonObject = new JSONObject(names);
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String need = it.next();
                mProviders.add(need);
                mProvidersJS.put(need, packageName + jsonObject.getString(need));
            }
            mProviders = Utils.filterList(mProviders);
        } catch (JSONException | NullPointerException e) {
            Utils.isConfiguration = false;
            return mProviders;
        }
        return mProviders;
    }
}
