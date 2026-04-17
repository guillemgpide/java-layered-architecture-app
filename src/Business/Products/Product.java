package Business.Products;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Clase que guarda informació i atributs sobre els productes, també amb funcions bàsiques respectives.
 *
 * @author : Bruno Bordoy, Guillem Gil
 * @version : 13/11/2023
 */
public abstract class Product {
    @SerializedName("name")
    private String productName;
    @SerializedName("brand")
    private String productBrand;
    //productCategory tot i no ser utilitzat es usat per guardar l'atribut al Json.
    @SerializedName("category")
    private String productCategory;
    @SerializedName("mrp")
    private float productMaxPrice;
    @SerializedName("reviews")
    private ArrayList<String> productReview;
    /**
     * Constructor for creating a Product with specified attributes.
     *
     * @param name      The name of the product.
     * @param brand     The brand of the product.
     * @param category  The category of the product.
     * @param priceMax  The maximum retail price of the product.
     */
    public Product(String name, String brand, String category, float priceMax){
        this.productName = name;
        this.productBrand = brand;
        this.productCategory = category;
        this.productMaxPrice = priceMax;
        this.productReview = new ArrayList<>();
    }
    /**
     * Constructor for creating a Product with only the name.
     *
     * @param name The name of the product.
     */
    public Product(String name){
        this.productName = name;
        this.productBrand = null;
        this.productCategory = null;
        this.productMaxPrice = 0;//potser en un futur també a shops haurà d'arribar priceMax per alguna raó (si no pogués inicialitzar des d'aquí podria fer el mateix que amb name i brand)
        this.productReview = new ArrayList<>();
    }
    /**
     * Constructor for creating a Product with specified attributes, including reviews.
     *
     * @param name      The name of the product.
     * @param brand     The brand of the product.
     * @param category  The category of the product.
     * @param maxPrice  The maximum retail price of the product.
     * @param reviews   The reviews for the product.
     */
    public Product(String name, String brand, String category, float maxPrice, ArrayList<String> reviews) {
        this.productName = name;
        this.productBrand = brand;
        this.productCategory = category;
        this.productMaxPrice = maxPrice;
        this.productReview = reviews;
    }

    /**
     * Retrieves the name of the product.
     *
     * @return The name of the product.
     */
    public String getName(){
        return productName;
    }
    /**
     * Retrieves the maximum retail price of the product.
     *
     * @return The maximum retail price of the product.
     */
    public float getPriceMax(){
        return productMaxPrice;
    }
    /**
     * Retrieves the brand of the product.
     *
     * @return The brand of the product.
     */
    public String getBrand() {
        return productBrand;
    }
    /**
     * Sets the brand of the product.
     *
     * @param brand The brand to set for the product.
     */
    public void setBrand(String brand){
        this.productBrand = brand;
    }
    /**
     * Retrieves the reviews of the product.
     *
     * @return The reviews of the product.
     */
    public ArrayList<String> getReviews() {
        return productReview;
    }
    /**
     * Adds a review to the product.
     *
     * @param review The review to add to the product.
     */
    public void setReview(String review) {
        this.productReview.add(review);
    }
    /**
     * Sets the reviews for the product by adding the specified reviews to the existing ones.
     *
     * @param reviews The reviews to be added to the product.
     */
    public void setReviews(ArrayList<String> reviews){
        this.productReview.addAll(reviews);
    }
    /**
     * Retrieves the category of the product.
     *
     * @return The category of the product.
     */
    public String getCategory(){
        return productCategory;
    }
    /**
     * Abstract method to retrieve the discount applied to the product.
     *
     * @return The discount applied to the product.
     */
    public abstract float getDiscount();
    /**
     * Abstract method to set the discount for the product.
     *
     * @param discount The discount to be set for the product.
     */
    public void setDiscount(float discount) {
    }
}

