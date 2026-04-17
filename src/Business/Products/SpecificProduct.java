package Business.Products;

import Business.Products.Product;

import java.util.ArrayList;
/**
 * The SpecificProduct class extends the Product class and represents a specific instance of a product
 * with additional attributes like price, ratings, and reviews specific to a shop.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public class SpecificProduct extends Product {
    private float productPrice;
    private ArrayList<String> productRating;
    private ArrayList<String> reviewsForShop;
    /**
     * Constructor for creating a SpecificProduct with specified attributes.
     *
     * @param productName   The name of the product.
     * @param price          The price of the product.
     * @param productBrand   The brand of the product.
     * @param productRating  The ratings of the product.
     * @param productReviews The reviews for the shop.
     */
    public SpecificProduct(String productName, float price, String productBrand, ArrayList<String> productRating, ArrayList<String> productReviews) {
        super(productName);
        this.productPrice = price;
        this.productRating = (productRating != null) ? productRating : new ArrayList<>();
        this.setBrand(productBrand);
        this.reviewsForShop = (productReviews != null) ? productReviews : new ArrayList<>();
    }
    /**
     * Constructor for creating a SpecificProduct with specified attributes.
     *
     * @param productName  The name of the product.
     * @param productBrand  The brand of the product.
     * @param productPrice  The price of the product.
     */
    public SpecificProduct(String productName, String productBrand, float productPrice){
        super(productName);
        this.setPrice(productPrice);
        this.setBrand(productBrand);
    }
    /**
     * Retrieves the price of the product.
     *
     * @return The price of the product.
     */
    public float getPrice(){
        return productPrice;
    }
    /**
     * Sets the price of the product.
     *
     * @param price The price to set for the product.
     */
    public void setPrice(float price){
        this.productPrice = price;
    }
    /**
     * Adds a rating to the product.
     *
     * @param rating The rating to add to the product.
     */
    public void addRatings(String rating) {
        this.productRating.add(rating);
    }
    /**
     * Adds a review for the shop.
     *
     * @param review The review to add for the shop.
     */
    public void addProductsReviews(String review){
        this.reviewsForShop.add(review);
    }
    /**
     * Sets the ratings for the product.
     *
     * @param ratings The ratings to set for the product.
     */
    public void setRatings(ArrayList<String> ratings){
        this.productRating = ratings;
    }
    /**
     * Sets the reviews for the shop.
     *
     * @param reviews The reviews to set for the shop.
     */
    public void setReviewsForShop(ArrayList<String> reviews){
        this.reviewsForShop = reviews;
    }
    /**
     * Retrieves the ratings of the product.
     *
     * @return ArrayList containing the ratings of the product.
     */
    public ArrayList<String> getProductRating(){
        return productRating;
    }
    /**
     * Retrieves the reviews for the shop.
     *
     * @return ArrayList containing the reviews for the shop.
     */
    public ArrayList<String> getReviewsForShop(){
        return reviewsForShop;
    }
    /**
     * Retrieves the discount for the product.
     *
     * @return The discount for the product.
     */
    public float getDiscount(){return 0.0F;}
}
