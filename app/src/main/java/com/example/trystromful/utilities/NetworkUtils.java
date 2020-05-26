package com.example.trystromful.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    private  static  final String BASE_WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast";

    private static final String units = "metric";
    private static final int numDays = 14;
    private static final String format = "json";
    private static final  String appId = "04402e294a942dab68a991dc735cf2e4";

    final static String QUERY_PARAM = "q";
    final static String LAT_PARAM = "lat";
    final static String LON_PARAM = "lon";
    final static String FORMAT_PARAM = "mode";
    final static String UNITS_PARAM = "units";
    final static String DAYS_PARAM = "cnt";
    final static  String APP_ID_PARAM = "appid";

    public static URL buildURL (String locationQuery){
        Uri builtUri = Uri.parse(BASE_WEATHER_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM,locationQuery)
                .appendQueryParameter(FORMAT_PARAM,format)
                .appendQueryParameter(UNITS_PARAM,units)
                .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                .appendQueryParameter(APP_ID_PARAM,appId)
                .build();

        URL url = null;
        try{
            url = new URL(builtUri.toString());
        }catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        Log.e(TAG,"Built URI "+url);
        return  url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
