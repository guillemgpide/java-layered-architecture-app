package Business.Shops;

import Business.Products.SpecificProduct;

import java.util.ArrayList;
/**
 * The `ShopMaxBenefit` class represents a shop with a maximum benefit business model.
 * It extends the base `Shop` class and includes additional functionality related to the maximum benefit model.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public class ShopMaxBenefit extends Shop{
    /**
     * Constructs a new `ShopMaxBenefit` instance with the specified parameters.
     *
     * @param name        The name of the shop.
     * @param description The description of the shop.
     * @param year        The year the shop was established.
     * @param earnings    The earnings of the shop.
     * @param model       The business model of the shop.
     * @param catalogue   The catalogue of products in the shop.
     */
    public ShopMaxBenefit(String name, String description, int year, float earnings, String model, ArrayList<SpecificProduct> catalogue) {
        super(name, description, year, model);
        this.earnings = earnings;
        this.catalogue = catalogue;
    }
    /**
     * Constructs a new `ShopMaxBenefit` instance for a specific product in a shop.
     *
     * @param shopName     The name of the shop.
     * @param model        The business model of the shop.
     * @param productName  The name of the product.
     * @param productBrand The brand of the product.
     * @param productPrice The price of the product.
     */
    public ShopMaxBenefit(String shopName, String model, String productName, String productBrand, float productPrice) {
        super(shopName);
        this.businessModel = model;
        this.catalogue.add(new SpecificProduct(productName, productBrand, productPrice));
    }
}
