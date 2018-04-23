package fi.matti.weatherapp.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import fi.matti.weatherapp.Constants;
import fi.matti.weatherapp.DownloadJSONObject;
import fi.matti.weatherapp.ForecastsAdapter;
import fi.matti.weatherapp.Models.Weather;
import fi.matti.weatherapp.MyDownloadListener;
import fi.matti.weatherapp.R;
import fi.matti.weatherapp.Services.LocationService;
import fi.matti.weatherapp.Utils.Toaster;

/**
 * MainActivity class that holds all the main functionality of the Weatherapp.
 *
 * The whole application uses only this activity.
 *
 * Created by Niko on 10.3.2018.
 * Last updated by Niko on 23.4.2018.
 */
public class MainActivity extends AppCompatActivity implements MyDownloadListener {

    protected Location currentLocation;

    // BroadcastReceiver for LocationService
    private BroadcastReceiver broadcastReceiver;

    // TextView for city
    private TextView cityView;

    // - Current Weather
    // TextView for current weather
    private TextView currentWeatherView;

    // - Forecasts
    // RecyclerView for forecasts
    private RecyclerView recyclerView;
    // List of Weather objects (forecasts)
    ArrayList<Weather> forecasts = new ArrayList<>();

    // Messages that are shown to user
    private TextView messageView;

    // Loading spinner
    private ProgressBar progressBarSpinner;

    public Context context;

    // Permission check result
    int permissionCheck;

    /**
     * Basic onCreate method.
     * Sets views, Checks permissions and starts tracking Location.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        setViews();
        progressBarSpinner.setVisibility(View.VISIBLE);
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

    /**
     * Sets the views of the app. Sets RecyclerView unscrollable.
     */
    public void setViews() {
        cityView = findViewById(R.id.cityView);
        progressBarSpinner = findViewById(R.id.loadingSpinner);
        currentWeatherView = findViewById(R.id.currentWeatherView);
        messageView = findViewById(R.id.messageView);
        recyclerView = findViewById(R.id.forecasts);
        recyclerView.setNestedScrollingEnabled(false);
    }

    /**
     * Callback method for permission request. Returns with information regarding
     * user decision for permission.
     *
     * @param requestCode code for the permission that was asked.
     * @param permissions permissions.
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
                    Toaster.show(this, "Permission for GPS not granted");
                    finish();
                }
                break;
        }
    }

    /**
     * Shows the current city based on location. Initializes download Tasks for weather data.
     *
     * @param address address that was fetched based on latitude and longitude from gps.
     */
    public void showCityAndFetchWeather(Address address) {
         if (address == null) {
             messageView.setText(getResources().getText(R.string.service_not_available));
         } else {
             messageView.setText("");
             String currentCity;
             if (address.getLocality() == null) {
                 currentCity = address.getSubAdminArea();
             } else {
                 currentCity = address.getLocality();
             }

             cityView.setText(currentCity);

             new DownloadJSONObject(this)
                     .execute(Constants.QUERY_CURRENT +
                             currentCity + "," + address.getCountryCode() +
                             Constants.QUERY_UNITS_METRIC +
                             getResources().getString(R.string.hush));

             new DownloadJSONObject(this)
                     .execute(Constants.QUERY_FORECAST +
                             currentCity + "," + address.getCountryCode() +
                             Constants.QUERY_UNITS_METRIC +
                             getResources().getString(R.string.hush));
         }
    }

    /**
     * Stops tracking when activity gets destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTracking();
    }

    /**
     * Start LocationService for tracking location. Register broadcast receiver
     * which listens to location information from the LocationService.
     * Also handles other intents broadcast from the LocationService such as if the
     * gps was enabled or if the user need to be prompted with dialog to enable gps.
     */
    public void startTracking() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("gps_dialog")) {
                        messageView.setText(getResources().getText(R.string.gps_disabled));
                        showGpsDialog();
                    } else if (intent.getAction().equals("gps_enabled")) {
                        messageView.setText("");
                    } else {
                        if (intent.getExtras().getParcelable("location") != null) {
                            Location location = intent.getExtras().getParcelable("location");
                            setCurrentLocation(location);
                            showCityAndFetchWeather(reverseGeocode(location));
                        } else {
                            Toaster.show(context, "Location was not received!");
                        }
                    }
                }
            };
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("location_update");
        intentFilter.addAction("gps_dialog");
        intentFilter.addAction("gps_enabled");
        registerReceiver(broadcastReceiver, intentFilter);

        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    /**
     * Displays AlertDialog for enabling gps
     */
    public void showGpsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false)
                .setTitle("Gps is disabled. Want to enable it?")
                .setPositiveButton("Sure!", (dialog, whichButton) -> {
                    Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsIntent);
                }).setNegativeButton("Nope.", (dialog, whichButton) -> {
                    stopTracking();
                });
        builder.show();
    }

    /**
     * Reverse geocode location into Address object.
     *
     * Contains one Address object which is the closest Address based on the location
     * parameter.
     *
     * @param location The location that needs to be reverse geocode into address.
     * @return Closest address based on the location parameter.
     */
    public Address reverseGeocode(Location location) {
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
            messageView.setText(errorMessage);
        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = getString(R.string.invalid_lat_long_used);
        }

        if (addressList == null || addressList.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_valid_weather);
                messageView.setText(errorMessage);
            }
        } else {
            return addressList.get(0);
        }
        return null;
    }

    /**
     * Stop LocationService which keeps track of location. Unregister BroadcastReceiver
     * for location data.
     */
    public void stopTracking() {
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
        this.currentLocation = currentLocation;
    }

    /**
     * Callback for successfully completing DownloadJsonObject Task.
     * Sets loading spinner invisible and handles the JSONObject appropriately.
     *
     * If the JSONObject has a list, creates list of Weather objects as forecasts
     * If the JSONObject does not have list, creates single Weather object as current weather
     */
    @Override
    public void onCompletion(JSONObject jsonObject) {
        progressBarSpinner.setVisibility(View.INVISIBLE);
        if (jsonObject.has("list")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    forecasts.add(Weather.generateFromJSONObject(jsonArray.getJSONObject(i)));
                }
            } catch (JSONException e) {
                messageView.setText(getResources().getText(R.string.error_message));
            }
            ForecastsAdapter adapter = new ForecastsAdapter(forecasts);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            Weather weather = Weather.generateFromJSONObject(jsonObject);
            if (weather == null) {
                messageView.setText(R.string.no_valid_weather);
                stopTracking();
            } else {
                weather.showInTextView(currentWeatherView);
            }
        }
    }

    /**
     * Set error message into weather TextView if failed to perform task.
     * @param message Message from DownloadJSONObject to be displayed.
     */
    @Override
    public void onFailure(String message) {
        messageView.setText(message);
    }

}