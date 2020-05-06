# collaborative_filtering
- (ProductRecommondation.java) Program to create a user, product, and rating data-set. Updated for auto-generation API HTTP-request calls for USER &amp; PRODUCT names. Run Collaborative Filtering algorithm to generate top 3 products / user </br>
- (CollaborativeFiltering.java) Program that performs hyper-parameter tuning on dataset via TrainValidationSplit using dataset generated in csv files</br>

Notes
- The feature vector that I am using for input is a Ratings object of JavaRDD<Rating> with three values: <userID, productID, rating> -> <Integer, Integer, Double> as per the definition of JavaRDD<Rating> provided by Spark
- The users and products are represented by corresponding ID numbers from csv files
- I then create a MatrixFactorizationModel with explicit training with parameters including the ratings dataset, rank, iterations, and lambda (regularization parameter)
- From there, I generate the top 3 products based upon the MatrixFactorizationModel for each of the 50 users and the output is printed
- My code files are attached below with ProductRecommendation.java being the main running file. CSVFileWriter.java converts the API data into a csv file and the CollaborativeFiltering.java runs the ALS algorithm on a MatrixFactorizationModel
