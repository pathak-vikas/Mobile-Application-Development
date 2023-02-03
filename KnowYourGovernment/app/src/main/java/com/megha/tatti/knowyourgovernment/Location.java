package com.megha.tatti.knowyourgovernment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;


public class Location {
    private MainActivity mainActivity;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public Location(MainActivity activity) {
        mainActivity = activity;
        if (checkPermission()) {
            setLocationManager();
            getLocation();
        }
    }

    public void setLocationManager() {
        if (locationManager != null)
            return;

        locationManager = (LocationManager) mainActivity.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(android.location.Location location) {
               Toast.makeText(mainActivity, "Update from " + location.getProvider(), Toast.LENGTH_SHORT).show();
               MainActivity.address= getAddress(location.getLatitude(), location.getLongitude());
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    private String getAddress(double latitude, double longitude) {

        List<android.location.Address> addresses = null;
        for (int times = 0; times < 3; times++) {
            Geocoder geocoder = new Geocoder(mainActivity.getApplicationContext(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                StringBuilder sb = new StringBuilder();
                for (android.location.Address ad : addresses) {
                    sb.append(ad.getLocality()+", "+ad.getAdminArea());
                    sb.append("$"+ad.getPostalCode());

                }
                return sb.toString();
            } catch (IOException e) {

            }
            Toast.makeText(mainActivity.getApplicationContext(), "GeoCoder service is slow - please wait", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(mainActivity.getApplicationContext(), "GeoCoder service timed out -try again!!", Toast.LENGTH_LONG).show();
        return null;
    }

    public void destroy() {
        try {
            locationManager.removeUpdates(locationListener);
        } catch (SecurityException e) {
           Toast.makeText(mainActivity.getApplicationContext(),"PERMISSION NOT GRANTED",Toast.LENGTH_LONG).show();
        }
        locationManager = null;
    }

    public void getLocation() {

        if (checkPermission()) {

            if (locationManager != null) {
                android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    MainActivity.address= getAddress(location.getLatitude(), location.getLongitude());
                    return;
                }
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if (location != null) {
                    MainActivity.address= getAddress(location.getLatitude(), location.getLongitude());
                    return;
                }
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    MainActivity.address= getAddress(location.getLatitude(), location.getLongitude());
                    return;
                }
            }
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
            return false;
        }
        return true;
    }
}
