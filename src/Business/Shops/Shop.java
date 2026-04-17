package Business.Shops;

import Business.Products.SpecificProduct;

import java.util.ArrayList;

/**
 * The `Shop` class represents a shop in the shopping center with basic information and functions.
 * It is an abstract class that serves as a base for specific shop implementations.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public abstract class Shop {

    private String name;

    private String description;

    /**
     * The accumulated earnings of the shop.
     * This attribute is used to modify the earnings of the different kinds of shops.
     */
    protected float earnings;

    private int since;

    /**
     * The business model to which the shop belongs.
     */
    protected String businessModel;

    /**
     * The list of products in the shop's catalog.
     */
    protected ArrayList<SpecificProduct> catalogue;

    /**
     * Constructor to initialize all the attributes of a shop object.
     *
     * @param name        The name of the shop.
     * @param description The brief description of the shop.
     * @param year        The founding year of the shop.
     * @param model       The business model to which the shop belongs.
     */
    public Shop(String name, String description, int year, String model){
        this.name = name;
        this.description = description;
        this.since = year;
        this.businessModel = model;
        this.catalogue = new ArrayList<>();
        // esto ya no se podra usar pork es abstract, en el manager crearé directamente los Shop shop = new ShopLoyalty() por ejemplo para trabajar directamente con las herencias.
    }
    /**
     * Constructor to initialize a shop with a product in its catalog.
     *
     * @param name         The name of the shop.
     * @param businessModel The business model of the shop.
     * @param productName  The name of the product.
     * @param productBrand The brand of the product.
     * @param productPrice The price of the product.
     */
    public Shop(String name, String businessModel, String productName, String productBrand, float productPrice){
        this.name = name;
        this.businessModel = businessModel;
        this.catalogue = new ArrayList<>();
        this.catalogue.add(new SpecificProduct(productName, productBrand, productPrice));
    }
    /**
     * Constructor to initialize a shop with a specified name.
     *
     * @param shopName The name of the shop.
     */
    public Shop(String shopName) {
        this.name = shopName;
        this.catalogue = new ArrayList<>();
    }

    /**
     * Gets the name of the shop.
     *
     * @return The name of the shop.
     */
    public String getName() {
        return name;
    }
    /**
     * Gets the description of the shop.
     *
     * @return The description of the shop.
     */
    public String getDescription() {
        return description;
    }
    /**
     * Gets the founding year of the shop.
     *
     * @return The founding year of the shop.
     */
    public int getYear() {
        return since;
    }
    /**
     * Gets the accumulated earnings of the shop.
     *
     * @return The accumulated earnings of the shop.
     */
    public float getIncome() {
        return earnings;
    }
    /**
     * Gets the catalog of the shop, which contains a list of products.
     *
     * @return The catalog of the shop.
     */
    public ArrayList<SpecificProduct> getCatalogue() {
        return catalogue;
    }
    /**
     * Sets the catalog of the shop.
     *
     * @param catalogue The list of products to set in the catalog.
     */
    public void setCatalogue(ArrayList<SpecificProduct> catalogue) {
        this.catalogue = catalogue;
    }
    /**
     * Sets the accumulated earnings of the shop.
     *
     * @param income The earnings to set for the shop.
     */
    public void setIncome(float income) {
        this.earnings = income;
    }
    /**
     * Gets the business model of the shop.
     *
     * @return The business model of the shop.
     */
    public String getModel() {
        return businessModel;
    }
    /**
     * Gets the names of all products in the shop's catalog.
     *
     * @return ArrayList containing the names of all products in the shop's catalog.
     */
    public ArrayList<String> getCatalogueNames(){
        ArrayList<String> catalogueNames = new ArrayList<>();
        for(int compt = 0; compt < catalogue.size(); compt++){
            catalogueNames.add(catalogue.get(compt).getName());
        }
        return catalogueNames;
    }
}
