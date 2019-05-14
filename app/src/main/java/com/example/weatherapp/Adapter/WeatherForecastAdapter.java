package com.example.weatherapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherapp.Commons.Common;
import com.example.weatherapp.Model.WeatherForecasResult;
import com.example.weatherapp.R;
import com.squareup.picasso.Picasso;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.MyViewHolder> {

    Context mContext;
    WeatherForecasResult result;

    public WeatherForecastAdapter(Context mContext, WeatherForecasResult result) {
        this.mContext = mContext;
        this.result = result;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_forecast, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(result.list.get(i).weather.get(0).getIcon())
                .append(".png").toString()).into(myViewHolder.imageWeather);

        myViewHolder.txtDateTime.setText(new StringBuilder(Common.convertUnixToDate(result.list.get(i).dt)));
        myViewHolder.txtDescription.setText(new StringBuilder(result.list.get(i).weather.get(0).getDescription()));
        myViewHolder.txtTemperature.setText(new StringBuilder(String.valueOf(result.list.get(i).main.getTemp())).append("°C"));

    }

    @Override
    public int getItemCount() {
        return result.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txtDateTime, txtDescription, txtTemperature;
        ImageView imageWeather;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDateTime = itemView.findViewById(R.id.txtDateForecast);
            txtDescription = itemView.findViewById(R.id.txtDescriptionForecast);
            txtTemperature = itemView.findViewById(R.id.txtCityTemperatureForecase);
            imageWeather = itemView.findViewById(R.id.imgWeatherForecast);
        }
    }
}
