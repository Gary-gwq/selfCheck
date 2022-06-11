package com.gary.other.hook;

import android.app.FragmentController;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.lang.reflect.Method;

public class HookTest {

    public void hookActivityOnCreate() {
        try {
            Class viewClazz = Class.forName("android.app.FragmentController");
            Method createController = viewClazz.getDeclaredMethod("createController");
            if (!createController.isAccessible()) {
                createController.setAccessible(true);
            }
            Method dispatchCreate = viewClazz.getDeclaredMethod("dispatchCreate");
            if (!dispatchCreate.isAccessible()) {
                dispatchCreate.setAccessible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
