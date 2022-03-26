package com.gary.other.reflect;

import android.content.res.Configuration;
import android.util.Log;

import com.gary.selfCheck.Utils;

public class ReflectTest {

    public void testResume() {
        Log.i(Utils.TAG,"testResume");
    }

    public void testOnWindowFocusChanged(boolean b) {
        Log.i(Utils.TAG,"testOnWindowFocusChanged");
    }

    public void testOnConfigurationChanged(Configuration configuration) {
        Log.i(Utils.TAG,"testOnConfigurationChanged");
    }
}
