package fi.matti.weatherapp.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import fi.matti.weatherapp.Constants;

/**
 * This class is a Service that gets the Location based on GPS coordinates.
 *
 * Gets GPS location and broadcasts that location to BroadcastReceivers.
 * If GPS not enabled broadcasts intent to build AlertDialog.
 * If GPS is enabled broadcasts intent to clear error message.
 *
 * Created by Niko on 17.3.2018.
 * Last updated by Niko on 23.4.2018.
 */
public class LocationService extends Service {

    private LocationListener locationListener;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * LocationListener is set here to broadcast Location to the intent
     * with identifier: location_update <-- activates weather fetch
     * If gps is disabled broadcasts: gps_dialog <-- activates AlertDialog in MainActivity
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                broadcastLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                broadcastString("gps_enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                broadcastString("gps_dialog");
            }
        };

        locationManager = (LocationManager) getApplication().
                getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        Constants.GPS_TIME_INTERVAL,
                        Constants.GPS_MAX_DISTANCE,
                        locationListener);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        Constants.GPS_TIME_INTERVAL,
                        Constants.GPS_MAX_DISTANCE,
                        locationListener);
            }

        }
    }

    /**
     * Broadcast the location to BroadcastReceivers.
     *
     * @param location GPS location.
     */
    public void broadcastLocation(Location location) {
        Intent intent = new Intent("location_update");
        intent.putExtra("location", location);
        sendBroadcast(intent);
    }

    /**
     * Broadcast intent with just a String to be handled by BroadcastReceivers.
     *
     * @param intentName String object representing intent action.
     */
    public void broadcastString(String intentName) {
        Intent intent = new Intent(intentName);
        sendBroadcast(intent);
    }

    /**
     * Called when the service is destroyed. Removes updates
     * from the LocationListener.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
