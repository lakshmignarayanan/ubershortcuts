package com.lucky.uberapp.uberapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lucky.uberapp.utils.LocationUtils;
import com.lucky.uberapp.utils.PermissionUtils;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.core.auth.AccessTokenManager;
import com.uber.sdk.android.core.auth.AuthenticationError;
import com.uber.sdk.android.core.auth.LoginCallback;
import com.uber.sdk.android.core.auth.LoginManager;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestActivityBehavior;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.AccessToken;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    RelativeLayout content_main;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView shortcutList;
    RideRequestButton rideRequestButton;
    LoginManager loginManager;
    public static Context appContext;
    private static Location location;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getBaseContext();
        setContentView(R.layout.activity_main);
        location = LocationUtils.getLastKnownLocation(this);
        UberSdk.initialize(config);
        initComponents();
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void setLocation(Location loc) {
        location = loc;
    }

    public static Location getLocation() {
        return location;
    }

    public void initComponents() {
        content_main = (RelativeLayout) findViewById(R.id.content_main);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        shortcutList = (ListView) findViewById(R.id.shortcutList);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO add a new uber shortcut
            }
        });

        rideRequestButton = new RideRequestButton(this);
        rideRequestButton.setBackgroundColor(Color.CYAN);

        Activity activity = this; // If you're in a fragment you must get the containing Activity!
        int requestCode = 1234;
        rideRequestButton.setRequestBehavior(new RideRequestActivityBehavior(activity, requestCode));
        // Optional, default behavior is to use current location for pickup
        RideParameters rideParams = new RideParameters.Builder()
                .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                .setPickupLocation(37.775304, -122.417522, "Uber HQ", "1455 Market Street, San Francisco")
                .setDropoffLocation(37.795079, -122.4397805, "Embarcadero", "One Embarcadero Center, San Francisco")
                .build();
        rideRequestButton.setRideParameters(rideParams);

        content_main.addView(rideRequestButton);

        //use Uber's Login Manager to handle uber login in our app
        LoginCallback loginCallback = new LoginCallback() {
            @Override
            public void onLoginCancel() {
                // User canceled login
            }

            @Override
            public void onLoginError(@NonNull AuthenticationError error) {
                // Error occurred during login
            }

            @Override
            public void onLoginSuccess(@NonNull AccessToken accessToken) {
                // Successful login!  The AccessToken will have already been saved.
            }

            @Override
            public void onAuthorizationCodeReceived(@NonNull String authorizationCode) {

            }
        };
        AccessTokenManager accessTokenManager = new AccessTokenManager(this);
        loginManager = new LoginManager(accessTokenManager, loginCallback);
        loginManager.login(activity);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLocation(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    SessionConfiguration config = new SessionConfiguration.Builder()
            // mandatory
            .setClientId("XiKReXIURWeBvrP_4g2rxWv-tImPDWPp")
            // required for enhanced button features
            .setServerToken("xhCrpHoiaXSvUrOvmv-DFnACjewYYC6_1-TmUJCN")
            // required for implicit grant authentication
            .setRedirectUri("XiKReXIURWeBvrP4g2rxWv-tImPDWPp://uberConnect")
            // required scope for Ride Request Widget features
            .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
            // optional: set Sandbox as operating environment
            .setEnvironment(SessionConfiguration.Environment.SANDBOX)
            .build();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        loginManager.onActivityResult(this, requestCode, resultCode, data);
    }

    private void refreshLocation(boolean user_refresh) {
        Log.i(TAG, "refreshLocation called");
        location = LocationUtils.getLastKnownLocation(this);
        if (location != null) {
            Log.i(TAG, "location not null : " + location.getLatitude() + " / " + location.getLongitude());
            // TODO refresh shortcuts
            List<Address> addressList;
            Geocoder geocoder = new Geocoder(getAppContext(), Locale.getDefault());
            try {
                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                for (Address a :
                        addressList) {
                    Log.i(TAG, "address: " + a.getAddressLine(0) + "/ " + a.getFeatureName() + " / " + a.getSubLocality() + " / " + a.getLocality() + " / " + a.getAdminArea());
                }
            } catch (Exception e) {
                Log.i(TAG, "address parse exception");
                e.printStackTrace();
            }
            // TODO show nearest uber shortcuts + "ride back home shortcut"
        } else {
            Toast.makeText(this, "Enable GPS to get location..", Toast.LENGTH_SHORT);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.PERMISSION_LOCATION:
                location = LocationUtils.getLastKnownLocation(this); // this gets location and informs user

        }

    }
}
