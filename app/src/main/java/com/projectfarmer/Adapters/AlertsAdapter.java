package com.projectfarmer.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfarmer.Models.Alerts;
import com.projectfarmer.Models.Constants;
import com.projectfarmer.Models.MultiDays;
import com.projectfarmer.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.CropHolder> {

    private Context mContext;
    private List<Alerts> mAlerts;

    private ProgressDialog mProgressDialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AlertsAdapter(Context context, List<Alerts> alerts){
        mContext = context;
        mAlerts = alerts;
    }

    @Override
    public CropHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_alerts_layout, parent, false);
        return new CropHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CropHolder holder, final int position) {

        final Alerts alerts = mAlerts.get(position);

        holder.Temperature.setText(String.format(Locale.getDefault(), "%.0f°", alerts.getTemp().getMax()));
        holder.Weather_Icon.setAnimation(getWeatherAnimation(Long.valueOf(alerts.getWeatherItems().get(0).getId())));
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(alerts.getDt() * 1000L);
        holder.Alert.setText(alerts.getAlert());
        holder.Day.setText(Constants.DAYS_OF_WEEK[calendar.get(Calendar.DAY_OF_WEEK) - 1]);


    }

    @Override
    public int getItemCount() {
        return mAlerts.size();
    }


    public class CropHolder extends RecyclerView.ViewHolder {

        public TextView Temperature, Day, Alert;
        public LottieAnimationView Weather_Icon;

        public CropHolder(View itemView) {
            super(itemView);

            Day = itemView.findViewById(R.id.day);
            Alert = itemView.findViewById(R.id.alert);
            Weather_Icon = itemView.findViewById(R.id.weather_icon);
            Temperature = itemView.findViewById(R.id.temperature);
        }
    }

    public static int getWeatherAnimation(Long weatherCode) {
        if (weatherCode / 100 == 2) {
            return R.raw.storm_weather;
        } else if (weatherCode / 100 == 3) {
            return R.raw.rainy_weather;
        } else if (weatherCode / 100 == 5) {
            return R.raw.rainy_weather;
        } else if (weatherCode / 100 == 6) {
            return R.raw.snow_weather;
        } else if (weatherCode / 100 == 7) {
            return R.raw.unknown;
        } else if (weatherCode == 800) {
            return R.raw.clear_day;
        } else if (weatherCode == 801) {
            return R.raw.few_clouds;
        } else if (weatherCode == 803) {
            return R.raw.broken_clouds;
        } else if (weatherCode / 100 == 8) {
            return R.raw.cloudy_weather;
        }
        return R.raw.unknown;
    }
}