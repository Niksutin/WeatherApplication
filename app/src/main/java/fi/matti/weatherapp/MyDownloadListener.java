package fi.matti.weatherapp;

import org.json.JSONObject;

/**
 * Interface for MyDownloadListener.
 *
 * Created by Niko on 14.3.2018.
 */
public interface MyDownloadListener {
    void onCompletion(JSONObject jsonObject);
    void onFailure(final String msg);
}
