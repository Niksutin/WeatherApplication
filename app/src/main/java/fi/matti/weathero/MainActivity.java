package fi.matti.weathero;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import fi.matti.weathero.Utils.Toaster;

public class MainActivity extends AppCompatActivity implements MyDownloadListener {

    protected Location currentLocation;
    private BroadcastReceiver broadcastReceiver;
    private TextView cityView;
    private TextView weatherView;
    private Locale locale;
    public Context context;
    String currentCity;
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
     * @param data String of data to be displayed.
     */
    public void showLocationAndWeather(String data) {
        currentCity = data.split(" ")[0];
        cityView.setText(currentCity);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        new DownloadWeatherTask(this).execute(Constants.SERVER_QUERY +
                currentCity +
                "&parameters=t2m,ws_10min,wd_10min,r_1h,n_man");
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
                    Location location = intent.getExtras().getParcelable("location");
                    String city = intent.getExtras().getString("city");
                    Debug.print("Location was received: " + location + " city: " + city);
                    setCurrentLocation(location);
                    showLocationAndWeather(city);
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
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
     * Set weatherView's text with the data gathered from DownLoadWeatherTask. Perform
     * this method after successful task completion.
     *
     * @param data List of Weather objects from result of DownloadWeatherTask.
     */
    @Override
    public void onCompletion(List<Weather> data) {
        if (data == null || data.size() == 0) {
            weatherView.setText(R.string.no_address_found);
        } else {
            Weather weather = data.get(0);
            String temperature = getResources().getString(R.string.temperature);
            String windSpeed = getResources().getString(R.string.windSpeed);
            String windDirection = getResources().getString(R.string.windDirection);
            String precipitation = getResources().getString(R.string.precipitation);
            String cloudiness = getResources().getString(R.string.cloudiness);

            weatherView.setText(timeDateOfObservation(weather.getTimeStamp()) + "\n" +
                    temperature + " " + weather.getTemperature() + " °C\n" +
                    windSpeed + " " + weather.getWindSpeed() + " m/s\n" +
                    windDirection + " " + weather.getWindDirection() + " °\n" +
                    precipitation + " " + weather.getPrecipitation() + " mm\n" +
                    cloudiness + " " + weather.getCloudiness());
        }
    }

    /**
     * Set error message into weather TextView if failed to perform task.
     * @param message Message from DownloadWeatherTask to be displayed.
     */
    @Override
    public void onFailure(String message) {
        weatherView.setText(message);
    }

}