package Business.Shops;

import Business.Products.SpecificProduct;

import java.util.ArrayList;

/**
 * The `ShopSponsored` class represents a sponsored shop that extends the base `Shop` class.
 * It includes additional functionality related to sponsored shops, such as handling a sponsored brand.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public class ShopSponsored extends Shop {

    /**
     * The sponsored brand associated with the shop.
     */
    private String sponsoredBrand;

    /**
     * Constructs a new `ShopSponsored` instance with the specified parameters.
     *
     * @param name           The name of the shop.
     * @param description    The description of the shop.
     * @param year           The year the shop was established.
     * @param earnings       The earnings of the shop.
     * @param model          The business model of the shop.
     * @param catalogue      The catalogue of products in the shop.
     * @param sponsoredBrand The sponsored brand associated with the shop.
     */
    public ShopSponsored(String name, String description, int year, float earnings, String model, ArrayList<SpecificProduct> catalogue, String sponsoredBrand) {
        super(name, description, year, model);
        this.earnings = earnings;
        this.catalogue = catalogue;
        this.sponsoredBrand = sponsoredBrand;
    }

    /**
     * Constructs a new `ShopSponsored` instance for a specific product in a shop.
     *
     * @param shopName     The name of the shop.
     * @param model        The business model of the shop.
     * @param productName  The name of the product.
     * @param productBrand The brand of the product.
     * @param productPrice The price of the product.
     */
    public ShopSponsored(String shopName, String model, String productName, String productBrand, float productPrice) {
        super(shopName);
        this.businessModel = model;
        this.catalogue.add(new SpecificProduct(productName, productBrand, productPrice));
    }

    /**
     * Gets the sponsored brand associated with the shop.
     *
     * @return The sponsored brand.
     */
    public String getSponsoredBrand() {
        return this.sponsoredBrand;
    }

    /**
     * Sets the sponsored brand associated with the shop.
     *
     * @param sponsoredBrand The sponsored brand to set.
     */
    public void setSponsoredBrand(String sponsoredBrand) {
        this.sponsoredBrand = sponsoredBrand;
    }
}