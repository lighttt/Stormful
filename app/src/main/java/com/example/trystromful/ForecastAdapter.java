package com.example.trystromful;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trystromful.utilities.StormfulDateUtils;
import com.example.trystromful.utilities.StormfulWeatherUtils;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    //variables
    private final ForecastAdapterOnClickListener mClickListener;
    private Cursor mCursor;
    private final Context mContext;


    //  ====================== RV ON CLICK LISTENER =================================
    public interface ForecastAdapterOnClickListener {
        void onClick(long date);
    }

    public ForecastAdapter(Context context, ForecastAdapterOnClickListener listener) {
        this.mContext = context;
        this.mClickListener = listener;
    }

    //  ====================== RV FUNCTIONS =================================
    @NonNull
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.forecast_list_item, viewGroup, false);
        view.setFocusable(true);
        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        // ============= weather data setting ============

        //getting all columns values
        //date
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        String dateString = StormfulDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
        //condition
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        String description = StormfulWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        //temperature
        double highTemp = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        double lowTemp = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);

        //format degree celsius
        String highAndLowTemperature = StormfulWeatherUtils.formatHighLows(mContext, highTemp, lowTemp);

        String weatherSummary = dateString + " - " + description + " - " + highAndLowTemperature;
        holder.mWeatherTextView.setText(weatherSummary);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    //  ====================== CURSOR FUNCTIONS =================================
    //swap cursor for new weather data
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mWeatherTextView;

        public ForecastAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mWeatherTextView = itemView.findViewById(R.id.tv_weather_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateTimeMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickListener.onClick(dateTimeMillis);
        }
    }
}
