package com.example.weatherapp.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.Commons.Common;
import com.example.weatherapp.Model.WeatherResult;
import com.example.weatherapp.Model.Wind;
import com.example.weatherapp.R;
import com.example.weatherapp.Retrofit.ApiClient;
import com.example.weatherapp.Retrofit.IOpenWeatherMap;
import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TodayFragment extends Fragment {

    static TodayFragment instance;

    ImageView imgWeather;
    TextView txtCityName, txtHumidity, txtSunrise, txtSunset, txtPressure, txtTemperature, txtDescription, txtDateTime, txtWind, txtGeoCoord;
    LinearLayout linearLayoutWeather;
    ProgressBar progressBar;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    public TodayFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = ApiClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);

    }

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_today, container, false);

            Initialize(view);
            getWeatherInformation();

            return view;
        }

    private void getWeatherInformation() {

        SharedPreferences preferences = getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        String x = preferences.getString("x", "x");
        String y = preferences.getString("y","y");

        /*preferences.edit().remove("x").apply();
        preferences.edit().remove("y").apply();*/

       compositeDisposable.add(mService.getWeatherByLatLng(x, y, Common.APP_ID, "metric")
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Consumer<WeatherResult>() {
                   @Override
                   public void accept(WeatherResult weatherResult) throws Exception {

                       //Load Information
                       Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                               .append(weatherResult.getWeather().get(0).getIcon())
                               .append(".png").toString()).into(imgWeather);

                       txtCityName.setText(weatherResult.getName());

                       txtDescription.setText(new StringBuilder(weatherResult.getName()).append(" Hava Durumu"));

                       txtTemperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp()))
                               .append("°C").toString());

                       txtDateTime.setText(Common.convertUnixToDate(weatherResult.getDt()));

                       txtPressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure()))
                               .append(" hpa").toString());

                       txtHumidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity()))
                               .append("%").toString());

                       txtSunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                       txtSunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
                       txtGeoCoord.setText(new StringBuilder(weatherResult.getCoord().toString()));
                       txtWind.setText(new StringBuilder("Hız: ").append(weatherResult.getWind().getSpeed())
                       .append(", Derece: ").append(weatherResult.getWind().getDeg()));

                       //Relative Layout
                       linearLayoutWeather.setVisibility(View.VISIBLE);
                       progressBar.setVisibility(View.GONE);
                   }
               }, new Consumer<Throwable>() {
                   @Override
                   public void accept(Throwable throwable) throws Exception {
                       Toast.makeText(getActivity(), ""+throwable.getMessage(), Toast.LENGTH_LONG).show();
                   }
               })

       );
    }

    private void Initialize(View view) {
        txtDescription = view.findViewById(R.id.txtDescription);
        imgWeather = view.findViewById(R.id.imgWeather);
        txtCityName = view.findViewById(R.id.txtCityName);
        txtSunrise = view.findViewById(R.id.txtSunrise);
        txtSunset = view.findViewById(R.id.txtSunset);
        txtHumidity = view.findViewById(R.id.txtHumidity);
        txtPressure = view.findViewById(R.id.txtPressure);
        txtTemperature = view.findViewById(R.id.txtCityTemperature);
        txtDateTime = view.findViewById(R.id.txtCityDateTime);
        txtWind = view.findViewById(R.id.txtWind);
        txtGeoCoord = view.findViewById(R.id.txtGeoCoord);
        linearLayoutWeather = view.findViewById(R.id.linearLayoutWeather);
        progressBar = view.findViewById(R.id.loadingToday);
    }

    public static TodayFragment getInstance() {
        if(instance == null)
            instance = new TodayFragment();
        return instance;
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
