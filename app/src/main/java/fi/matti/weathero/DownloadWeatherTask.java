package fi.matti.weathero;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is for downloading data with HttpUrlConnection without pausing the main thread.
 *
 * Created by matti on 13.3.2018.
 */
public class DownloadWeatherTask extends AsyncTask<String, Void, Boolean> {

    private final MyDownloadListener myDownloadListener;
    private String message;
    private List<Weather> weatherData = new ArrayList<>();

    DownloadWeatherTask(MyDownloadListener myDownloadListener) {
        this.myDownloadListener = myDownloadListener;
    }

    /**
     * Do a task in the background. Connect to a URL, get input stream and send it to
     * WeatherXMLParser which parses the weather data from the fetched XML file.
     *
     * @param uris Strings of URLs to connect to.
     * @return true if result was found, false if not.
     */
    @Override
    protected Boolean doInBackground(String... uris) {
        InputStream in = null;
        try {
            URL url = new URL(uris[0]);
            Debug.print(uris[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(Constants.READ_TIMEOUT);
            connection.setConnectTimeout(Constants.CONNECT_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            in = connection.getInputStream();
            WeatherXmlParser parser = new WeatherXmlParser();
            weatherData = parser.parse(in, Constants.WEATHER_DATA_POSITIONS, Constants.WEATHER_DATA_VALUES);
            return true;
        } catch (IOException | XmlPullParserException exception) {
            exception.printStackTrace();
            message = "Error occured. Download failed.";
        } finally {
            if (in != null) {
                Debug.print("Closing input stream!");
                try {
                    in.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Call MyDownloadListener's functions: onFailure/onCompletion based on
     * gotten result from doInBackground method above.
     *
     * @param result return value from doInBackground method.
     */
    @Override
    protected void onPostExecute(Boolean result) {
        if (!result) {
            if (myDownloadListener != null) myDownloadListener.onFailure(message);
            return;
        }
        if (myDownloadListener != null) myDownloadListener.onCompletion(weatherData);
    }
}
