package fi.matti.weatherapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import fi.matti.weatherapp.Utils.Toaster;

public class MainActivity extends AppCompatActivity implements MyDownloadListener {

    protected Location currentLocation;
    private BroadcastReceiver broadcastReceiver;
    private TextView cityView;
    private TextView weatherView;
    private TextView latitude;
    private TextView longitude;
    private Locale locale;
    public Context context;
    int permissionCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreenNoTitle();
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        setViews();
        locale = getResources().getConfiguration().locale;
        permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            // Add more permission checks if needed.
            String[] listOfPermissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this,
                    listOfPermissions,
                    10);

        } else {
            startTracking();
        }
    }

    private void setFullscreenNoTitle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    public void setViews() {
        cityView = findViewById(R.id.cityView);
        weatherView = findViewById(R.id.weatherView);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
    }

    /**
     * Callback method for permission request. Returns with information regarding
     * user decision for permission.
     *
     * @param requestCode code for the permission that was asked.
     * @param permissions permissions
     * @param grantResults result for permission question. Empty if permission not granted.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch(requestCode) {
            case 10:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTracking();
                } else {
                    Debug.print("Permission not granted");
                    Toaster.show(this, "Permission for GPS not granted");
                    finish();
                }
                break;
        }
    }

    /**
     * Display address information(city) in the UI.
     *
     * @param address
     */
    public void showLocationAndWeather(Location location, Address address) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        DecimalFormat df2 = new DecimalFormat(".##");
        latitude.setText("Latitude: " + String.valueOf(df2.format(location.getLatitude())));
        longitude.setText("Longitude: " + String.valueOf(df2.format(location.getLongitude())));
        if (address.getAdminArea() == null) {
            cityView.setText(address.getSubAdminArea());
        } else {
            cityView.setText(address.getAdminArea());
        }

        new DownloadJSONObject(this).execute(Constants.SERVER_QUERY_SAMPLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Debug.print("Pause called");
    }

    /**
     * Stops tracking when activity gets destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Debug.print("Destroy called");
        stopTracking();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Debug.print("Start called");
    }

    /**
     * Start LocationService for tracking location. Register broadcast receiver
     * which listens to location information from the LocationService.
     */
    public void startTracking() {
        Debug.print("Tracking was started");
        if (broadcastReceiver == null) {
            Debug.print("BroadcastReceiver was null. Creating new");
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getExtras().getParcelable("location") != null) {
                        Location location = intent.getExtras().getParcelable("location");
                        Debug.print("Location was received: " + location);
                        setCurrentLocation(location);
                        showLocationAndWeather(location, reverseGeocode(location));
                    } else {
                        Debug.print("Location was not received!");
                    }

                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    /**
     * Reverse geocodes location into Address object.
     *
     * Contains one Address object which is the closest Address based on the location
     * parameter.
     *
     * @param location The location that needs to be reverse geocode into address.
     * @return Closest address based on the location parameter.
     */
    public Address reverseGeocode(Location location) {
        Debug.print("Reverse geocoding started");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";
        ArrayList<Address> addressList = null;
        try {
            addressList = (ArrayList<Address>) geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            errorMessage = getString(R.string.service_not_available);
            Debug.print(errorMessage);
        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = getString(R.string.invalid_lat_long_used);
            Debug.print(errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude() + ". " +  illegalArgumentException);
        }

        if (addressList == null || addressList.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Debug.print(errorMessage);
            }
        } else {
            Debug.print("Address found");
            Debug.print(addressList.get(0).toString());
            return addressList.get(0);
        }
        return null;
    }

    /**
     * Stop LocationService which keeps track of location. Unregister broadcastreceiver
     * for location data.
     */
    public void stopTracking() {
        Debug.print("Tracking was stopped");
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
    }

    /**
     * Set current location
     * @param currentLocation location to be set as the current one.
     */
    public void setCurrentLocation(Location currentLocation) {
        Debug.print("Current location was set");
        this.currentLocation = currentLocation;
    }

    /**
     * Convert observation timestamp from epoch to Finnish time.
     *
     * @param epoch epoch timestamp from the time of observation.
     * @return formatted date time for Finnish time model.
     */
    public String timeDateOfObservation(long epoch) {
        String finnishTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                .format(new Date(epoch*1000).getTime() +
                        TimeUnit.HOURS.toMillis(2));
        return finnishTime;
    }

    /**
     *
     * @param jsonObject
     */
    @Override
    public void onCompletion(JSONObject jsonObject) {
        Weather weather = null;

        try {
            String temp = jsonObject.getJSONObject("main").getString("temp");
            String wspeed = jsonObject.getJSONObject("wind").getString("speed");
            String clouds = jsonObject.getJSONObject("clouds").getString("all");
            weather = new Weather(temp, wspeed, clouds);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        if (weather == null) {
            weatherView.setText(R.string.no_address_found);
        } else {
            String temperature = getResources().getString(R.string.temperature);
            String windSpeed = getResources().getString(R.string.windSpeed);
            String cloudiness = getResources().getString(R.string.cloudiness);

            weatherView.setText(temperature + " " + weather.getTemperature() + " °F\n" +
                    windSpeed + " " + weather.getWindSpeed() + " m/s\n" +
                    cloudiness + " " + weather.getCloudiness() + "%");
        }
    }

    /**
     * Set error message into weather TextView if failed to perform task.
     * @param message Message from DownloadJSONObject to be displayed.
     */
    @Override
    public void onFailure(String message) {
        weatherView.setText(message);
    }

}