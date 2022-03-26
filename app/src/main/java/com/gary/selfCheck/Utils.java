package com.gary.selfCheck;

import android.content.Context;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Utils {

    public static String TAG = "selfCheck";
    public static Context mainContext = null;
    public static JSONObject jsonObject = null;
    public static String application_name = "";
    public final static int DISPENSE = 20;
    public static int application_dispense = DISPENSE;
    public static String api_name = "";
    public static String dex_class_name = "dex_class_name=";
    public static boolean isConfiguration = true;

    private Utils() {
    }

    public static void isConfiguration(String js) {
        if (js == null || js.isEmpty()) {
            isConfiguration = false;
        }
    }

    public static List<String> stringsToList(String[] strings) {
        List<String> list = new ArrayList<>();
        if (strings.length != 0) {
            for (String s : strings) {
                if (!s.trim().equals("")) {
                    list.add(s.trim());
                }
            }
        }
        return list;
    }

    public static Context getContext(Context context, String packageName) {
        Context packageContext = null;
        try {
            packageContext = context.createPackageContext(packageName, Context.CONTEXT_RESTRICTED);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageContext;
    }

    public static void isEmptyList(List<String> list, String name) {
        if (isConfiguration) {
            if (list.isEmpty()) {
                MainActivity.appendSuccess(name + "合格");
            } else {
                MainActivity.appendErr(name + "缺失：" + list.toString());
            }
        }
        isConfiguration = true;
    }

    public static void isEmptyObject(Object object, String name) {
        if (object == null) {
            MainActivity.appendErr(name + "不存在");
        }
    }

    public static void forStrings(List<String> strings, List<String> list, String name) {
        if (list == null || list.isEmpty()) {
            if (isConfiguration) {
                MainActivity.appendSuccess(name + "合格");
            }
            isConfiguration = true;
            return;
        }
        if (strings != null) {
            for (String string : strings) {
                for (int j = 0; j < list.size(); j++) {
                    if (string.equals(list.get(j))) {
                        list.remove(j);
                        j--;
                    }
                }
            }
        }
        isEmptyList(list, name);
    }

    public static void forComponentInfos(ComponentInfo[] componentInfos, List<String> list, String name) {
        if (list == null || list.isEmpty()) {
            if (isConfiguration) {
                MainActivity.appendSuccess(name + "合格");
            }
            isConfiguration = true;
            return;
        }
        if (componentInfos != null) {
            for (ComponentInfo componentInfo : componentInfos) {
                for (int j = 0; j < list.size(); j++) {
                    if (componentInfo.name.equals(list.get(j))) {
                        list.remove(j);
                        j--;
                    }
                }
            }
        }
        isEmptyList(list, name);
    }

    public static List<String> listAddString(List<String> list, List<String> listAdd, String name) {
        if (list != null) {
            for (String s : list) {
                if (s.equals(name)) {
                    continue;
                }
                listAdd.add(s);
            }
        }
        return listAdd;
    }

    public static List<String> listAddComponentInfos(ComponentInfo[] componentInfos, List<String> listAdd, String name) {
        if (componentInfos != null) {
            for (ComponentInfo s : componentInfos) {
                if (s.name.equals(name)) {
                    continue;
                }
                listAdd.add(s.name);
            }
        }
        return listAdd;
    }

    public static List<String> filterList(List<String> list) {
        if (list == null) {
            return null;
        }
        Set<String> infoSet = new TreeSet<>(String::compareTo);
        infoSet.addAll(list);
        return new ArrayList<>(infoSet);
    }
}
