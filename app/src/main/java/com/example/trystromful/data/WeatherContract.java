package com.example.trystromful.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.trystromful.utilities.StormfulDateUtils;

public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.example.trystromful";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //path
    public static final String PATH_WEATHER = "weather";


    //inner class
    public static final class WeatherEntry implements BaseColumns{

        // content provider uri/list
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build();

        //particular uri date/item
        // content://........./15023102301
        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .build();
        }

        //table name
        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_DATE = "date";
        //weather / temperature
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // temperature details
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_DEGREES = "degrees";

    }
}
