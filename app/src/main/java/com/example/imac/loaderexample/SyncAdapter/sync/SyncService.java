package com.example.imac.loaderexample.SyncAdapter.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by imac on 5/25/17.
 */

public class SyncService extends Service {

    public static SyncAdapter mSyncAdapter;
    private static final Object mSyncObj = new Object();

    @Override
    public void onCreate() {
        Log.e("SyncService", "onCreate");
        synchronized (mSyncObj){
            if(mSyncAdapter == null)
                mSyncAdapter=new SyncAdapter(this,true);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
