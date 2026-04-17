package Business;

import Business.Shops.Shop;
import Business.Shops.ShopLoyalty;
import Business.Shops.ShopMaxBenefit;
import Business.Shops.ShopSponsored;

import java.lang.reflect.AnnotatedArrayType;
import java.util.ArrayList;

/**
 * Represents a shopping cart that contains products from different shops.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public class Cart {
    private ArrayList<String> possibleThresholdPrice;
    private ArrayList<String> currentThresholdPrice;
    private ArrayList<Shop> shops;
    private ArrayList<String> possibleSponsoredBrand;
    private ArrayList<Boolean> activeThresholdDiscount;
    /**
     * Constructs a new Cart with default values for its properties.
     */
    public Cart(){
        possibleThresholdPrice = new ArrayList<>();
        currentThresholdPrice = new ArrayList<>();
        shops = new ArrayList<>();
        possibleSponsoredBrand = new ArrayList<>();
        activeThresholdDiscount = new ArrayList<>();
    }
    /**
     * Adds a product to the specified shop in the cart.
     *
     * @param shopName      The name of the shop.
     * @param model         The business model of the shop.
     * @param productName   The name of the product.
     * @param productBrand  The brand of the product.
     * @param productPrice  The price of the product.
     */
    public void setProductToShop(String shopName, String model, String productName, String productBrand, float productPrice){
        switch (model) {
            case "LOYALTY" -> shops.add(new ShopLoyalty(shopName, model, productName, productBrand, productPrice));
            case "MAXIMUM BENEFITS" -> shops.add(new ShopMaxBenefit(shopName, model, productName, productBrand, productPrice));
            case "SPONSORED" -> shops.add(new ShopSponsored(shopName, model, productName, productBrand, productPrice));
            default -> {
            }
        }
    }
    /**
     * Gets the total number of shops in the cart.
     *
     * @return The size of the cart.
     */
    public int getCartSize(){
        return shops.size();
    }
    /**
     * Gets the price of the first product in the specified shop in the cart.
     *
     * @param comptadorCart The index of the shop in the cart.
     * @return The price of the first product in the specified shop.
     */
    /*public float getShopPrice(int comptadorCart){
        return shops.get(comptadorCart).getCatalogue().get(0).getPrice();
    }*/
    /**
     * Gets the name of the shop at the specified index in the cart.
     *
     * @param comptadorCart The index of the shop in the cart.
     * @return The name of the shop.
     */
    public String getShopName(int comptadorCart){
        return shops.get(comptadorCart).getName();
    }
    /**
     * Gets the name of the first product in the specified shop in the cart.
     *
     * @param comptadorCart The index of the shop in the cart.
     * @return The name of the product.
     */
    public String getCartProductName(int comptadorCart) {
        return shops.get(comptadorCart).getCatalogue().get(0).getName();
    }
    /**
     * Gets the brand of the first product in the specified shop in the cart.
     *
     * @param comptadorCart The index of the shop in the cart.
     * @return The brand of the product.
     */
    public String getCartProductBrand(int comptadorCart) {
        return shops.get(comptadorCart).getCatalogue().get(0).getBrand();
    }
    /**
     * Gets the price of the first product in the specified shop in the cart.
     *
     * @param comptadorCart The index of the shop in the cart.
     * @return The price of the product.
     */
    public float getCartProductPrice(int comptadorCart) {
        return shops.get(comptadorCart).getCatalogue().get(0).getPrice();
    }
    /**
     * Removes all products from the shopping cart.
     */
    public void removeProducts() {
        shops.clear();
    }

    /**
     * Returns all the shops from the cart.
     *
     * @return All the shops from the cart.
     */
    public ArrayList<Shop> getShops(){
        return shops;
    }
    /**
     * Sets a new value to all the shops in the cart.
     *
     * @param shops The updated shops we want to add to the cart.
     */
    public void setShops(ArrayList<Shop> shops){
        this.shops = shops;
    }
    /**
     * Gets the possible threshold prices for shops in the cart.
     *
     * @return The list of possible threshold prices.
     */
    public ArrayList<String> getPossibleThresholdPrice(){
        return possibleThresholdPrice;
    }
    /**
     * Gets the current threshold prices for shops in the cart.
     *
     * @return The list of current threshold prices.
     */
    public ArrayList<String> getCurrentThresholdPrice(){
        return currentThresholdPrice;
    }
    /**
     * Sets the current threshold prices for shops in the cart.
     *
     * @param currentThresholdPrice The list of current threshold prices to set.
     */
    public void setCurrentThresholdPrice(ArrayList<String> currentThresholdPrice){
        this.currentThresholdPrice = currentThresholdPrice;
    }
    /**
     * Sets the possible threshold prices for shops in the cart.
     *
     * @param possibleThresholdPrice The list of possible threshold prices to set.
     */
    public void setPossibleThresholdPrice(ArrayList<String> possibleThresholdPrice){
        this.possibleThresholdPrice = possibleThresholdPrice;
    }
    /**
     * Gets the possible sponsored brands for shops in the cart.
     *
     * @return The list of possible sponsored brands.
     */
    public ArrayList<String> getPossibleSponsoredBrand() {
        return possibleSponsoredBrand;
    }
    /**
     * Sets the possible sponsored brands for shops in the cart.
     *
     * @param possibleSponsoredBrand The list of possible sponsored brands to set.
     */
    public void setPossibleSponsoredBrand(ArrayList<String> possibleSponsoredBrand){
        this.possibleSponsoredBrand = possibleSponsoredBrand;
    }
    /*public void setActiveThresholdDiscount(ArrayList<Boolean> activeThresholdDiscount){
        this.activeThresholdDiscount = activeThresholdDiscount;
    }*/
    /**
     * Gets the list of active threshold discounts for shops in the cart.
     *
     * @return The list of active threshold discounts.
     */
    public ArrayList<Boolean> getActiveThresholdDiscount(){
        return activeThresholdDiscount;
    }

}
