package pom.kgoeltner;

/**
 *  Program to test out Machine Learning for a USER, PRODUCT, and RATING data-set.
 *	Uses auto-generation API HTTP-request calls for USER & PRODUCT names.
 *	Updated to create csv files for users, products, and ratings.
 *  Run Collaborative Filtering algorithm to generate top 3 products / user
 *
 *  @author Karl Goeltner
 *  @version February 22, 2020
 */
 
import pom.kgoeltner.CSVFileWriter;
import pom.kgoeltner.CollaborativeFiltering;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//import java.util.Random;

public class ProductRecommendation {

    // Declare data-sets to represent users, products, and ratings
    private JSONArray users;
    private JSONArray products;
    private ArrayList<RATING> ratings;

    // Constructor
    public ProductRecommendation() {
        users = new JSONArray();
        products = new JSONArray();
        ratings = new ArrayList<RATING>();
    }
 
    // Main Method 
    public static void main (String [] args) throws FileNotFoundException {
        ProductRecommendation run = new ProductRecommendation();
        
        run.createSets();			// Create the data sets for USERs and PRODUCTs
        run.generateRatings();      // Generate random quantity of ratings per user
        run.createCSV();			// Create a csv file with each rating / line
        run.runColl();				// Store Ratings into a series of Spark ratings tuples
        //run.printRatings();         // Test out printing of all ratings
    }
    
    // Method to store the names from API call data into users & products JSONArrays
    public void createSets() throws FileNotFoundException {
    	users = storeData("https://uinames.com/api/?amount=50&region=United%20States");
        products = storeData("https://api.datamuse.com/words?topics=appliance+hardware+clothing+furniture+household&max=250");
    }
    
