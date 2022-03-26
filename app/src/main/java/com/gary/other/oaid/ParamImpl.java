package com.gary.other.oaid;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.gary.selfCheck.Utils;

import java.util.HashMap;

/**
 * 参数获取实现类
 */
public class ParamImpl implements IParam {

    public ParamImpl(Activity mainActivity) {
    }

    @Override
    public boolean isSupportMethod(String methodName) {
        return true;
    }

    @Override
    public HashMap<String, String> getParam(Context context) {
        final HashMap<String, String> paramMap = new HashMap<String, String>();
        try {
            MdidSdkHelper.InitSdk(Utils.mainContext, true, new IIdentifierListener() {
                @Override
                public void OnSupport(boolean isSupport, IdSupplier idSupplier) {
                    if (!isSupport || idSupplier == null) {
                        paramMap.put("isSupport", "false");
                        return;
                    }
                    Log.i(Utils.TAG, "idSupplier.getOAID()=" + idSupplier.getOAID());
                    paramMap.put("oa_id", idSupplier.getOAID());
                    paramMap.put("isSupport", "true");
                }
            });
        } catch (Exception e) {
            paramMap.put("isSupport", "false");
            e.printStackTrace();
        }
        while (paramMap.get("isSupport") == null ) {
        }
        return paramMap;
    }
}
