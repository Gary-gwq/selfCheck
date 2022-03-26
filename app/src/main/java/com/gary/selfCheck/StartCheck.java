package com.gary.selfCheck;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.util.Log;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodImplementation;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.dexlib2.util.SyntheticAccessorResolver;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StartCheck {

    private Context targetContext;//需要检测的apk中的Context
    private Context mainContext;
    private boolean isMatchingApplicationName = false;//检测的包和配置文件中是否匹配applicationName
    private String targetApplicationName;//需要检测的包内applicationName
    private String configApplicationName = "";//配置好的applicationName
    private String targetPackageName = "";//需要检测的包名
    private SyntheticAccessorResolver sar;
    private ConfigInfo mConfigInfo;
    private List<String> listMethod;
    private List<String> listJoinMethodSuccess;
    private List<String> listCodeNoScan;
    private List<DexFile> listDex;

    public StartCheck(String packageName) {
        mainContext = Utils.mainContext;
        targetPackageName = packageName;
        targetContext = Utils.getContext(mainContext, targetPackageName);
    }

    public void configCheck() {
        mConfigInfo = ConfigInfo.getInstance(mainContext);
        Utils.jsonObject = TargetAppInfo.GetConfiguration(mainContext);
        checkAllPackageInfo();
    }

    private void checkAllPackageInfo() {
        checkApplicationInfo();
        checkAssets();
        checkSoFile();
        checkActivities();
        checkPermissions();
        checkMetaData();
        checkProviders();
        checkService();
//        checkReceivers();
        MainActivity.appendHint("正在检测代码是否接入合格，请稍后！！！");
        checkCode();
        MainActivity.appendHint("检测完成!");
        ((MainActivity) mainContext).stopService();
    }


    private boolean startCheckApplication(DexFile dexFile, String configApplicationName, String targetApplicationName) {
        for (ClassDef classDef : dexFile.getClasses()) {
            if (classDef.getType().contains(configApplicationName)) {
                for (Method method : classDef.getMethods()) {
                    if (method.getName().equals("<config>")) {
                        MethodImplementation methodImpl = method.getImplementation();
                        for (Instruction instruction : methodImpl.getInstructions()) {
                            Opcode opcode1 = instruction.getOpcode();
                            if (opcode1 == Opcode.INVOKE_SUPER || opcode1 == Opcode.INVOKE_DIRECT) {
                                Reference reference = ((ReferenceInstruction) instruction).getReference();
                                String name = ((MethodReference) reference).getDefiningClass();
                                if (name.contains(targetApplicationName)) {
                                    MainActivity.appendSuccess("继承SDK的Application合格");
                                    Utils.application_dispense = -1;
                                    return true;
                                }
                                while (Utils.application_dispense > 1) {
                                    Utils.application_dispense--;
                                    this.configApplicationName = name;
                                    startCheckAppLicationName(listDex);
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private String pointToSlash(String str) {
        if (str.contains(".")) {
            StringBuilder sb = new StringBuilder();
            String[] split = str.split("\\.");
            for (String s : split) {
                sb.append(s).append("/");
            }
            sb = sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } else {
            return str;
        }
    }

    private boolean checkApplicationName(DexFile dexFile) {
        if (configApplicationName != null && targetApplicationName != null && !configApplicationName.isEmpty() && !targetApplicationName.isEmpty()) {
            if (configApplicationName.equals(targetApplicationName)) {
                MainActivity.appendSuccess("xml配置Application-name合格");
                return true;
            } else {
                configApplicationName = pointToSlash(configApplicationName);
                targetApplicationName = pointToSlash(targetApplicationName);
                return startCheckApplication(dexFile, configApplicationName, targetApplicationName);
            }
        } else {
            return false;
        }
    }

    //必要方法是否存在---不知道怎么验证方法是否调用
    private boolean checkMethod(DexFile dexFile) {
        boolean isGetMethods = false;
        for (ClassDef classDef : dexFile.getClasses()) {
            int flag = 0;
            String type = classDef.getType();
            for (String codeNoScan : listCodeNoScan) {
                if (!type.contains(pointToSlash(codeNoScan))) {
                    flag++;
                    if (flag == listCodeNoScan.size()) {
                        isGetMethods = getMethods(classDef);
                    }
                }
            }
        }
        return isGetMethods;
    }

    private boolean getMethods(ClassDef classDef) {
        String APIName = pointToSlash(Utils.api_name);
        for (Method method : classDef.getMethods()) {
            MethodImplementation methodImpl = method.getImplementation();
            try {
                for (Instruction instruction : methodImpl.getInstructions()) {
                    Opcode opcode1 = instruction.getOpcode();
                    if (opcode1 == Opcode.INVOKE_VIRTUAL || opcode1 == Opcode.INVOKE_STATIC) {
                        Reference reference = ((ReferenceInstruction) instruction).getReference();
                        String dexName = ((MethodReference) reference).getDefiningClass();
                        if (dexName.contains(APIName)) {
                            String name2 = ((MethodReference) reference).getName();
                            if (!listMethod.isEmpty()) {
                                for (int i = 0; i < listMethod.size(); i++) {
                                    if (name2.equals(listMethod.get(i))) {
                                        listJoinMethodSuccess.add(name2);
                                        listMethod.remove(i);
                                        i--;
                                        Log.i("selfCheck", method.getDefiningClass() + method.getName());
                                    }
                                }
                            } else {
                                return true;
                            }
                        }
                    }
                }
            } catch (NullPointerException | ClassCastException e) {
                continue;
            }
        }
        return false;
    }

    private void checkClass(DexFile dexFile) {
        List<String> list = mConfigInfo.configClass();
        for (ClassDef classDef : dexFile.getClasses()) {
            for (int j = 0; j < list.size(); j++) {
                if (classDef.toString().equals(list.get(j))) {
                    list.remove(j);
                    j--;
                }
            }
        }
        Utils.isEmptyList(list, "必要类");
    }

    private void startCheckAppLicationName(List<DexFile> list) {
        for (DexFile dexFile : list) {
            sar = new SyntheticAccessorResolver(dexFile.getOpcodes(), dexFile.getClasses());
            if (!isMatchingApplicationName) {
                if (checkApplicationName(dexFile)) {
                    isMatchingApplicationName = true;
                }
            }
        }
    }

    private void startCheckMethod(List<DexFile> list) {
        boolean checkMethod = false;
        for (DexFile dexFile : list) {
            sar = new SyntheticAccessorResolver(dexFile.getOpcodes(), dexFile.getClasses());
            if (checkMethod(dexFile)) {
                checkMethod = true;
            }
        }
        if (listJoinMethodSuccess != null && !listJoinMethodSuccess.isEmpty()) {
            MainActivity.appendSuccess("已接入" + Utils.api_name + "." + listJoinMethodSuccess.toString());
        }
        if (!checkMethod && !listMethod.isEmpty()) {
            MainActivity.appendErr("未接入" + Utils.api_name + "." + listMethod.toString());
        }
    }

    //必要代码是否包含
    public void checkCode() {
        listDex = TargetAppInfo.GetDex(targetContext, targetPackageName);//获取apk中所有dex文件
        configApplicationName = TargetAppInfo.GetApplicationName(targetContext, targetPackageName);
        targetApplicationName = mConfigInfo.configApplicationName();
        if (targetApplicationName != null) {
            startCheckAppLicationName(listDex);
            if (!isMatchingApplicationName) {
                MainActivity.appendErr("接入SDK的Application不合格，请查看SDK文档.");
            }
        }
        configMethod(mConfigInfo.configApi("no_scan"));
    }

    private void configMethod(JSONObject jsonObject) {
        if (jsonObject != null && jsonObject.length() != 0) {
            try {
                for (Iterator<String> i = jsonObject.keys(); i.hasNext(); ) {
                    String need = i.next();
                    listCodeNoScan = Utils.stringsToList(need.split(","));
                    JSONObject api = new JSONObject(jsonObject.getString(need));
                    for (Iterator<String> j = api.keys(); j.hasNext(); ) {
                        Utils.api_name = j.next();
                        listJoinMethodSuccess = new ArrayList<>();
                        listMethod = Utils.stringsToList(api.getString(Utils.api_name).split(","));
                        startCheckMethod(listDex);
                    }
                }
            } catch (NullPointerException | JSONException e) {
                MainActivity.appendErr("请合规配置code_api,否则会影响结果");
            }
        }
    }

    //So
    public void checkSoFile() {
        String[] strings = TargetAppInfo.GetSoFile(targetContext, targetPackageName);
        if (strings != null) {
            List<String> soFiles = Utils.stringsToList(strings);
            Utils.forStrings(soFiles, mConfigInfo.configSoFile(), "so文件");
        }
    }

    //assets
    public void checkAssets() {
        String[] assets = TargetAppInfo.GetAssets(targetContext, targetPackageName);
        Utils.forStrings(Utils.stringsToList(assets), mConfigInfo.configAssets(), "assets");
    }

    //Service
    public void checkService() {
        ServiceInfo[] services = TargetAppInfo.GetService(targetContext, targetPackageName);
        Utils.forComponentInfos(services, mConfigInfo.configServices(), "xml配置标签service");
    }

    //Receivers
    public void checkReceivers() {
        ActivityInfo[] receivers = TargetAppInfo.GetReceivers(targetContext, targetPackageName);
        Utils.forComponentInfos(receivers, mConfigInfo.configReceivers(), "xml配置标签receiver");
    }

    //Providers
    public void checkProviders() {
        ProviderInfo[] providers = TargetAppInfo.GetProviders(targetContext, targetPackageName);
        List<String> configProviders = mConfigInfo.configProviders(targetPackageName);
        if (providers == null || providers.length == 0 || configProviders == null || configProviders.isEmpty()) {
            return;
        }
        for (ProviderInfo provider : providers) {
            String name = provider.name;
            String authority = provider.authority;
            for (int j = 0; j < configProviders.size(); j++) {
                try {
                    if (configProviders.get(j).equals(name)) {
                        configProviders.remove(j);
                        j--;
                        if (ConfigInfo.mProvidersJS.getString(name).equals(authority)) {
                            break;
                        }
                        MainActivity.appendErr(name + "的authority错误!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Utils.isEmptyList(configProviders, "xml配置标签provider");
    }

    //权限
    public void checkPermissions() {
        String[] requestedPermissions = TargetAppInfo.GetPermissions(targetContext, targetPackageName);
        Utils.forStrings(Utils.stringsToList(requestedPermissions), mConfigInfo.configPermissions(), "xml配置标签permission");
    }

    //activity
    public void checkActivities() {
        ActivityInfo[] activities = TargetAppInfo.GetActivities(targetContext, targetPackageName);
        Utils.forComponentInfos(activities, mConfigInfo.configActivities(), "xml配置标签activity");
    }

    //显名和icon
    public void checkApplicationInfo() {
        TargetAppInfo.GetApplicationInfo(targetContext, targetPackageName);
    }

    //metaData
    public void checkMetaData() {
        Set setMDs = TargetAppInfo.GetMetaData(targetContext, targetPackageName);
        List<String> configMetaDatas = mConfigInfo.configMetaDatas();
        if (setMDs == null) {
            MainActivity.appendErr("metaData缺失：" + configMetaDatas.toString());
            return;
        }
        List list = new ArrayList(setMDs);
        Utils.forStrings(list, configMetaDatas, "xml配置标签metaData");
    }

}
