package pom.kgoeltner;

/**
 *  Program that uses tuned hyper-parameters to make top 3 product predictions / user
 *
 *  @author Karl Goeltner
 *  @version February 22, 2020
 */

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MatrixPrediction {
	public void recommend(int rank, int maxIter, double regParam) {
		
		// Turn off unnecessary logging
		Logger.getLogger("org").setLevel(Level.OFF);
		Logger.getLogger("akka").setLevel(Level.OFF);
				
		SparkConf conf = new SparkConf().setAppName("Collaborative Filtering");
		conf.setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);
		
		//String usersPath = "/Users/kgoeltner/Desktop/CS/ML/maven-demo/src/main/resources/users.csv";
		// Read user file format - name#,name
		//JavaRDD<String> usersFile = sc.textFile(usersPath);
		
		String productsPath = "/Users/kgoeltner/Desktop/CS/ML/maven-demo/src/main/resources/products.csv";
		// Read product file format - product#,product
		//JavaRDD<String> productsFile = sc.textFile(productsPath);

		String ratingsPath = "/Users/kgoeltner/Desktop/CS/ML/maven-demo/src/main/resources/ratings.csv";
		// Read user-product rating file format - name#,product#,rating
		JavaRDD<String> userProductRatingsFile = sc.textFile(ratingsPath);

		JavaRDD<Rating> ratings = userProductRatingsFile.map(s -> {
			  String[] sarray = s.split(",");
			  return new Rating(Integer.parseInt(sarray[0]),
			    Integer.parseInt(sarray[1]),
			    Double.parseDouble(sarray[2]));
		});
		
		// Store product names from csv file into an array
		String[] products = storeProducts(productsPath);
		
		// Build the recommendation model using mllib ALS
		MatrixFactorizationModel mfModel = ALS.train(JavaRDD.toRDD(ratings), rank, maxIter, regParam);
		
		System.out.println("RECOMMENDED PRODUCTS / USER: ");
		
		// Print out the top 3 recommended products for each user
		for (int i=0; i<50; i++) {
			Rating[] recommendations = mfModel.recommendProducts(i, 3);
			System.out.print("USER " + i + ": ");
			System.out.print(products[recommendations[0].product()] + ", ");
			System.out.print(products[recommendations[1].product()] + ", ");
			System.out.println(products[recommendations[2].product()]);
		}
		
		sc.close();
		
		/*for (Rating rating : recommendations) {
        System.out.println(rating.product());
    	}*/
		
	}

	// Method to store products from csv file into a String array
	public static String[] storeProducts(String productsPath) {
		String[] products = new String[251];
		
		BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";
        int cnt = 1;

        try {

            br = new BufferedReader(new FileReader(productsPath));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] sep = line.split(csvSplitBy);
                products[cnt] = sep[1];
                cnt++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return products;
	}
}
