package Business.Shops;

import Business.Products.SpecificProduct;

import java.util.ArrayList;
/**
 * The `ShopLoyalty` class represents a shop with a loyalty business model.
 * It extends the base `Shop` class and includes additional functionality related to the loyalty model.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public class ShopLoyalty extends Shop{
    private float loyaltyThreshold;
    /**
     * Constructs a new `ShopLoyalty` instance with the specified parameters.
     *
     * @param name            The name of the shop.
     * @param description     The description of the shop.
     * @param year            The year the shop was established.
     * @param earnings        The earnings of the shop.
     * @param model           The business model of the shop.
     * @param catalogue       The catalogue of products in the shop.
     * @param loyaltyThreshold The loyalty threshold for the shop.
     */
    public ShopLoyalty(String name, String description, int year, float earnings, String model, ArrayList<SpecificProduct> catalogue, float loyaltyThreshold) {
        super(name, description, year, model);
        this.earnings = earnings;
        this.catalogue = catalogue;
        this.loyaltyThreshold = loyaltyThreshold;
    }
    /**
     * Constructs a new `ShopLoyalty` instance for a specific product in a shop.
     *
     * @param shopName       The name of the shop.
     * @param model          The business model of the shop.
     * @param productName    The name of the product.
     * @param productBrand   The brand of the product.
     * @param productPrice   The price of the product.
     */
    public ShopLoyalty(String shopName, String model, String productName, String productBrand, float productPrice) {
        super(shopName);
        this.businessModel = model;
        this.catalogue.add(new SpecificProduct(productName, productBrand, productPrice));
    }

    /**
     * Gets the loyalty threshold for the shop.
     *
     * @return The loyalty threshold.
     */
    public float getLoyaltyThreshold(){
        return  loyaltyThreshold;
    }
    /*public void setLoyaltyThreshold(float loyaltyThreshold){
        this.loyaltyThreshold = loyaltyThreshold;
    }*/

}
