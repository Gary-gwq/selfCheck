package com.gary.other.oaid;

import android.content.Context;
import android.util.Log;

import com.bun.miitmdid.core.MdidSdk;
import com.bun.supplier.IIdentifierListener;
import com.bun.supplier.IdSupplier;

import java.util.HashMap;

/**
 * 1.0.13版本和其他版本共同存在时，可能会有异常产生，处理方式如下
 *  1.使用同一个版本
 *  2.其他版本先调用，之后再调用1.0.13版本的oaid获取
 */
public class ParamImpl1013 implements IParam {

    public ParamImpl1013() {
    }

    @Override
    public boolean isSupportMethod(String methodName) {
        return true;
    }

    @Override
    public HashMap<String, String> getParam(Context context) {
        //需要提前调用下面注释的方法，不然会报错或者获取不到oaid
//        try {
//            Class<?> cls = Class.forName("com.bun.miitmdid.core.JLibrary");
//            Method method = cls.getMethod("InitEntry", Context.class);
//            method.invoke(null, context);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("selfCheck", "未添加oaid插件");
//        }

        return getOaid(context);
    }

    private HashMap<String, String> getOaid(Context context) {
        final String[] oaid = {null};
        final HashMap<String, String> paramMap = new HashMap<String, String>();
        if (context == null) {
            return null;
        }
        try {
            MdidSdk sdk = new MdidSdk();
            sdk.InitSdk(context, new IIdentifierListener() {
                @Override
                public void OnSupport(boolean b, IdSupplier idSupplier) {
                    if (idSupplier == null) {
                        Log.e("selfCheck", "oaid获取 不支持");
                        return;
                    }
                    oaid[0] = idSupplier.getOAID();
                    Log.e("selfCheck", "oaid=" + oaid[0]);
                    paramMap.put("device-oa_id", oaid[0]);
                }
            });
        } catch (Exception e) {
            paramMap.put("device-oa_id", "");
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        while (paramMap.get("device-oa_id") == null) {
            endTime = System.currentTimeMillis();
            if ((endTime - startTime) > 1000) {
                paramMap.put("device-oa_id", "");
                return paramMap;
            }
        }
        if ("00000000-0000-0000-0000-000000000000".equals(paramMap.get("device-oa_id"))) {
            paramMap.put("device-oa_id", "");
        }
        return paramMap;
    }
}
