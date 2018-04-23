package fi.matti.weatherapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fi.matti.weatherapp.Models.Weather;

/**
 * Adapter for the forecast items in RecyclerView
 *
 * Created by Niko on 15.4.2018.
 * Last updated by Niko on 23.4.2018.
 */

public class ForecastsAdapter extends
        RecyclerView.Adapter<ForecastsAdapter.ViewHolder> {

    /**
     * ViewHolder for RecyclerView. Vies are based from item_forecast.xml file.
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView forecastDateTextView;
        ImageView weatherImage;
        TextView forecastTemperatureTextView;
        TextView forecastWindSpeedTextView;
        TextView forecastsHumidityTextView;

        ViewHolder(View itemView) {
            super(itemView);
            forecastDateTextView = itemView.findViewById(R.id.forecastDateTextView);
            weatherImage = itemView.findViewById(R.id.weatherIconView);
            forecastTemperatureTextView = itemView.findViewById(R.id.forecastTemperatureTextView);
            forecastWindSpeedTextView = itemView.findViewById(R.id.forecastWindSpeedTextView);
            forecastsHumidityTextView = itemView.findViewById(R.id.forecastHumidityTextView);

        }
    }

    // List of weather objects
    private List<Weather> forecasts;

    public ForecastsAdapter(List<Weather> forecasts) {
        this.forecasts = forecasts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_forecasts, parent, false);
        return new ViewHolder(contactView);
    }

    /**
     *
     * @param holder of the Views in a single RecyclerView item.
     * @param position in the RecyclerView.
     */
    @Override
    public void onBindViewHolder(@NonNull ForecastsAdapter.ViewHolder holder, int position) {
        Weather weather = forecasts.get(position);

        TextView textClock = holder.forecastDateTextView;
        ImageView weatherIcon = holder.weatherImage;
        TextView temperatureTextView = holder.forecastTemperatureTextView;
        TextView windSpeedTextView = holder.forecastWindSpeedTextView;
        TextView humidityTextView = holder.forecastsHumidityTextView;

        // Set the time for the item
        textClock.setText(weather.getTime());

        // Set the weather icon for the item
        weatherIcon.setImageResource(weather.weatherIcon);

        // Set the temperature for the item
        temperatureTextView.setText(weather.getTemperature() + " °C");

        // Set the wind speed for the item
        windSpeedTextView.setText(weather.getWindSpeed() + " m/s");

        // Set the humidity for the item
        humidityTextView.setText(weather.getHumidity() + " %");

    }

    /**
     * @return item count.
     */
    @Override
    public int getItemCount() {
        return forecasts.size();
    }
}
