package com.example.weatherapp.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weatherapp.Adapter.WeatherForecastAdapter;
import com.example.weatherapp.Commons.Common;
import com.example.weatherapp.Model.WeatherForecasResult;
import com.example.weatherapp.R;
import com.example.weatherapp.Retrofit.ApiClient;
import com.example.weatherapp.Retrofit.IOpenWeatherMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ForecastFragment extends Fragment {

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    TextView txtCityName, txtGeoCoord;
    RecyclerView recyclerViewForecast;


    static ForecastFragment instance;

    public ForecastFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = ApiClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        Initialize(view);
        recyclerViewForecast.setHasFixedSize(true);
        recyclerViewForecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        getForecastWeatherInfo();


        return view;
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

    private void getForecastWeatherInfo() {

        SharedPreferences preferences = getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        String x = preferences.getString("x", "x");
        String y = preferences.getString("y","y");

        compositeDisposable.add(mService.getForecastWeatherByLatLng(x,y, Common.APP_ID, "metric")
            .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecasResult>() {
                    @Override
                    public void accept(WeatherForecasResult weatherForecasResult) throws Exception {
                        displayForecastWeather(weatherForecasResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("ERROR", ""+throwable.getMessage());
                    }
                })

        );
    }

    private void displayForecastWeather(WeatherForecasResult weatherForecasResult) {
        txtCityName.setText(new StringBuilder(weatherForecasResult.city.name));
        txtGeoCoord.setText(new StringBuilder(weatherForecasResult.city.coord.toString()));

        WeatherForecastAdapter adapter = new WeatherForecastAdapter(getContext(), weatherForecasResult);
        recyclerViewForecast.setAdapter(adapter);
    }

    private void Initialize(View view) {
        txtCityName = view.findViewById(R.id.txtNameForecast);
        txtGeoCoord = view.findViewById(R.id.txtGeoCoordForecast);
        recyclerViewForecast = view.findViewById(R.id.recyclerviewForecast);
    }

    public static ForecastFragment getInstance(){
        if(instance == null)
            instance = new ForecastFragment();
        return instance;
    }

}