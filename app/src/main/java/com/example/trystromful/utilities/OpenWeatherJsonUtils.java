package com.example.trystromful.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class OpenWeatherJsonUtils {

    public static  String[] getParsedJsonWeatherData(Context context, String forecastJsonStr) throws JSONException {
        /*each day weather forecast is on the list*/
        final String OWM_LIST = "list";

        /* temp is children of all weather data */
        final String OWM_TEMPERATURE = "temp";

        /* max min temp for day*/
        final String OWM_MAX = "temp_max";
        final String OWM_MIN = "temp_min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";

        final String OWM_MESSAGE_CODE = "cod";

        /* getting parsed weather data of each day */
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
}
