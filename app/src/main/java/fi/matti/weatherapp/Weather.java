package fi.matti.weatherapp;

/**
 * This class holds data for the weather.
 * Created by matti on 13.3.2018.
 */

public class Weather {

    private double temperature;
    private double windSpeed;
    private String cloudiness;

    Weather(double temperature,
            double windSpeed,
            String cloudiness) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.cloudiness = cloudiness;
    }

    Weather(String temperature,
            String windSpeed,
            String cloudiness) {
        setTemperature(temperature);
        setWindSpeed(windSpeed);
        this.cloudiness = cloudiness;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = Double.valueOf(temperature);
    }

    double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = Double.valueOf(windSpeed);
    }

    public String getCloudiness() {
        return cloudiness;
    }

    private void setCloudiness(String cloudiness) {
        this.cloudiness = cloudiness;
    }

    public String toString() {
        return "Weather{" +
                ", temperature=" + temperature +
                ", windSpeed=" + windSpeed +
                ", cloudiness=" + cloudiness +
                '}';
    }
}
