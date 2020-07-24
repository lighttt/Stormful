package com.example.trystromful.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.trystromful.data.WeatherContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class StormfulSyncUtils {
    private static final String TAG = "StormfulSyncUtils";

    //interval times
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FIXTIME_SECONDS = SYNC_INTERVAL_SECONDS/3;

    private static boolean sInitialized;

    //unique tag for jobs
    private static final String STORMFUL_SYNC_TAG = "stromful-sync";


    /**
     * Helps to dispatch our job
     */
    static void scheduleFirebaseJobDispatcher(@NonNull final Context context)
    {
        Log.e(TAG,"Weather is syncing");
        //initializing
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        //job create
        Job syncWeatherJob = dispatcher.newJobBuilder()
                .setService(StormfulFirebaseJobService.class)
                .setTag(STORMFUL_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FIXTIME_SECONDS
                ))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncWeatherJob);
    }

    /**
     * It helps to immediately begin our sync and get the weather data that will be stored in database
     */
   synchronized public static void initialize(@NonNull final Context context)
    {
        if(sInitialized)
        {
            return;
        }
        sInitialized = true;
        //gets the weather data in 4 hours
        scheduleFirebaseJobDispatcher(context);

        //if we don't have data present we check it through cursor and run our service
        //the weather data should not be query on the ui thread so we must create a thread that can handle it
        Thread checkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri forecastUri = WeatherContract.WeatherEntry.CONTENT_URI;

                String[] projectionColumns = {WeatherContract.WeatherEntry._ID};
                String selectionStatement = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                Cursor cursor = context.getContentResolver().query(
                        forecastUri,
                        projectionColumns,
                        selectionStatement,
                        null,
                        null);

                //check cursor for validation
                if(cursor == null || cursor.getCount() == 0)
                {
                    //immediately sync new weather data
                    Log.e(TAG,"Weather is syncing");
                    startImmediatelySync(context);
                }
                cursor.close();
            }
        });
        checkThread.start();
    }


    /**
     * Helps to immediately run the get weather data tasks
     */
    public static void startImmediatelySync(@NonNull final  Context context)
    {
        Intent intentImmediately = new Intent(context,StormfulSyncIntentService.class);
        context.startService(intentImmediately);
    }

}
