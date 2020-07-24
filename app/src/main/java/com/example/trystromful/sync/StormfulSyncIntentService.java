package com.example.trystromful.sync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

public class StormfulSyncIntentService extends IntentService {
    private static final String TAG = "StormfulSyncIntentServi";

    public StormfulSyncIntentService() {
        super("StormfulSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e(TAG,"Weather is syncing");
        StormfulSyncTask.syncWeather(this);
    }
}
