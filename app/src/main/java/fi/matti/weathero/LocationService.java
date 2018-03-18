package fi.matti.weathero;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This class is a Service that gets the Location based on GPS coordinates.
 *
 * Created by matti on 17.3.2018.
 * Last updated by matti on 17.3.2018.
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
     * LocationListener is set here in to broadcast Location to the intent
     * with identifier: location_update.
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
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        locationManager = (LocationManager) getApplication().
                getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    Constants.GPS_TIME_INTERVAL,
                    Constants.GPS_MAX_DISTANCE,
                    locationListener);
        }
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
        } catch(IOException ioException) {
            errorMessage = getString(R.string.service_not_available);
            Debug.print(errorMessage);
        } catch(IllegalArgumentException illegalArgumentException) {
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
     * Broadcasts the location and reverse geocoded city.
     *
     * @param location GPS location.
     */
    public void broadcastLocation(Location location) {
        Intent intent = new Intent("location_update");
        intent.putExtra("location", location);
        String city = reverseGeocode(location).getSubAdminArea();
        intent.putExtra("city", city);
        Debug.print("Location was broadcast: " + location);
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
