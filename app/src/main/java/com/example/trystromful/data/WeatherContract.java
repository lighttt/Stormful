package com.example.trystromful.data;

import android.provider.BaseColumns;

public class WeatherContract {

    //inner class
    public static final class WeatherEntry implements BaseColumns{
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