    // Method to call APIs for USERs and PRODUCTs names and store in ArrayList
    public JSONArray storeData(String urlname) throws FileNotFoundException {
        
		// inline will store the JSON data streamed in string format
		String inline = "";
		
		// Return a JSONArray list of data - either USERs or PRODUCTs
		JSONArray list = new JSONArray();
		
		try {
			// Set-up url API connection based on url address
		    URL url = new URL(urlname);
		    HttpURLConnection con = (HttpURLConnection)url.openConnection();
		    con.setRequestMethod("GET");		
		    con.connect();
		    
		    // Get the response status of the API
			int responsecode = con.getResponseCode();
			
			// Iterating condition to if response code is not 200 then throw a runtime exception
			// else continue the actual process of getting the JSON data
			if(responsecode != 200)
				throw new RuntimeException("HttpResponseCode: " +responsecode);
			else {
				//Scanner functionality will read the JSON data from the stream
				Scanner sc = new Scanner(url.openStream());
				while(sc.hasNext())
					inline+=sc.nextLine();
					
				//Close the stream when reading the data has been finished
				sc.close();
			}
	
			// After reading JSON String, parse it to an Object -> JSONArray list
			JSONParser parser = new JSONParser();
			Object json = parser.parse(inline);
			list = (JSONArray) json;
			
			// Iterate through list 
			/*for(int i=0; i < list.size(); i++) {
				JSONObject obj = (JSONObject)list.get(i);
				//System.out.println(i + " " + obj.get("word"));
				//System.out.println(i + " " + obj.get("word"));
			}*/
			
		// Catch any unwanted errors
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
    }

    // Method to generate a random number of ratings / user-product;
    // last 8 users will have same ratings as first 8
    public void generateRatings() {
        double percent = 0;         // percent -> 25-40% of products to be rated / user
        int quantity = 0;           // quantity -> percent converted into quantity of products
        int productVal = 0;         // productVal -> Index to represent which product to be rated
        int first8 = 0;             // first8 -> Counter for the quantity of ratings for first 8 users
        int[] productInd = new int[50];     // productInd -> Array to check against repeat ratings / user
        int [] first8NameCount = new int[8];    // first8NameCount -> Array to store # of ratings / user
        
        //JSONObject user = new JSONObject();
        //JSONObject prod = new JSONObject();

        // Loop through users until last 8 and generate random rating values
        for (int user=0; user<42; user++) {
        	
        	// Store JSONObject of user
        	//user = (JSONObject)users.get(i);
        			
            // Generate a percent -> quantity in between 25-40% for #products to be rated
            percent = (double)(Math.random()*(.40-.25)+0.25);
            quantity = (int)(percent * 250);
            // Create repeat-checker array with values 1-250
            productInd = produce250Array();

            // Store the quantity of products each of the first 8 users rates in array
            if (user < 8)
                first8NameCount[user] = quantity;

            // Loop through random quantity of products to generate ratings
            for (int j=0; j<quantity; j++) {
                do {
                    // Generate a value 1-50 to determine which product should be rated
                    productVal = (int)(Math.random()*250+0);
                    
                    // Store JSONObject of selected product value
                    //prod = (JSONObject)products.get(productVal);

                    // As long as product hasn't been rated by the same user before, create a rating - capitalize product name
                    if (productInd[productVal] != 0)
                        ratings.add(new RATING(user, productVal+1, (int)(Math.random()*100+0)));

                // Keep looping until a random value is created such that ONLY a new product is chosen
                } while (productInd[productVal] == 0);
                // Set rated value to be 0 to confirm rating and avoid repeats
                productInd[productVal] = 0;

                // Increment rating counter for ONLY the first 8 users' ratings
                if (user < 8)
                    first8++;
            }
        }

        int id = 0;         // id -> represents  users 1-8
        int idcnt = 0;      // idcnt -> represents the quantity of ratings / users 1-8
        // Set final 8 users to equal all initial 8 users' ratings
        for (int x=0; x<first8; x++) {
            // Reset user ratings count when set count is met
            if (id != 8 && idcnt >= first8NameCount[id]) {
                id++;
                idcnt = 0;
            }

            // Store JSONObject of user
        	//user = (JSONObject)users.get(42+id);
        	
            // Copy the rating from first user to last user
            ratings.add(new RATING(42+id, ratings.get(x).getProduct(), ratings.get(x).getRating()));
            // Increment user rating count to ensure proper copying
            idcnt++;
        }
    }

    // Method to generate a list of values 1-250 for product-rating creation
    public int[] produce250Array() {
        int[] two_fifty_indices = new int[250];

        // Store 1-50 within each index of the array
        for (int i=0; i<250; i++)
        	two_fifty_indices[i] = i+1;

        // Return an array with values of 1-250
        return two_fifty_indices;
    }

    // Method to print out the ratings data-set
    public void printRatings() {
        // Test proper output for USER, PRODUCT, and RATING

        for (int i=0; i<ratings.size(); i++) {
            //System.out.println("USER: " + users.get(i));
            //System.out.println("PRODUCT: " + products.get(i));
            System.out.println(ratings.get(i));
        }
    }
    
    // Method to use RATINGS ArrayList to create a csv file with data values: Name, Product, Rating
    public void createCSV() {
    	String usersFile = "/Users/kgoeltner/Desktop/CS/ML/maven-demo/src/main/resources/users.csv";
    	String productsFile = "/Users/kgoeltner/Desktop/CS/ML/maven-demo/src/main/resources/products.csv";
    	String ratingsFile = "/Users/kgoeltner/Desktop/CS/ML/maven-demo/src/main/resources/ratings.csv";
    	CSVFileWriter.writeUsersCSV(usersFile, users);
    	CSVFileWriter.writeProductsCSV(productsFile, products);
    	CSVFileWriter.writeRatingsCSV(ratingsFile, ratings);
    }
    
    // Method to perform Collaborative Filtering upon 3 csv files to pick top 3 products / user
    public void runColl() {
    	CollaborativeFiltering.collFilter();
    }
}
