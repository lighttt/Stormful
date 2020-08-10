package com.example.trystromful;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trystromful.data.WeatherContract;
import com.example.trystromful.utilities.StormfulDateUtils;
import com.example.trystromful.utilities.StormfulWeatherUtils;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    //variables
    private final ForecastAdapterOnClickListener mClickListener;
    private Cursor mCursor;
    private final Context mContext;

    //view type
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    //show today weather
    private boolean mUseTodayLayout;


    //  ====================== RV ON CLICK LISTENER =================================
    public interface ForecastAdapterOnClickListener {
        void onClick(long date);
    }

    public ForecastAdapter(Context context, ForecastAdapterOnClickListener listener) {
        this.mContext = context;
        this.mClickListener = listener;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    //  ====================== RV FUNCTIONS =================================
    @NonNull
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        int layoutId;
        switch (viewType)
        {
            case VIEW_TYPE_TODAY:
                layoutId = R.layout.list_item_forecast_today;
                break;
            case VIEW_TYPE_FUTURE_DAY:
                layoutId = R.layout.forecast_list_item;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }

        View view = LayoutInflater
                .from(mContext)
                .inflate(layoutId, viewGroup, false);
        view.setFocusable(true);
        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        // ============= weather data setting ============

        //view type
        int viewType = getItemViewType(position);

        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherIconImage;

        switch (viewType)
        {
            case VIEW_TYPE_TODAY:
                weatherIconImage = StormfulWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);
                break;
            case VIEW_TYPE_FUTURE_DAY:
                weatherIconImage = StormfulWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }

        //icon
        holder.iconView.setImageResource(weatherIconImage);

        //getting all columns values
        //date
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        String dateString = StormfulDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
        holder.dateView.setText(dateString);

        //condition with accessibility
        String description = StormfulWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        String descriptionAlly = mContext.getString(R.string.ally_forecast,description);
        holder.descriptionView.setText(description);
        holder.descriptionView.setContentDescription(descriptionAlly);

        //temperature
        double highTemp = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        String highString = StormfulWeatherUtils.formatTemperature(mContext, highTemp);
        String highAlly = mContext.getString(R.string.ally_high_temp,highString);
        holder.highTempView.setText(highString);
        holder.highTempView.setContentDescription(highAlly);

        double lowTemp = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        String lowString = StormfulWeatherUtils.formatTemperature(mContext, lowTemp);
        String lowAlly = mContext.getString(R.string.ally_low_temp,lowString);
        holder.lowTempView.setText(lowString);
        holder.lowTempView.setContentDescription(lowAlly);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        if(mUseTodayLayout && position == 0)
        {
            return  VIEW_TYPE_TODAY;
        }
        else{
            return VIEW_TYPE_FUTURE_DAY;
        }
    }

    //  ====================== CURSOR FUNCTIONS =================================
    //swap cursor for new weather data
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final ImageView iconView;

        public ForecastAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.date);
            descriptionView = itemView.findViewById(R.id.weather_description);
            highTempView = itemView.findViewById(R.id.high_temperature);
            lowTempView = itemView.findViewById(R.id.low_temperature);
            iconView = itemView.findViewById(R.id.weather_icon);
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
