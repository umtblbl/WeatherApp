package com.example.weatherapp.Commons;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static Location currentLocation=null;

    public static final String APP_ID = "eb2ac110a3d5a767e6ffb394e5090790";

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd EEE MM yyy");
        String formatted = sdf.format(date);
        return formatted;

    }

    public static String convertUnixToHour(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm EEE MM yyy");
        String formatted = sdf.format(date);
        return formatted;
    }

}
