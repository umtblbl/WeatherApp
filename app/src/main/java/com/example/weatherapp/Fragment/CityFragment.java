package com.example.weatherapp.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.weatherapp.R;
import com.example.weatherapp.Retrofit.ApiClient;
import com.example.weatherapp.Retrofit.IOpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CityFragment extends Fragment {

    static CityFragment instance;
    private List<String> cityList;
    private MaterialSearchBar searchBar;

    ImageView imgWeather;
    TextView txtCityName, txtHumidity, txtSunrise, txtSunset, txtPressure, txtTemperature, txtDescription, txtDateTime, txtWind, txtGeoCoord;
    LinearLayout linearLayoutWeather;
    ProgressBar progressBar;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    public CityFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = ApiClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    public static CityFragment getInstance() {
        if(instance == null)
            instance = new CityFragment();
        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_city, container, false);

        Initialize(view);
        searchBar.setEnabled(true);

        //Read City List
        new LoadCities().execute();

        return view;
    }

    private void Initialize(View view) {
        txtDescription = view.findViewById(R.id.txtDescription2);
        imgWeather = view.findViewById(R.id.imgWeather2);
        txtCityName = view.findViewById(R.id.txtCityName2);
        txtSunrise = view.findViewById(R.id.txtSunrise2);
        txtSunset = view.findViewById(R.id.txtSunset2);
        txtHumidity = view.findViewById(R.id.txtHumidity2);
        txtPressure = view.findViewById(R.id.txtPressure2);
        txtTemperature = view.findViewById(R.id.txtCityTemperature2);
        txtDateTime = view.findViewById(R.id.txtCityDateTime2);
        txtWind = view.findViewById(R.id.txtWind2);
        txtGeoCoord = view.findViewById(R.id.txtGeoCoord2);
        linearLayoutWeather = view.findViewById(R.id.linearLayoutWeather2);
        progressBar = view.findViewById(R.id.loadingToday2);
        searchBar = view.findViewById(R.id.searchBar);
    }

    private void getWeatherInformation(String cityName) {
        compositeDisposable.add(mService.getWeatherCityName(cityName, Common.APP_ID, "metric")
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

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    private class LoadCities extends SimpleAsyncTask<List<String>>{
        @Override
        protected List<String> doInBackgroundSimple() {
            cityList = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream stream = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzıpInputStream = new GZIPInputStream(stream);

                InputStreamReader reader = new InputStreamReader(gzıpInputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String readed;

                while ((readed = bufferedReader.readLine()) != null)
                    builder.append(readed);
                cityList = new Gson().fromJson(builder.toString(), new TypeToken<List<String>>(){}.getType());


            } catch (IOException e) {
                e.printStackTrace();
            }
            return cityList;
        }


        @Override
        protected void onSuccess(final List<String> listCity) {
            super.onSuccess(listCity);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest = new ArrayList<>();
                    for(String search : listCity) {
                        if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                            suggest.add(search);
                    }
                    searchBar.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInformation(text.toString());

                    searchBar.setLastSuggestions(listCity);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

            searchBar.setLastSuggestions(listCity);

            progressBar.setVisibility(View.GONE);
        }
    }
}
