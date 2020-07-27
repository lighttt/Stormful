package com.example.trystromful.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.trystromful.R;

public class StormfulPreferences {

    private static final String PREF_CITY_NAME = "Kathmandu";


    private static final String DEFAULT_WEATHER_LOCATION = "Kathmandu, Nepal";
    private static final double[] DEFAULT_WEATHER_COORDINATES = {27.7172, 85.3240};

    private static final String PREF_COORD_LAT = "coord_lat";
    private static final String PREF_COORD_LONG = "coord_long";


    // =========================== TEMPERATURE ==========================================


    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForUnits = context.getString(R.string.pref_units_key);
        String defaultUnits = context.getString(R.string.pref_units_metric);
        String preferredUnits = prefs.getString(keyForUnits, defaultUnits);
        String metric = context.getString(R.string.pref_units_metric);
        boolean userPrefersMetric;
        if (metric.equals(preferredUnits)) {
            userPrefersMetric = true;
        } else {
            userPrefersMetric = false;
        }
        return userPrefersMetric;
    }


    // =========================== LOCATION ==========================================

    public static String getPreferredWeatherLocation(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForLocation = context.getString(R.string.pref_location_key);
        String defaultLocation = context.getString(R.string.pref_location_default);
        return prefs.getString(keyForLocation, defaultLocation);
    }

    private static String getDefaultWeatherLocation() {
        return DEFAULT_WEATHER_LOCATION;
    }

    public static double[] getDefaultWeatherCoordinates() {
        return DEFAULT_WEATHER_COORDINATES;
    }


    /**
     *  Setting to location from json result in preferences
     * @param context :
     * @param lat :
     * @param lon :
     */
    public static void setLocationDetails(Context context, double lat, double lon)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        //editor
        SharedPreferences.Editor editor = sp.edit();

        editor.putLong(PREF_COORD_LAT, Double.doubleToRawLongBits(lat));
        editor.putLong(PREF_COORD_LONG, Double.doubleToRawLongBits(lon));
        editor.apply();
    }

    /**
     *  Getting the location coordinates that is set from json result
     * @param context :
     * @return : coordinates
     */
    public static double[] getLocationCoordinates(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        double[] preferredCoordinates = new double[2];

        preferredCoordinates[0] = Double.longBitsToDouble(sp.
                getLong(PREF_COORD_LAT, Double.doubleToRawLongBits(0.0)));
        preferredCoordinates[1] = Double.longBitsToDouble(sp.
                getLong(PREF_COORD_LONG, Double.doubleToRawLongBits(0.0)));

        return preferredCoordinates;
    }

    /**
     * Check whether our location has lat and long
     */
    public static boolean isLocationLatLonAvailable(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean spContainLat = sp.contains(PREF_COORD_LAT);
        boolean spContainLong = sp.contains(PREF_COORD_LONG);

        boolean spContainBothLatLong = false;
        if(spContainLat && spContainLong)
        {
            spContainBothLatLong = true;
        }
        return spContainBothLatLong;
    }

    /**
     * It resets our location coordinates
     */
    public static void resetLocationCoordinates(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.remove(PREF_COORD_LONG);
        editor.remove(PREF_COORD_LAT);
        editor.apply();
    }

    // =========================== NOTIFICATIONS ==========================================

    /**
     *  Check if the user has enabled notifications
     */
    public static boolean areNotificationsEnabled(Context context)
    {
        String notifyKey = context.getString(R.string.pref_enable_notification_key);

        boolean displayNotifyDefault = context.getResources().getBoolean(R.bool.show_notifications_default);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        boolean displayNotify = sp.getBoolean(notifyKey,displayNotifyDefault);
        return displayNotify;
    }

    /**
     *  Get the last time you notified user
     */
    public static long getTimeSinceLastNotify(Context context)
    {
        long lastNotificationInMillis = getLastNotificationInMillis(context);
        long timeSinceLastNotification = System.currentTimeMillis() - lastNotificationInMillis;
        return timeSinceLastNotification;
    }


    /**
     * Save the last time you notify user
     */
    public static void saveLastNotificationTime(Context context, long timeOfNotification)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        editor.putLong(lastNotificationKey,timeOfNotification);
        editor.apply();
    }

    public static long getLastNotificationInMillis(Context context)
    {
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        long lastNotificationTime = sharedPreferences.getLong(lastNotificationKey,0);
        return lastNotificationTime;
    }


}
