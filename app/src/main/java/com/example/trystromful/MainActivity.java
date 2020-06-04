package com.example.trystromful;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private static final String TAG = "MainActivity";
    private TextView mErrorMessageDisplay;
    private SpinKitView mLoadingIndicator;
    private ForecastAdapter mForecastAdapter;
    private Context context = MainActivity.this;

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

        loadWeatherData();
    }

    private void loadWeatherData(){
        showWeatherDataView();
        String location = StormfulPreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

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

    @Override
    public void onClick(String weatherForDay) {
        Intent intent = new Intent(context,DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,weatherForDay);
        startActivity(intent);
    }

    public class FetchWeatherTask extends AsyncTask<String,Void,String[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            String location = params[0];
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
        protected void onPostExecute(String[] weatherData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (weatherData != null) {
                showWeatherDataView();
                mForecastAdapter.setWeatherData(weatherData);
            }
            else{
                showErrorMessage();
            }
        }
    }

    /*
    *  ----------------------  Menu Items --------------------------
    * */

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
           mForecastAdapter.setWeatherData(null);
           loadWeatherData();
           return  true;
       }
       return super.onOptionsItemSelected(item);
    }
}
