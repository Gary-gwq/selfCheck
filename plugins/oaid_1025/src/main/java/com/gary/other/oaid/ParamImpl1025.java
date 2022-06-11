package com.gary.other.oaid;

import android.content.Context;
import android.util.Log;

import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IdSupplier;

import java.util.HashMap;

/**
 *
 */
public class ParamImpl1025 implements IParam {

    public ParamImpl1025() {
    }

    @Override
    public boolean isSupportMethod(String methodName) {
        return true;
    }

    @Override
    public HashMap<String, String> getParam(Context context) {
        final String[] oaid = {null};
        final HashMap<String, String> paramMap = new HashMap<String, String>();
        if (context == null) {
            return null;
        }
        try {
            MdidSdkHelper.InitSdk(context, true, new com.bun.miitmdid.interfaces.IIdentifierListener() {
                @Override
                public void OnSupport(boolean isSupport, IdSupplier idSupplier) {
                    if (!isSupport || idSupplier == null) {
                        paramMap.put("device-oa_id", "");
                        return;
                    }
                    oaid[0] = idSupplier.getOAID();
                    Log.i("selfCheck","idSupplier.getOAID()=" + oaid[0]);
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
            if ((endTime-startTime)>1000) {
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
