package com.example.trystromful.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.trystromful.data.WeatherContract;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FakeDataUtils {

    private static int [] weatherIDs = {200,300,500,711,900,962};

    /**
     *    Creating random data to add to content provider
      */
    private static ContentValues createTestWeatherContentValues(long date){
        ContentValues testWeatherValues = new ContentValues();
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE,date);
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES,Math.random()*2);
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,Math.random()*100);
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE,900+Math.random()*100);
        int maxTemp = (int)(Math.random()*100);
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,maxTemp);
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,maxTemp - (int) (Math.random() * 10));
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,Math.random()*10);
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,weatherIDs[(int)(Math.random()*10)%5]);
        return testWeatherValues;
    }

    /**
     *    Inserting data to content provider
     */
    public static void insertFakeData(Context context)
    {
        //today's normalized date
        long today = StormfulDateUtils.normalizeDate(System.currentTimeMillis());
        List<ContentValues> fakeValues = new ArrayList<>();
        //loop over 7 days of data
        for(int i=0;i<7;i++)
        {
            fakeValues.add(FakeDataUtils.createTestWeatherContentValues(today + TimeUnit.DAYS.toMillis(i)));
        }
        //bulk insert
        context.getContentResolver().bulkInsert(
                WeatherContract.WeatherEntry.CONTENT_URI,
                fakeValues.toArray(new ContentValues[7])
        );
    }
}
