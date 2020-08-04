package com.example.trystromful;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trystromful.data.StormfulPreferences;
import com.example.trystromful.data.WeatherContract;
import com.example.trystromful.sync.StormfulSyncUtils;
import com.example.trystromful.utilities.FakeDataUtils;
import com.example.trystromful.utilities.NetworkUtils;
import com.example.trystromful.utilities.NotificationUtils;
import com.example.trystromful.utilities.OpenWeatherJsonUtils;
import com.github.ybq.android.spinkit.SpinKitView;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    //views
    private RecyclerView mWeatherRecyclerView;
    private static final String TAG = "MainActivity";
    private TextView mErrorMessageDisplay;
    private SpinKitView mLoadingIndicator;
    private ForecastAdapter mForecastAdapter;
    private Context mContext = MainActivity.this;

    //recycler view
    private int mPosition = RecyclerView.NO_POSITION;

    //loader id
    private static final int FORECAST_LOADER_ID = 0;

    // weather columns that are displayed and queried
    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    // weather table column ko indexes
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        mWeatherRecyclerView = findViewById(R.id.recyclerview_forecast);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        //layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mWeatherRecyclerView.setLayoutManager(layoutManager);
        mWeatherRecyclerView.setHasFixedSize(true);

        mForecastAdapter = new ForecastAdapter(this,this);
        mWeatherRecyclerView.setAdapter(mForecastAdapter);

        //loading indicator
        showLoading();

        //loader manager
        getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);

        //get the data from our service
        StormfulSyncUtils.initialize(this);

    }

    /*
       -------------------------------- Loading UI  ----------------------------------------
     */

    private void showLoading()
    {
        mWeatherRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }


    private void showWeatherDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mWeatherRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mWeatherRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(long dateInMillis) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        //passing uri to the detail activity for a particular weather item
        Uri uriForDetail = WeatherContract.WeatherEntry.buildWeatherUriWithDate(dateInMillis);
        intent.setData(uriForDetail);
        startActivity(intent);
    }

    /*
       -------------------------------- Loader Manager  ----------------------------------------
     */

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case FORECAST_LOADER_ID:
                //uri
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                //selection : from today onwards
                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(this,
                        forecastQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented" + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor weatherData) {
        mForecastAdapter.swapCursor(weatherData);
        if(mPosition == RecyclerView.NO_POSITION)
        {
            mPosition =0;
        }
        if (weatherData.getCount() != 0) {
            showWeatherDataView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    /*
       -------------------------------- MENU ----------------------------------------
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_map) {
            openLocationInMap();
            return true;
        }
        if (id == R.id.action_settings) {
            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settings);
            return true;
        }
        if (id == R.id.action_refresh) {
            NotificationUtils.notifyUserOfNewWeather(getApplicationContext());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

      /*
       -------------------------------- INTENTS ----------------------------------------
     */

    private void openLocationInMap() {
        double[] coords = StormfulPreferences.getLocationCoordinates(mContext);
        String posLat = Double.toString(coords[0]);
        String posLong = Double.toString(coords[1]);
        Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

        Intent intent = new Intent(Intent.ACTION_VIEW, geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
