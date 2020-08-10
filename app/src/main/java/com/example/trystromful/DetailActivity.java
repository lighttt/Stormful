package com.example.trystromful;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
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
import com.example.trystromful.databinding.ActivityDetailBinding;
import com.example.trystromful.utilities.StormfulDateUtils;
import com.example.trystromful.utilities.StormfulWeatherUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "DetailActivity";
    private static final String FORECAST_SHARE_HASHTAG = " #Stormful";
    private String mForecastSummary;

    //data binding
    private ActivityDetailBinding mDetailBinding;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setTitle("");

        //data binding
        mDetailBinding = DataBindingUtil.setContentView(this,R.layout.activity_detail);

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

        // ============= weather data setting ============

        //icon
        int weatherId = data.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherIconImage = StormfulWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);
        mDetailBinding.primaryInfo.weatherIcon.setImageResource(weatherIconImage);

        //getting all columns values
        //date
        long dateInMillis = data.getLong(MainActivity.INDEX_WEATHER_DATE);
        String dateString = StormfulDateUtils.getFriendlyDateString(this, dateInMillis, false);
        mDetailBinding.primaryInfo.date.setText(dateString);

        //condition with accessibility
        String description = StormfulWeatherUtils.getStringForWeatherCondition(this, weatherId);
        String descriptionAlly = this.getString(R.string.ally_forecast,description);
        mDetailBinding.primaryInfo.weatherDescription.setText(description);
        mDetailBinding.primaryInfo.weatherDescription.setContentDescription(descriptionAlly);

        //temperature
        double highTemp = data.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        String highString = StormfulWeatherUtils.formatTemperature(this, highTemp);
        String highAlly = this.getString(R.string.ally_high_temp,highString);
        mDetailBinding.primaryInfo.highTemperature.setText(highString);
        mDetailBinding.primaryInfo.highTemperature.setContentDescription(highAlly);

        double lowTemp = data.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        String lowString = StormfulWeatherUtils.formatTemperature(this, lowTemp);
        String lowAlly = this.getString(R.string.ally_low_temp,lowString);
        mDetailBinding.primaryInfo.lowTemperature.setText(lowString);
        mDetailBinding.primaryInfo.lowTemperature.setContentDescription(lowAlly);

        //humidity
        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity,humidity);
        String humidityAlly = getString(R.string.ally_humidity, humidityString);
        mDetailBinding.extraDetails.humidity.setText(humidityString);
        mDetailBinding.extraDetails.humidityLabel.setContentDescription(humidityAlly);
        mDetailBinding.extraDetails.humidity.setContentDescription(humidityAlly);

        //pressure
        float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);
        String pressureString = getString(R.string.format_pressure,pressure);
        String pressureAlly = getString(R.string.ally_pressure, pressureString);
        mDetailBinding.extraDetails.pressure.setText(pressureString);
        mDetailBinding.extraDetails.pressureLabel.setContentDescription(pressureAlly);
        mDetailBinding.extraDetails.pressure.setContentDescription(pressureAlly);

        //wind
        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = StormfulWeatherUtils.getFormattedWind(this,windSpeed,windDirection);
        String windAlly = getString(R.string.ally_wind, windString);
        mDetailBinding.extraDetails.wind.setContentDescription(windAlly);
        mDetailBinding.extraDetails.windLabel.setContentDescription(windAlly);
        mDetailBinding.extraDetails.wind.setText(windString);

        //summary for sharing intent
        mForecastSummary = String.format("%s - %s - %s/%s",dateString,description,highString,lowString);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
