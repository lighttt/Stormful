package com.example.trystromful.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.example.trystromful.data.StormfulPreferences;
import com.example.trystromful.data.WeatherContract;
import com.example.trystromful.utilities.NetworkUtils;
import com.example.trystromful.utilities.NotificationUtils;
import com.example.trystromful.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class StormfulSyncTask {

    /**
     * Gets the weather from the network and parses it to the database/ content provider
     * @param context
     */
   synchronized public static void syncWeather(Context context)
    {
        try{
            URL weatherRequestUrl = NetworkUtils.getURL(context);
            //response
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
            //parse
            ContentValues[] weatherValues = OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context,jsonWeatherResponse);

            //assign to the content provider
            if(weatherValues!=null && weatherValues.length!=0)
            {
                //add to the content provider
                ContentResolver resolver = context.getContentResolver();
                //delete old data and add new one
                resolver.delete(WeatherContract.WeatherEntry.CONTENT_URI,
                        null,null);
                //new data
                resolver.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI,weatherValues);
            }
            // check if notifications are enabled
            boolean notificationsEnabled = StormfulPreferences.areNotificationsEnabled(context);

            //check if the notification was sent more than 1 day ago
            long timeSinceLastNotify = StormfulPreferences.getTimeSinceLastNotify(context);
            boolean oneDayHasPassed = false;
            if(timeSinceLastNotify>= DateUtils.DAY_IN_MILLIS)
            {
                oneDayHasPassed = true;
            }

            if(notificationsEnabled && oneDayHasPassed)
            {
                NotificationUtils.notifyUserOfNewWeather(context);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
