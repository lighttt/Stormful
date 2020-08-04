package com.example.trystromful.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.example.trystromful.DetailActivity;
import com.example.trystromful.R;
import com.example.trystromful.data.StormfulPreferences;
import com.example.trystromful.data.WeatherContract;

public class NotificationUtils {

    private static final int WEATHER_NOTIFICATION_ID = 123;
    private static final String WEATHER_CHANNEL_ID = "weather-channel";
    private static final int WEATHER_NOTIFICATION_PENDING_INTENT_ID = 321;

    //projection and indexes
    // weather columns that are displayed and queried
    public static final String[] WEATHER_NOTIFICATION_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
    };

    // weather table column ko indexes
    public static final int INDEX_WEATHER_CONDITION_ID = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;


    public static void notifyUserOfNewWeather(Context context)
    {
        //show today date in the notification
        Uri todayWeatherUri = WeatherContract.WeatherEntry.
                buildWeatherUriWithDate(StormfulDateUtils.normalizeDate(System.currentTimeMillis()));

        //get the weather data to display as well
        Cursor todayWeatherCursor = context.getContentResolver().query(
                todayWeatherUri,
                WEATHER_NOTIFICATION_PROJECTION,
                null,
                null,
                null);

        //cursor ma bhako data lina
        if(todayWeatherCursor.moveToFirst())
        {
            int weatherId = todayWeatherCursor.getInt(INDEX_WEATHER_CONDITION_ID);
            double high = todayWeatherCursor.getDouble(INDEX_WEATHER_MAX_TEMP);
            double low = todayWeatherCursor.getDouble(INDEX_WEATHER_MIN_TEMP);

            //large icon
            Resources res = context.getResources();
            int largeIconResourceId = StormfulWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);

            Bitmap largeIcon = BitmapFactory.decodeResource(res,R.drawable.ic_baseline_desktop_mac_24);

            String notificationTitle = "Stormful";
            String notificationText =getNotificationText(context,weatherId,high,low);

            int smallIconResourceId = StormfulWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);

            // make the notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //oreo bhanda mathi ko devices ho bhane you need notification channel
            // haina bhane you dont need it

            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)
            {
                NotificationChannel channel = new NotificationChannel(
                        WEATHER_CHANNEL_ID,
                        "Weather",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            //notification
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(smallIconResourceId)
                    .setLargeIcon(largeIcon)
                    .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                    .setAutoCancel(true);

            //content intent for going to detail
            Intent detailIntentForToday = new Intent(context, DetailActivity.class);
            detailIntentForToday.setData(todayWeatherUri);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);
            PendingIntent resultPendingIntent = taskStackBuilder
                    .getPendingIntent(456, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setContentIntent(resultPendingIntent);

            //use notification manager to show notification finally
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            {
                notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            }
            notificationManager.notify(WEATHER_NOTIFICATION_ID,notificationBuilder.build());

            //save the last notification sent
            StormfulPreferences.saveLastNotificationTime(context,System.currentTimeMillis());
        }
        //always close cursor
        todayWeatherCursor.close();
    }

    /**
     * Create a notification text to display
     */
    private static String getNotificationText(Context context, int weatherId, double high, double low)
    {
        String shortDesc = StormfulWeatherUtils.getStringForWeatherCondition(context,weatherId);

        String notificationFormat = context.getString(R.string.format_notification);
        String notificationText = String.format(notificationFormat,
                shortDesc,
                StormfulWeatherUtils.formatTemperature(context,high),
                StormfulWeatherUtils.formatTemperature(context,low));

        return notificationText;
    }
}
