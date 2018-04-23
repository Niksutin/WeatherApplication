package fi.matti.weatherapp;

/**
 * Constant variables for various parts of the application
 *
 * Created by matti on 10.3.2018.
 * Last updated by Niko on 23.4.2018.
 */
public final class Constants {

    // Updates GPS every 60 seconds
    public static final long GPS_TIME_INTERVAL = 60000; // Milliseconds

    // Updates GPS every 500 meters
    public static final float GPS_MAX_DISTANCE = 500; // Meters

    // Query urls
    public static final String QUERY_CURRENT = "http://api.openweathermap.org/data/2.5/weather?q=";

    public static final String QUERY_FORECAST = "http://api.openweathermap.org/data/2.5/forecast?q=";

    // Query ic_temperature units
    public static final String QUERY_UNITS_METRIC = "&units=metric";

    // Read timeout value for DownloadTask
    static final int READ_TIMEOUT = 10000; // Milliseconds

    // Connection timeout value for DownloadTask
    static final int CONNECT_TIMEOUT = 15000; // Milliseconds

}
