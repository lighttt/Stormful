package com.example.trystromful.data;

import android.content.Context;

public class StormfulPreferences {

    private static final String PREF_CITY_NAME = "Kathmandu";


    private static final String DEFAULT_WEATHER_LOCATION = "Kathmandu,Nepal";
    private static final double[] DEFAULT_WEATHER_COORDINATES = {27.7172, 85.3240};

    public static boolean isMetric(Context context) {
        return true;
    }

    public static String getPreferredWeatherLocation(Context context) {
        return getDefaultWeatherLocation();
    }

    private static String getDefaultWeatherLocation() {
        return DEFAULT_WEATHER_LOCATION;
    }

    public static double[] getDefaultWeatherCoordinates() {
        return DEFAULT_WEATHER_COORDINATES;
    }
}
