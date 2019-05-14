package com.example.weatherapp.Retrofit;

import com.example.weatherapp.Model.WeatherForecasResult;
import com.example.weatherapp.Model.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {

    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLng(@Query("lat") String lat,
                                                 @Query("lon") String lng,
                                                 @Query("appid") String appid,
                                                 @Query("units") String unit);
    @GET("forecast")
    Observable<WeatherForecasResult> getForecastWeatherByLatLng(@Query("lat") String lat,
                                                                @Query("lon") String lng,
                                                                @Query("appid") String appid,
                                                                @Query("units") String unit);
    @GET("weather")
    Observable<WeatherResult> getWeatherCityName(@Query("q") String cityName,
                                                 @Query("appid") String appid,
                                                 @Query("units") String unit);

}
