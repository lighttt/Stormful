package com.example.trystromful.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.trystromful.R;

public class StormfulPreferences {

    private static final String PREF_CITY_NAME = "Kathmandu";


    private static final String DEFAULT_WEATHER_LOCATION = "Kathmandu, Nepal";
    private static final double[] DEFAULT_WEATHER_COORDINATES = {27.7172, 85.3240};

    public static boolean isMetric(Context context) {
        return true;
    }

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
}
