package com.lucky.uberapp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by lucky on 4/20/17.
 */

public class LocationUtils extends PermissionUtils {

    private static final String TAG = "LocationUtils";

    public static Location getLastKnownLocation(final Activity activity) {
        Log.i(TAG, "getLastKnownLocation called from activity : " + activity.getLocalClassName());
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "version 23 and no permission");
            PermissionUtils.checkPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, "need per", PERMISSION_LOCATION);
        } else {
            Log.i(TAG, "else..");
            // register location listener to get locationChanged updates
            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle("Enable GPS").setMessage("Enable to get location").setCancelable(true).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("Goto Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).show();
            }
            LocationListener locationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1800000, 1000, locationListener);
            // for last know location, get it from GPS
            Location GPSLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            long GPSLocationTime = 0, networkLocationTime = 0;
            if (GPSLocation != null) {
                GPSLocationTime = GPSLocation.getTime();
            }
            if (networkLocation != null) {
                networkLocationTime = networkLocation.getTime();
            }
            if (GPSLocationTime > networkLocationTime) {
                return GPSLocation;
            } else {
                return networkLocation;
            }
        }
        return null;
    }

}
