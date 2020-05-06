package pom.kgoeltner;

/**
 *  Program to convert an JSONArray or ArrayList into a CSV file in src/main/resources
 *
 *  @author Karl Goeltner
 *  @version February 22, 2020
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;

public class CSVFileWriter {
     
    // Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
 
    // Method to convert JSONArray of users into csv file
    public static void writeUsersCSV(String fileName, JSONArray users) {
        FileWriter fileWriter = null;
        JSONObject name = new JSONObject();
        int cnt = 0;
        
        try {
            fileWriter = new FileWriter(fileName);
             
            // Write a new student object list to the CSV file
            for (Object user : users) {
            	// Store JSONObject of user
            	name = (JSONObject)user;
            	
                fileWriter.append(String.valueOf(cnt));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append((String)name.get("name"));
                fileWriter.append(NEW_LINE_SEPARATOR);
                
                cnt++;
            }
 
            System.out.println("users.csv was created successfully !!!");
             
        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
             
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
             
        }
    }
    
    // Method to convert JSONArray of products into csv file
    public static void writeProductsCSV(String fileName, JSONArray products) {
        FileWriter fileWriter = null;
        JSONObject product = new JSONObject();
        int cnt = 1;
        
        try {
            fileWriter = new FileWriter(fileName);
             
            // Write a new student object list to the CSV file
            for (Object prod : products) {
            	// Store JSONObject of user
            	product = (JSONObject)prod;
            	
                fileWriter.append(String.valueOf(cnt));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append((((String)product.get("word")).substring(0,1)).toUpperCase() + ((String)product.get("word")).substring(1));
                fileWriter.append(NEW_LINE_SEPARATOR);
                
                cnt++;
            }
 
            System.out.println("products.csv was created successfully !!!");
             
        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
             
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
             
        }
    }
    
    // Method to convert ArrayList of ratings into csv file
    public static void writeRatingsCSV(String fileName, ArrayList<RATING> ratings) {
        FileWriter fileWriter = null;
                 
        try {
            fileWriter = new FileWriter(fileName);
             
            // Write a new student object list to the CSV file
            for (RATING rating : ratings) {
                fileWriter.append(String.valueOf(rating.getName()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(rating.getProduct()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(rating.getRating()));
                fileWriter.append(NEW_LINE_SEPARATOR);
            }
 
            System.out.println("ratings.csv was created successfully !!!");
             
        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
             
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
             
        }
    }
}