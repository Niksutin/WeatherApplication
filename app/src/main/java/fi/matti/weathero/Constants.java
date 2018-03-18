package fi.matti.weathero;

/**
 *  Constant variables for various application parts
 *
 * Created by matti on 10.3.2018.
 */

final class Constants {

    private static final String FMI_API_KEY = "";

    // Updates GPS every 5 seconds
    static final long GPS_TIME_INTERVAL = 5000; // Milliseconds

    // Updates GPS every 0 meters
    static final float GPS_MAX_DISTANCE = 0; // Meters

    // Stored query from FMI API for weather observations
    private static final String STORED_QUERY_OBSERVATION =
            "fmi::observations::weather::multipointcoverage&place=";

    // Url to
    static final String SERVER_QUERY = "http://data.fmi.fi/fmi-apikey/" +
            FMI_API_KEY +
            "/wfs?request=getFeature&storedquery_id=" +
            STORED_QUERY_OBSERVATION;

    // Tags to search from downloaded XML
    static final String WEATHER_DATA_VALUES = "gml:doubleOrNilReasonTupleList"; // Values
    static final String WEATHER_DATA_POSITIONS = "gmlcov:positions"; // Positions

    // Read timeout value for DownloadTask
    static final int READ_TIMEOUT = 100000; // Milliseconds
    // Connection timeout value for DownloadTask
    static final int CONNECT_TIMEOUT = 150000; // Milliseconds

}
