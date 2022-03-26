package com.gary.selfCheck;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

//import androidx.annotation.Keep;
//import androidx.annotation.Nullable;

public class LocalService extends IntentService {

    private StartCheck mStartCheck;

    public LocalService() {
        super("LocalService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LocalService(String name) {
        super(name);
    }

//    @Keep
    class PlayBinder extends Binder {
        public LocalService getPlayService() {
            return LocalService.this;
        }
    }

//    @Keep
//    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

//    @Keep
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String packageName = intent.getStringExtra("packageName");
            mStartCheck = new StartCheck(packageName);
            mStartCheck.configCheck();
        }
    }

}