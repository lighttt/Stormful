package com.example.trystromful;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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
import com.example.trystromful.utilities.NetworkUtils;
import com.example.trystromful.utilities.OpenWeatherJsonUtils;
import com.github.ybq.android.spinkit.SpinKitView;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String[]> {

    private RecyclerView mRecyclerView;
    private static final String TAG = "MainActivity";
    private TextView mErrorMessageDisplay;
    private SpinKitView mLoadingIndicator;
    private ForecastAdapter mForecastAdapter;
    private Context context = MainActivity.this;

    private static final int FORECAST_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        mRecyclerView = findViewById(R.id.recyclerview_forecast);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        //layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mForecastAdapter = new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);

        getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
    }



    /*
     *  ----------------------  Loading Functions --------------------------
     * */

    private void showWeatherDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }



    /*
     *  ----------------------  Loader Functions --------------------------
     * */

    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
           //caching
            String[] mWeatherData = null;
            @Override
            protected void onStartLoading() {
                if (mWeatherData != null) {
                    deliverResult(mWeatherData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public String[] loadInBackground() {
                String location = StormfulPreferences.getPreferredWeatherLocation(context);
                URL weatherRequestUrl = NetworkUtils.buildURL(location);
                try{
                    String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                    String[] weatherDataFromJson = OpenWeatherJsonUtils.getParsedJsonWeatherData(MainActivity.this,jsonWeatherResponse);
                    Log.e(TAG,"The weather data is " + weatherDataFromJson.length + weatherDataFromJson);
                    return weatherDataFromJson;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable String[] data) {
                mWeatherData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String[]> loader, String[] weatherData) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (weatherData != null) {
            showWeatherDataView();
            mForecastAdapter.setWeatherData(weatherData);
        }
        else{
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {

    }



    /*
    *  ----------------------  Menu Items --------------------------
    * */

    private void invalidateData() {
        mForecastAdapter.setWeatherData(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       int id = item.getItemId();
       if(id == R.id.action_refresh)
       {
          invalidateData();
           getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID,null,this);
           return  true;
       }
        if (id == R.id.action_map) {
            openLocationInMap();
            return true;
        }
       return super.onOptionsItemSelected(item);
    }

    /*
     *  ----------------------  Recyclerview OnClick --------------------------
     * */
    @Override
    public void onClick(String weatherForDay) {
        Intent intent = new Intent(context,DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,weatherForDay);
        startActivity(intent);
    }


    /*
     *  ----------------------  Location Intent --------------------------
     * */

    private void openLocationInMap() {
        String addressString = StormfulPreferences.getPreferredWeatherLocation(context);
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e(TAG, "Couldn't call " + geoLocation.toString()
                    + ", no receiving apps installed!");
        }
    }
}
