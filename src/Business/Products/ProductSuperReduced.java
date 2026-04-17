package Business.Products;

import java.util.ArrayList;
/**
 * The ProductSuperReduced class represents a specific type of product with additional attributes
 * such as a discount, extending the base Product class.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public class ProductSuperReduced extends Product{
    private float discount;
    /**
     * Constructor for creating a ProductSuperReduced with specified attributes.
     *
     * @param name       The name of the product.
     * @param brand      The brand of the product.
     * @param category   The category of the product.
     * @param priceMax   The maximum price of the product.
     * @param reviewList The list of reviews for the product.
     */
    public ProductSuperReduced(String name, String brand, String category, float priceMax, ArrayList<String> reviewList) {
        super(name, brand, category, priceMax, reviewList);
        discount = 0.04F;
    }
    /**
     * Retrieves the discount applied to the product.
     *
     * @return The discount applied to the product.
     */
    public float getDiscount(){
        return discount;
    }
    /**
     * Sets the discount for the product.
     *
     * @param discount The discount to be set for the product.
     */
    public void setDiscount(float discount){
        this.discount = discount;
    }
}
