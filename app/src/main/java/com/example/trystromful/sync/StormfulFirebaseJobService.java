package com.example.trystromful.sync;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class StormfulFirebaseJobService extends JobService {

    private AsyncTask<Void,Void,Void> mFetchWeatherTask;
    private static final String TAG = "StormfulFirebaseJobServ";

    @Override
    public boolean onStartJob(@NonNull final JobParameters jobParameters) {
        Log.e(TAG,"Weather is syncing");
        mFetchWeatherTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                StormfulSyncTask.syncWeather(getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters,false);
            }
        };
        mFetchWeatherTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters job) {
        if(mFetchWeatherTask!=null)
        {
            mFetchWeatherTask.cancel(true);
        }
        return true;
    }
}
