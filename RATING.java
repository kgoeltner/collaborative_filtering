package pom.kgoeltner;

/**
 *  Object to represent an individual rating which includes: 
 *	a user, product and rating (int, int, double)
 *
 *  @author Karl Goeltner
 *  @version February 1, 2020
 */

public class RATING {
	private int name;
	private int product;
	private double rating;

	public RATING(int id, int prod, double rate) {
		name = id;
		product = prod;
		rating = rate;
	}

	// Public accessor for user name
	public int getName() {
		return name;
	}

	// Public accessor for product
	public int getProduct() {
		return product;
	}

	// Public accessor for rating
	public double getRating() {
		return rating;
	}

    @Override
    public String toString() {
        return String.format("%d %d %.0f%%", name, product, rating);
    }

}