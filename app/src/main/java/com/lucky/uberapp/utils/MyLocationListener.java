package com.lucky.uberapp.utils;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.lucky.uberapp.uberapp.MainActivity;

/**
 * Created by lucky on 4/20/17.
 */

public class MyLocationListener implements LocationListener{

    private final String TAG = "MyLocationListener";

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged called");
        MainActivity.setLocation(location);
        Toast.makeText(MainActivity.getAppContext(), "received location.. pull up home page", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
