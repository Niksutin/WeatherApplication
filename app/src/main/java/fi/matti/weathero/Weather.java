package fi.matti.weathero;

/**
 * This class holds data for the weather.
 * Created by matti on 13.3.2018.
 */

public class Weather {

    private LatLong latlong;
    private long timeStamp;
    private double temperature;
    private double windSpeed;
    private double windDirection;
    private double precipitation;
    private String cloudiness;

    Weather(LatLong latlong,
            long timeStamp,
            double temperature,
            double windSpeed,
            double windDirection,
            double precipitation,
            String cloudiness) {
        this.latlong = latlong;
        this.timeStamp = timeStamp;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        setPrecipitation(precipitation);
        setCloudiness(cloudiness);
    }

    public LatLong getLatlong() {
        return latlong;
    }

    public void setLatlong(LatLong latlong) {
        this.latlong = latlong;
    }

    long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    double getPrecipitation() {
        return precipitation;
    }

    private void setPrecipitation(double precipitation) {
        if (Double.isNaN(precipitation)) {
            this.precipitation = 0.0;
        } else {
            this.precipitation = precipitation;
        }
    }

    public String getCloudiness() {
        return cloudiness;
    }

    private void setCloudiness(String cloudiness) {
        switch (cloudiness) {
            case "NaN":
                this.cloudiness = "Selkeää";
                break;
            case "1.0":
                this.cloudiness = "Selkeää";
                break;
            case "2.0":
                this.cloudiness = "Puolipilvistä";
                break;
            case "21.0":
                this.cloudiness = "Heikkoja sadekuuroja";
                break;
            case "22.0":
                this.cloudiness = "Voimakkaita sadekuuroja";
                break;
            case "3.0":
                this.cloudiness = "Pilvistä";
                break;
            case "31.0":
                this.cloudiness = "Heikkoa vesisadetta";
                break;
            case "32.0":
                this.cloudiness = "Vesisadetta";
                break;
            case "33.0":
                this.cloudiness = "Voimakasta vesisadetta";
                break;
            case "41.0":
                this.cloudiness = "Heikkoja lumikuuroja";
                break;
            case "42.0":
                this.cloudiness = "Lumikuuroja";
                break;
            case "43.0":
                this.cloudiness = "Voimakkaita lumikuuroja";
                break;
            case "51.0":
                this.cloudiness = "Heikkoa lumisadetta";
                break;
            case "52.0":
                this.cloudiness = "Lumisadetta";
                break;
            case "53.0":
                this.cloudiness = "Voimakasta lumisadetta";
                break;
            case "61.0":
                this.cloudiness = "Ukkoskuuroja";
                break;
            case "62.0":
                this.cloudiness = "Voimakkaita ukkoskuuroja";
                break;
            case "63.0":
                this.cloudiness = "Ukkosta";
                break;
            case "64.0":
                this.cloudiness = "Voimakasta ukkosta";
                break;
            case "71.0":
                this.cloudiness = "Heikkoja räntäkuuroja";
                break;
            case "72.0":
                this.cloudiness = "Räntäkuuroja";
                break;
            case "73.0":
                this.cloudiness = "Voimakkaita räntäkuuroja";
                break;
            case "81.0":
                this.cloudiness = "Heikkoa räntäsadetta";
                break;
            case "82.0":
                this.cloudiness = "Räntäsadetta";
                break;
            case "83.0":
                this.cloudiness = "Voimakasta räntäsadetta";
                break;
            case "91.0":
                this.cloudiness = "Utua";
                break;
            case "92.0":
                this.cloudiness = "Sumua";
                break;
            default: this.cloudiness = "?";
                break;
        }
    }

    public String toString() {
        return "Weather{" +
                "latlong=" + latlong +
                ", timeStamp=" + timeStamp +
                ", temperature=" + temperature +
                ", windSpeed=" + windSpeed +
                ", windDirection=" + windDirection +
                ", precipitation=" + precipitation +
                ", cloudiness=" + cloudiness +
                '}';
    }
}
