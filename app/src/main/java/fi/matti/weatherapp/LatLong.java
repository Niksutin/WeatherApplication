package fi.matti.weatherapp;

/**
 * This class is to store Latitude and Longitude.
 *
 * Created by matti on 14.3.2018.
 */
public class LatLong {

    private double latitude;
    private double longitude;

    LatLong(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Checks to see if two LatLong objects are equal.
     *
     * @param o Object that is compared to the caller.
     * @return returns true if the two LatLong's are equal.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LatLong latLong = (LatLong) o;

        return Double.compare(latLong.latitude, latitude) == 0 &&
                Double.compare(latLong.longitude, longitude) == 0;
    }

    public String toString() {
        return "LatLong{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
