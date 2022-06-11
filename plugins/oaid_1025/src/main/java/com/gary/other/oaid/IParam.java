package com.gary.other.oaid;

import android.content.Context;
import java.util.HashMap;

public interface IParam extends IPlugin {
    int PLUGIN_TYPE_OAID = 8;

    HashMap<String, String> getParam(Context var1);
}
