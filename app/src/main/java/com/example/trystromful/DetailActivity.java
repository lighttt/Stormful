package com.example.trystromful;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    private String mForecast;
    private TextView mWeatherDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mWeatherDisplay = findViewById(R.id.tv_display_weather);
        Intent fromForecast = getIntent();
        if(fromForecast!=null)
        {
            if(fromForecast!=null)
            {
                mForecast = fromForecast.getStringExtra(Intent.EXTRA_TEXT);
                mWeatherDisplay.setText(mForecast);
            }
        }
    }
}
