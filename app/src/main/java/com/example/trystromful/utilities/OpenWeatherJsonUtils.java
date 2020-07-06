package com.example.trystromful.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.trystromful.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class OpenWeatherJsonUtils {
    //default json items
    //has all the temp of 14 days
    private static final String OWM_LIST = "list";
    private static final String OWM_WEATHER_ID = "id";

    //min and max temp
    private static final String OWM_MAX = "temp_max";
    private static final String OWM_MIN= "temp_min";

    // has weather description
    private static final String OWM_WEATHER = "weather";
    //has weather temperature
    private static final String OWM_TEMPERATURE = "main";
    private static final String OWM_DESCRIPTION = "main";

    //humidity pressure wind speed and wind direction
    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_HUMIDITY = "humidity";
    private static final String OWM_WIND = "wind";
    private static final String OWM_WIND_SPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";

    //connection status
    private static final String OWM_MESSAGE_CODE = "cod";


    public static  String[] getParsedJsonWeatherData(Context context, String forecastJsonStr) throws JSONException {
        //getting parsed weather of each day
        String[] parsedWeatherData = null;
        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        /* if there is error */
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
        parsedWeatherData = new String[weatherArray.length()];

        long localDate = System.currentTimeMillis();
        long utcDate = StormfulDateUtils.getUTCDateFromLocal(localDate);
        long startDay = StormfulDateUtils.normalizeDate(utcDate);

        for(int i = 0;i<weatherArray.length();i++)
        {
            String date;
            String highAndLow;

            /* These are the values that will be collected */
            long dateTimeMillis;
            double high;
            double low;
            String description;

            /*Get the JSON object representing the day */
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            dateTimeMillis = startDay + StormfulDateUtils.DAY_IN_MILLIS * i;
            date = StormfulDateUtils.getFriendlyDateString(context, dateTimeMillis, false);

            JSONObject weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            JSONObject mainObject = dayForecast.getJSONObject(OWM_DESCRIPTION);
            high = mainObject.getDouble(OWM_MAX);
            low = mainObject.getDouble(OWM_MIN);
            highAndLow = StormfulWeatherUtils.formatHighLows(context, high, low);

            parsedWeatherData[i] = date + " - " + description + " - " + highAndLow;

        }
        return parsedWeatherData;
    }
    /**
     *  This method will help on: content provider bulk insert and json parse
     */
    public static ContentValues[] getWeatherContentValuesFromJson(Context context, String forecastJsonStr) throws JSONException{

        //main json object
        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        //error check
        if(forecastJson.has(OWM_MESSAGE_CODE))
        {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode)
            {
                case HttpURLConnection
                        .HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // location invalid
                    return null;
                default:
                    // server down
                    return null;
            }
        }

        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
        ContentValues[] weatherContentValues = new ContentValues[weatherArray.length()];

        //phone ko date
        long localDate = System.currentTimeMillis();
//        long utcDate = StormfulDateUtils.getUTCDateFromLocal(localDate);
        long startDay = StormfulDateUtils.normalizeDate(localDate);

        for(int i = 0; i<weatherArray.length();i++)
        {
            String date; // particular day
            String highAndLow; // max temp and min temp

            long dateTimeMillis;
            double high;
            double low;
            double humidity;
            double pressure;
            double windSpeed;
            double windDirection;
            int weatherId;
            String description;

            // get the json of i th day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            dateTimeMillis = startDay + StormfulDateUtils.DAY_IN_MILLIS * i;
            date = StormfulDateUtils.getFriendlyDateString(context,dateTimeMillis,false);

            // getting weather info
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            weatherId = weatherObject.getInt(OWM_WEATHER_ID);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // getting temperature
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            high = temperatureObject.getDouble(OWM_MAX);
            low = temperatureObject.getDouble(OWM_MIN);

            // getting humidity and pressure
            humidity = temperatureObject.getDouble(OWM_HUMIDITY);
            pressure = temperatureObject.getDouble(OWM_PRESSURE);

            // wind speed and direction
            JSONObject windObject = dayForecast.getJSONObject(OWM_WIND);
            windSpeed = windObject.getDouble(OWM_WIND_SPEED);
            windDirection = windObject.getDouble(OWM_WIND_DIRECTION);

            //auta day ko weather
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE,dateTimeMillis);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,humidity);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE,pressure);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,windSpeed);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES,windDirection);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,high);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,low);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,weatherId);

            weatherContentValues[i] = weatherValues;
        }
        return  weatherContentValues;
    }
}
