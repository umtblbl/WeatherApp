package com.example.weatherapp;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.weatherapp.Adapter.ViewPagerAdapter;
import com.example.weatherapp.Commons.Common;
import com.example.weatherapp.Fragment.CityFragment;
import com.example.weatherapp.Fragment.ForecastFragment;
import com.example.weatherapp.Fragment.TodayFragment;
import com.example.weatherapp.Model.City;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CoordinatorLayout coordinatorLayout;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Initialize();
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        SetupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        //Request Permission
        RequestPermission();

    }

    public void RequestPermission() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){

                            BuildLocationRequest();
                            BuildLocationCallBack();

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Snackbar.make(coordinatorLayout, "İzin verilmedi!", Snackbar.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private void SetupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(TodayFragment.getInstance(), "Bugün");
        adapter.AddFragment(ForecastFragment.getInstance(), "5 Günlük");
        adapter.AddFragment(CityFragment.getInstance(), "Şehirler");
        viewPager.setAdapter(adapter);

    }
    private void BuildLocationCallBack() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Common.currentLocation = locationResult.getLastLocation();
                Log.d("Location", Common.currentLocation.getLatitude()+"/"+Common.currentLocation.getLongitude());

                SharedPreferences.Editor editor = getSharedPreferences("PREPS", MODE_PRIVATE).edit();
                editor.putString("x", String.valueOf(Common.currentLocation.getLatitude()));
                editor.putString("y", String.valueOf(Common.currentLocation.getLongitude()));
                editor.apply();

            }
        };
    }

    private void BuildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }

    private void Initialize() {
        toolbar = findViewById(R.id.toolbarMain);
        tabLayout = findViewById(R.id.tabLayoutMain);
        viewPager = findViewById(R.id.viewPagerMain);
        coordinatorLayout = findViewById(R.id.coordinatorLayoutMain);
        toolbar = findViewById(R.id.toolbarMain);
        toolbar = findViewById(R.id.toolbarMain);
    }
}
