package fi.matti.weathero;

import java.util.List;

/**
 * Interface for MyDownloadListener.
 *
 * Created by matti on 14.3.2018.
 */
public interface MyDownloadListener {
    void onCompletion(List<Weather> data);
    void onFailure(final String msg);
}
