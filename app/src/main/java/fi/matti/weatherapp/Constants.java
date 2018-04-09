package fi.matti.weatherapp;

/**
 *  Constant variables for various application parts
 *
 * Created by matti on 10.3.2018.
 */

final class Constants {

    // Updates GPS every 5 seconds
    static final long GPS_TIME_INTERVAL = 5000; // Milliseconds

    // Updates GPS every 0 meters
    static final float GPS_MAX_DISTANCE = 0; // Meters

    static final String SERVER_QUERY_SAMPLE =
            "http://samples.openweathermap.org/data/2.5/weather?q=" +
            "London,uk" +
            // "&units=metric" +
            "&appid=b6907d289e10d714a6e88b30761fae22";

    // Read timeout value for DownloadTask
    static final int READ_TIMEOUT = 100000; // Milliseconds
    // Connection timeout value for DownloadTask
    static final int CONNECT_TIMEOUT = 150000; // Milliseconds

}
