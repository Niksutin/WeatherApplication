package fi.matti.weathero;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is to parse data of the weather observation, fetched from the FMI API.
 * Created by matti on 13.3.2018.
 */
class WeatherXmlParser {

    /**
     * Initiating method for the parsing of the InputStream. Parse based on the tags
     * given to t his method as parameters.
     *
     * @param in InputStream which will be parsed.
     * @param tags Tags which will be looked for in XML file.
     * @return List of Weather objects with data parsed from XML file.
     */
    List<Weather> parse(InputStream in, String... tags)
            throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readWeatherFeed(parser, tags);
        } finally {
            in.close();
        }
    }

    /**
     * Read the XML feed from the input stream with wanted tags. Converts parsed string arrays
     * into list of weather objects.
     *
     * @param parser parser for the XML data.
     * @param tags tags which are looked for from the XML data.
     */
    private List<Weather> readWeatherFeed(XmlPullParser parser, String... tags)
            throws XmlPullParserException, IOException {
        StringBuilder positions = new StringBuilder();
        StringBuilder values = new StringBuilder();
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            if (eventType == XmlPullParser.START_TAG) {
                name = parser.getName();
                if (name.equals(tags[0])) {
                    positions.append(parser.nextText());
                } else if (name.equals(tags[1])){
                    values.append(parser.nextText());
                }
            }
            eventType = parser.next();
        }
        String[] positionsArray = trimStringAndConvertToArray(positions.toString());
        String[] valuesArray = trimStringAndConvertToArray(values.toString());
        return toWeatherArray(positionsArray, valuesArray);
    }

    /**
     * Convert all the position and value data into Weather objects.
     *
     * @param positions Parsed positions from the XML as an array.
     * @param values Parsed weather valued from the XML as an array.
     * @return List of Weather objects parsed from the XML.
     */
    private List<Weather> toWeatherArray(String[] positions, String[] values) {
        ArrayList<Weather> weatherData = new ArrayList<>();
        for(int i = positions.length-1; i > 0 ; i--) {
            double lat = Double.valueOf(positions[i].split(" ")[0]);
            double lng = Double.valueOf(positions[i].split(" ")[1]);
            LatLong latlong = new LatLong(lat, lng);
            int timeStamp = Integer.valueOf(positions[i].split(" ")[2]);
            double temperature = Double.valueOf(values[i].split(" ")[0]);
            double windSpeed = Double.valueOf(values[i].split(" ")[1]);
            double windDirection = Double.valueOf(values[i].split(" ")[2]);
            double precipitation = Double.valueOf(values[i].split(" ")[3]);
            String cloudiness = String.valueOf(values[i].split(" ")[4]);
            weatherData.add(new Weather(latlong,
                    timeStamp,
                    temperature,
                    windSpeed,
                    windDirection,
                    precipitation,
                    cloudiness));
        }
        return weatherData;
    }

    /**
     * Trims the string and returns an array with needed values.
     * @param string This will be trimmed.
     * @return Array of values from FMI API.
     */
    private String[] trimStringAndConvertToArray(String string) {
        string = string.trim().replaceAll("\n", ",");
        String[] strings = string.split(",");
        for(int i = 0; i < strings.length; i++) {
            strings[i] = strings[i].trim();
            strings[i] = strings[i].replaceAll("\\s+", " ");
        }
        return strings;
    }
}
