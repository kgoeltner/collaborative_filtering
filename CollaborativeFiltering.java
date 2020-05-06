package pom.kgoeltner;

/**
 *  Program that performs hyper-parameter tuning on dataset via TrainValidationSplit
 *
 *  @author Karl Goeltner
 *  @version February 22, 2020
 */

import pom.kgoeltner.MatrixPrediction;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

//import org.apache.spark.mllib.recommendation.ALS;
//import org.apache.spark.ml.recommendation.ALS.Rating;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.ml.recommendation.ALS;

import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.ml.tuning.TrainValidationSplit;
import org.apache.spark.ml.tuning.TrainValidationSplitModel;

import java.util.ArrayList;
import java.util.List;


public class CollaborativeFiltering {
	
	public static void collFilter() {
		
		// Turn off unnecessary logging
		Logger.getLogger("org").setLevel(Level.OFF);
		Logger.getLogger("akka").setLevel(Level.OFF);
		
		// Create a Spark Session
		SparkSession spark = SparkSession
			      .builder()
			      .appName("JavaALSExample")
			      .master("local")
			      .getOrCreate();
		
		// Create a list of StructField that contains column headers: user, product, rating for Dataset<Row>
		List<StructField> fields = new ArrayList<>();
		StructField field1 = DataTypes.createStructField("user", DataTypes.IntegerType, true);
		StructField field2 = DataTypes.createStructField("product", DataTypes.IntegerType, true);
		StructField field3 = DataTypes.createStructField("rating", DataTypes.DoubleType, true);
		fields.add(field1);
		fields.add(field2);
		fields.add(field3);
		
		StructType schema = DataTypes.createStructType(fields);
		
		// Create a Dataset<Row> for Hyper-Parameter Training
		Dataset<Row> ratingsDS = spark.read().format("csv").schema(schema).load("/Users/kgoeltner/Desktop/CS/ML/maven-demo/src/main/resources/ratings.csv");
		//ratingsDS.show();
		
		// Split dataset into training & test sets
		Dataset<Row>[] splits = ratingsDS.randomSplit(new double[]{0.8, 0.2});
		Dataset<Row> training = splits[0];
		Dataset<Row> test = splits[1];

		
		// Build the recommendation model using ALS on the training data
	    ALS als = new ALS()
	    	.setUserCol("user")
	  	    .setItemCol("product")
	  	    .setRatingCol("rating");
	      /*.setMaxIter(5)
	      .setRegParam(0.01)*/
	      	    
	    // ParamGridBuilder constructs a grid of parameters to search over
	    // TrainValidationSplit will try all combinations of values and determine best model using the evaluator
	    ParamMap[] paramGrid = new ParamGridBuilder()
	    	      .addGrid(als.regParam(), new double[] {0.1, 0.01})
	    	      .addGrid(als.rank(), new int[] {10, 15})
	    	      .addGrid(als.maxIter(), new int[] {10, 15})
	    	      .build();

	    // In this case, use ALS as Estimator
	    // A TrainValidationSplit requires an Estimator, a set of Estimator ParamMaps, and an Evaluator
	    TrainValidationSplit trainValidationSplit = new TrainValidationSplit()
	    	      .setEstimator(als)
	    	      .setEvaluator(new RegressionEvaluator().setMetricName("rmse").setLabelCol("rating"))
	    	      .setEstimatorParamMaps(paramGrid)
	    	      .setTrainRatio(0.8)  // 80% for training and the remaining 20% for validation
	    	      .setParallelism(2);  // Evaluate up to 2 parameter settings in parallel
	    
	    // Run train validation split, and choose the best set of parameters
	    TrainValidationSplitModel model = trainValidationSplit.fit(training);
	    
	    // Print out the transformed model after reviewing the test dataset
	    model.transform(test)
	      .select("user", "product", "rating")
	      .show();
		
	    // Extract the best model from TrainValidationSplitModel
	    ALSModel bm = (ALSModel)model.bestModel();
	    
		int rank = bm.rank(); 													// Optimal rank
		int maxIter = ((ALS) model.bestModel().parent()).getMaxIter(); 			// Optimal max iterations
		double regParam = ((ALS) model.bestModel().parent()).getRegParam();		// Optimal reg-parameter
		
		// Print out optimal rank, max iterations, and reg parameter
		System.out.println("HYPER-TUNED PARAMETERS FOR ALS:");
	    System.out.println("rank: " + rank);
	    System.out.println("maxiter: " + maxIter);
	    System.out.println("regparam: " + regParam + "\n");
	    
	    spark.stop();

	    // Pass tuned hyper-parameters into MatrixPrediction to create top 3 recommendations per user
	    MatrixPrediction mp = new MatrixPrediction();
	    mp.recommend(rank, maxIter, regParam);        
		
	}
}
