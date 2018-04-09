package fi.matti.weatherapp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is for downloading data with HttpUrlConnection without pausing the main thread.
 *
 * Created by matti on 13.3.2018.
 */
public class DownloadJSONObject extends AsyncTask<String, Void, Boolean> {

    private final MyDownloadListener myDownloadListener;
    private String message;
    private JSONObject jsonObject;

    DownloadJSONObject(MyDownloadListener myDownloadListener) {
        this.myDownloadListener = myDownloadListener;
    }

    /**
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
            String line;
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            while((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            in.close();
            jsonObject = new JSONObject(result.toString());
            return true;
        } catch (IOException |JSONException exception) {
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
        if (myDownloadListener != null) myDownloadListener.onCompletion(jsonObject);
    }
}
