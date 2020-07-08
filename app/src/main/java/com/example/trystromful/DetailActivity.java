package com.example.trystromful;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.trystromful.R;
import com.example.trystromful.data.WeatherContract;
import com.example.trystromful.utilities.StormfulDateUtils;
import com.example.trystromful.utilities.StormfulWeatherUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "DetailActivity";
    private static final String FORECAST_SHARE_HASHTAG = " #Stormful";
    private String mForecastSummary;

    //views
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    //uri
    private Uri mUri;

    //cursor and content provider
    private static final int ID_DETAIL_LOADER = 10;

    //columns
    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
    };


    // weather table column ko indexes
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;
    public static final int INDEX_WEATHER_HUMIDITY = 4;
    public static final int INDEX_WEATHER_PRESSURE = 5;
    public static final int INDEX_WEATHER_WIND_SPEED = 6;
    public static final int INDEX_WEATHER_DEGREES = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDateView = findViewById(R.id.date);
        mDescriptionView = findViewById(R.id.weather_description);
        mHighTempView = findViewById(R.id.high_temperature);
        mLowTempView = findViewById(R.id.low_temperature);
        mHumidityView = findViewById(R.id.humidity);
        mWindView = findViewById(R.id.wind);
        mPressureView = findViewById(R.id.pressure);

        // intent
        mUri = getIntent().getData();
        if (mUri == null) {
            throw new NullPointerException("Uri for DetailActivity cannnot be null");
        }

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER,null,this);
    }

    /*
           ------------------------ MENU ----------------------------
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
        return true;
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        return shareIntent;
    }

    /*
          ------------------------ CURSOR LOADER ----------------------------
    */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        WEATHER_DETAIL_PROJECTION,
                        null,
                        null,
                        null
                );
            default:
                throw new RuntimeException("Loader not implemented " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        //validating cursor data
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }
        if (!cursorHasValidData) {
            return;
        }
        //get data from cursor using indexes

        //date
        long localDate = data.getLong(INDEX_WEATHER_DATE);
        String dateText = StormfulDateUtils.getFriendlyDateString(this,localDate,true);
        mDateView.setText(dateText);

        //description from weather id
        int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
        String description  = StormfulWeatherUtils.getStringForWeatherCondition(this,weatherId);
        mDescriptionView.setText(description);

        //max temp
        double highTemp = data.getDouble(INDEX_WEATHER_MAX_TEMP);
        String highString = StormfulWeatherUtils.formatTemperature(this,highTemp);
        mHighTempView.setText(highString);

        //min temp
        double lowTemp = data.getDouble(INDEX_WEATHER_MIN_TEMP);
        String lowString = StormfulWeatherUtils.formatTemperature(this,lowTemp);
        mHighTempView.setText(lowString);

        //humidity
        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity,humidity);
        mHumidityView.setText(humidityString);

        //pressure
        float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);
        String pressureString = getString(R.string.format_pressure,pressure);
        mHumidityView.setText(pressureString);

        //wind
        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = StormfulWeatherUtils.getFormattedWind(this,windSpeed,windDirection);
        mWindView.setText(windString);

        //summary for sharing intent
        mForecastSummary = String.format("%s - %s - %s/%s",dateText,description,highString,lowString);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
