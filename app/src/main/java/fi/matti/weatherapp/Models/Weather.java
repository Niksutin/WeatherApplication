package fi.matti.weatherapp.Models;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fi.matti.weatherapp.R;

/**
 * This is a model class that holds data for a single Weather object.
 *
 * Contains following information of weather data:
 * - Temperature (currently only in Celsius)
 * - Wind Speed (currently only in meters/second)
 * - Humidity (in percentages)
 * - Time (in format: day, month hours:minutes)
 * - Weather Icon (in integer value of the resource file location eg. R.drawable.ic_clodudy)
 *
 * Created by Niko on 13.3.2018.
 * Last updated by Niko on 23.4.2018.
 */

public class Weather {

    private double temperature;
    private double windSpeed;
    private String humidity;
    private String time;
    public int weatherIcon;

    Weather(double temperature,
            double windSpeed,
            String humidity) {
        setTemperature(temperature);
        setWindSpeed(windSpeed);
        setHumidity(humidity);
    }

    private Weather(String temperature,
            String windSpeed,
            String humidity,
            long dt,
            String weatherIcon) {
        setTemperature(temperature);
        setWindSpeed(windSpeed);
        setHumidity(humidity);
        setTimeEpoch(dt);
        setWeatherIcon(weatherIcon);
    }

    public double getTemperature() {
        return temperature;
    }

    private void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    private void setTemperature(String temperature) {
        this.temperature = Double.valueOf(temperature);
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    private void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    private void setWindSpeed(String windSpeed) {
        this.windSpeed = Double.valueOf(windSpeed);
    }

    public  String getHumidity() {
        return humidity;
    }

    private void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Set current time String from epoch time parameter (in format: day, month hours:minutes).
     * @param epoch epoch time to be converted into String.
     */
    private void setTimeEpoch(long epoch) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM HH:mm",
                Locale.getDefault());
        this.time = formatter.format(new Date(epoch*1000));
    }

    private int getWeatherIcon() {
        return weatherIcon;
    }

    /**
     * Set weather icon based on data from the API.
     * @param weatherIcon String of the weather icon to be displayed.
     */
    private void setWeatherIcon(String weatherIcon) {
        switch (weatherIcon.toLowerCase()) {
            case "rain":
                this.weatherIcon = R.drawable.ic_rain;
                break;
            case "snow":
                this.weatherIcon = R.drawable.ic_snow;
                break;
            case "clear":
                this.weatherIcon = R.drawable.ic_clear_day;
                break;
            case "thunder":
                this.weatherIcon = R.drawable.ic_thunder;
                break;
            case "clouds":
                this.weatherIcon = R.drawable.ic_cloudy;
                break;
            default:
                this.weatherIcon = R.drawable.ic_cloudy;
                break;
        }
    }

    /**
     * Shows Weather object data in TextView parameter.
     * @param textView TextView where the data is displayed.
     */
    public void showInTextView(TextView textView) {
        String temperature = textView.getResources().getString(R.string.temperature);
        String windSpeed = textView.getResources().getString(R.string.windSpeed);
        String humidity = textView.getResources().getString(R.string.humidity);
        String displayedText = temperature + " " + getTemperature() + " °C\n" +
                windSpeed + " " + getWindSpeed() + " m/s\n" +
                humidity + " " + getHumidity() + " %";
        textView.setCompoundDrawablesWithIntrinsicBounds( 0, getWeatherIcon(), 0, 0);
        textView.setText(displayedText);
    }

    /**
     * Generates Weather object based on JSONObject parameter.
     * @param jsonObject JSONObject where the weather data is gathered.
     * @return Weather object containing weather data or null.
     */
    public static Weather generateFromJSONObject(JSONObject jsonObject) {
        try {
            String temperature = jsonObject.getJSONObject("main").getString("temp");
            String windSpeed = jsonObject.getJSONObject("wind").getString("speed");
            String humidity = jsonObject.getJSONObject("main").getString("humidity");
            long dateTime = jsonObject.getLong("dt");
            String weatherIcon = jsonObject.getJSONArray("weather")
                    .getJSONObject(0)
                    .getString("main");
            return new Weather(temperature, windSpeed, humidity, dateTime, weatherIcon);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public String toString() {
        return "Weather{" +
                ", temperature=" + temperature +
                ", windSpeed=" + windSpeed +
                ", humidity=" + humidity +
                '}';
    }
}
