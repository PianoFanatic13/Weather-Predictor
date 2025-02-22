import java.util.*;
import java.io.*;

/*  Abdul-Raqib Islam
 *  12/6/24
 *  C3: Better than Spam
 *  TA: Sean Eglip
 *  This class implements the Classifiable interface and represents a piece of weather data.
 *  This class is used to train the classfication model, so the model can predict the summary label
 *  for weather data (eg. Partly Cloudy, Mostly Cloudy, etc.) based on weather features like 
 *  Temperature and Humidity.
 */ 
public class Weather implements Classifiable {
    
    public static final Set<String> FEATURES = Set.of("Temperature", "Humidity");

    public static final int TEMP_COL = 3;
    public static final int HUMID_COL = 5;
    public static final int WIND_COL = 6;

    private double temp;
    private double humidity;
    private double wind;

    //B: Constructs a weather object with temperature and humidity data from parsed weather data
    //E: None
    //R: None
    //P: Takes temp and humidity data as strings
    public Weather(String temp, String humidity, String Wind) {
        this.temp = Double.parseDouble(temp);
        this.humidity = Double.parseDouble(humidity);
        this.wind = Double.parseDouble(Wind);
    }

    //B: Gets the corresponding threshold value for the wanted feature in the weather data
    //E: Throws IllegalArgumentException if the feature being searched for isn't one contained
    //   in the weather data (Temperature and Humidity)
    //R: Returns the corresponding threshold value as a double for the searched feature
    //P: Takes a string for the feature with the wanted corresponding threshold value
    public double get(String feature) {
        if (feature.equals("Temperature")) {
            return this.temp;
        }
        else if (feature.equals("Humidity")) {
            return this.humidity;
        }
        else if (feature.equals("Wind")) {
            return this.wind;
        }
        else {
            throw new IllegalArgumentException("Trying to get invalid feature threshold");
        }
    }

    //B: Returns a list of all the features used for the weather data classificationn
    //E: None
    //R: Returns a set of strings pertaining to all the features used in the weather data
    //P: None
    public Set<String> getFeatures() {
        return FEATURES;
    }

    //B: Creates a weather object from a row of weather data
    //E: None
    //R: Returns a weather object created from a line of weather data
    //P: Takes a list of strings corrresponding to a line of weather data, assumes non-null
    public static Classifiable toClassifiable(List<String> row) {
        return new Weather(row.get(TEMP_COL), row.get(HUMID_COL), row.get(WIND_COL));
    }

    //B: Creates a new split representing the midpoint for the new threshold value of a feature
    //E: None
    //R: Returns a split with the new midpoint threshold value
    //P: Takes classifiable data to be compared to create the partition, assumes non-null
    public Split partition(Classifiable other) {
        Double threshold = 0.0;

        if (this.humidity != other.get("Humidity")) {
            threshold = (this.humidity + other.get("Humidity")) / 2;
            return new Split("Humidity", threshold);
        }
        else if (this.temp != other.get("Temperature")){
            threshold = (this.temp + other.get("Temperature")) / 2;
            return new Split("Temperature", threshold);
        }
        else {
            threshold = (this.wind + other.get("Wind")) / 2;
            return new Split("Wind", threshold);
        }
        
    }
}