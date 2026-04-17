package Business;

import Business.Products.Product;
import Business.Products.SpecificProduct;
import Business.Shops.Shop;
import Business.Shops.ShopLoyalty;
import Business.Shops.ShopSponsored;
import edu.salle.url.api.exception.ApiException;

import java.lang.reflect.Array;
import java.util.*;
/**
 * Represents the manager of the manager of the cart which helps with all its actions.
 *
 * This class manages a shopping cart and provides operations for adding, retrieving, and managing products.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public class CartManager {
        private Cart cart;
    private ArrayList<String> alreadySaidShop;
    private ArrayList<String> alreadyInThreshold;
    private ArrayList<Boolean> shopWithDiscount;
    //per cada shopName registro si tindra descompte, en cas de que tingui afegire true.

    /**
     * Constructs a new CartManager with an associated shopping cart.
     */
    public CartManager(){
        this.cart = new Cart();
        this.alreadySaidShop = new ArrayList<>();
        this.alreadyInThreshold = new ArrayList<>();
        //workingThreshold = false;
        this.shopWithDiscount = new ArrayList<>();
    }
    /**
     * Adds a product to the shopping cart.
     *
     * @param productName The name of the product.
     * @param productBrand The brand of the product.
     * @param productPrice The price of the product as a string.
     * @param shopName     The name of the shop where the product is added.
     * @param model        The model of the shop.
     */
    public void addProductToCart(String productName, String productBrand, String productPrice, String shopName, String model) {
        cart.setProductToShop(shopName, model, productName, productBrand, Float.parseFloat(productPrice));

    }
    /**
     * Gets the total number of products in the shopping cart.
     *
     * @return The size of the shopping cart.
     */
    public int getCartSize(){
        return cart.getCartSize();
    }
    /**
     * Gets the name of the product at the specified index in the shopping cart.
     *
     * @param comptadorCart The index of the product in the shopping cart.
     * @return The name of the product.
     */
    public String getCartProductName(int comptadorCart){
        return cart.getCartProductName(comptadorCart);
    }
    /**
     * Gets the brand of the product at the specified index in the shopping cart.
     *
     * @param comptadorCart The index of the product in the shopping cart.
     * @return The brand of the product.
     */
    public String getCartProductBrand(int comptadorCart){
        return cart.getCartProductBrand(comptadorCart);
    }
    /**
     * Gets the price of the product at the specified index in the shopping cart.
     *
     * @param comptadorCart The index of the product in the shopping cart.
     * @return The price of the product.
     */
    public float getCartProductPrice(int comptadorCart){
        return cart.getCartProductPrice(comptadorCart);
    }
    /**
     * Removes all products from the shopping cart.
     */
    public void removeCartProducts() {
        cart.removeProducts();
    }
    /**
     * Gets the total income for each shop in the shopping cart.
     *
     * @return A list of total income for each shop in the same order as shop names.
     * @throws ApiException if an error occurs while retrieving shop information.
     */
    public ArrayList<Float> getMoneyForEachShop() throws ApiException {
        ArrayList<Float> incomeForShops = new ArrayList<>();
        //ArrayList<String> currentThreshold = cart.getCurrentThresholdPrice();
        Map<String, Float> shopIncomeMap = new HashMap<>();
        for (int compt = 0; compt < getCartSize(); compt++) {
            String shopName = cart.getShopName(compt);
            float price = cart.getCartProductPrice(compt);
            String productName = cart.getCartProductName(compt);
            float discountedPrice = adaptedDiscountsForCart(price, productName, shopName);
            //System.out.println("!!!!!" + discountedPrice);
            //currentThreshold.set(compt, Float.toString(discountedPrice));
            shopIncomeMap.put(shopName, shopIncomeMap.getOrDefault(shopName, 0.0f) + discountedPrice);
        }
        for (String shopName : getShopsNames()) {
            incomeForShops.add(shopIncomeMap.getOrDefault(shopName, 0.00f));
        }
        //cart.setCurrentThresholdPrice(currentThreshold);
        return incomeForShops;
    }
    /**
     * Calculates the discounted price for a product based on various factors such as product, shop, and active discounts.
     *
     * @param price       The original price of the product.
     * @param productName The name of the product.
     * @param shopName    The name of the shop.
     * @return The discounted price of the product.
     * @throws ApiException if an error occurs while retrieving product or shop information.
     */
    private float adaptedDiscountsForCart(float price, String productName, String shopName) throws ApiException {
        float discountedPrice = 0.0f;
        int appliedDiscount = 0;
        ProductManager productManager = new ProductManager();
        ShopManager shopManager = new ShopManager();
        ArrayList<Shop> shops = cart.getShops();
        ArrayList<String> productNames = productManager.getProductNames();
        ArrayList<String> productBrands = productManager.getProductBrand();
        ArrayList<Float> productDiscounts = productManager.getProductDiscounts();
        ArrayList<String> possibleThresholdPrice = cart.getPossibleThresholdPrice();
        if(!possibleThresholdPrice.isEmpty()){
            cart.setPossibleThresholdPrice(shopManager.getExtraShopInfo("thresholdPrice"));
        }
        ArrayList<String> possibleSponsoredBrand = shopManager.getPossibleSponsoredBrand();
        if(!possibleSponsoredBrand.isEmpty()){
            cart.setPossibleSponsoredBrand(shopManager.getExtraShopInfo("sponsoredBrand"));
        }

        for(int compt = 0; compt < shops.size(); compt++){
            if(shops.get(compt).getCatalogueNames().get(0).equals(productName) && shopName.equals(shops.get(compt).getName())){
                if(adaptProductDiscounts(productNames, productDiscounts, shops).get(compt).equals(0.04F) && price >= 100F){
                    adaptProductDiscounts(productNames, productDiscounts, shops).set(compt, 0F);
                }
                discountedPrice = price / (1 + adaptProductDiscounts(productNames, productDiscounts, shops).get(compt));
                //System.out.println(price +"/ 1 +"+ adaptProductDiscounts(productNames, productDiscounts, shops).get(compt) + "=" + discountedPrice);
                switch (shops.get(compt).getModel()) {
                    case "LOYALTY" -> {
                        //ArrayList<String> currentThresholdPrice = cart.getCurrentThresholdPrice();
                        //System.out.println(currentThresholdPrice);
                            if (Float.parseFloat(cart.getCurrentThresholdPrice().get(compt)) >= Float.parseFloat(cart.getPossibleThresholdPrice().get(compt))) {
                                //System.out.println("Entra aqui si es s'ha activat el sistema threshold");
                                //System.out.println(cart.getPossibleThresholdPrice());
                                //System.out.println(cart.getCurrentThresholdPrice());
                                //AQUI ME FALTA UNA CONDICION QUE ME HAGA LO MISMO QUE HECHO EN CONTROLLER, DE MANERA QUE SI ME AÑADE 1 DE UNA TIENDA YA ME AÑADA TODOS
                                if (activeThresholdDiscount(compt) && getAlreadySaidShop().contains(cart.getShops().get(compt).getName()) && alreadyInThreshold.contains(cart.getShops().get(compt).getName()) || shopWithDiscount.get(compt)) {
                                    //System.out.println("Aixo pasa quan ja es deu aplicar el descompte final en tot moment en la part de threshold");
                                    //System.out.println(discountedPrice / (1 + adaptProductDiscounts(productNames, productDiscounts, shops).get(compt)));
                                    return discountedPrice / (1 + adaptProductDiscounts(productNames, productDiscounts, shops).get(compt));
                                } else {

                                   // System.out.println("Aixo pasa quan s'ha activat el threshold pero no es deu activar encara.");
                                    return discountedPrice;
                                }
                            } else {
                                //System.out.println("Aqui el threshold no s'activa encara.");
                                return discountedPrice;
                            }
                    }
                    case "SPONSORED" -> {
                        //System.out.println("m'entra a case SPONSORED");
                        for (int comptBrands = 0; comptBrands < productBrands.size(); comptBrands++) {
                            possibleSponsoredBrand.add(null);
                        }
                        if (productBrands.get(compt).equals(possibleSponsoredBrand.get(compt))) {
                            return price;
                        }
                        //System.out.println("entra aqui 4");
                    }
                    default -> {
                        //System.out.println("entra directament a cas maxBenefit");
                        return discountedPrice;
                    }
                }

            }

        }
        //System.out.println(activeThresholdDiscount);
        //cart.setActiveThresholdDiscount(activeThresholdDiscount);
        return discountedPrice; // Reemplazar con lógica real
    }
    /**
     * Checks if the threshold discount is active for a shop.
     *
     * @param comptShops The index of the shop in the cart.
     * @return true if the threshold discount is active, false otherwise.
     */
    private boolean activeThresholdDiscount(int comptShops) {
        //ArrayList<Shop> shops = cart.getShops();
        ArrayList<String> currentThresholdPrice = cart.getCurrentThresholdPrice();
        ArrayList<String> possibleThresholdPrice = cart.getPossibleThresholdPrice();
        return Float.parseFloat(currentThresholdPrice.get(comptShops)) >= Float.parseFloat(possibleThresholdPrice.get(comptShops));
    }
    /**
     * Adapts product discounts based on shop and product information.
     *
     * @param productNames    The list of product names.
     * @param productDiscounts The list of product discounts.
     * @param shops            The list of shops.
     * @return A list of adapted product discounts.
     */
    private ArrayList<Float> adaptProductDiscounts(ArrayList<String> productNames, ArrayList<Float> productDiscounts, ArrayList<Shop> shops) {
        ArrayList<Float> adaptedProductsDiscounts = new ArrayList<>();
        for(int compt = 0; compt < productNames.size(); compt++){
            for(int comptShops = 0; comptShops < shops.size(); comptShops++){
                if(productNames.get(compt).equals(shops.get(comptShops).getCatalogueNames().get(0))){
                    //System.out.println(productDiscounts.get(compt));
                    adaptedProductsDiscounts.add(productDiscounts.get(compt));
                }
            }
        }
        return adaptedProductsDiscounts;
    }
    /**
     * Initializes arrays and information needed for cart management.
     *
     * @throws ApiException if an error occurs while retrieving shop information.
     */
    public void initializeCartArrays() throws ApiException {
        ShopManager shopManager = new ShopManager();
        ArrayList<Float> shopIncomes = shopManager.getIncomesForShops(shopManager.getShopInfo("names"));
        ArrayList<String> shopModels = shopManager.getShopInfo("businessModel");
        ArrayList<Shop> shops = shopManager.getShopInfo();
        //ShopManager shopManager = new ShopManager();
        //ArrayList<Shop> shopsAdapted = new ArrayList<>();
        ArrayList<String> sponsoredBrands = new ArrayList<>();
        ArrayList<String> thresholdPrices = new ArrayList<>();
        ArrayList<String> thresholdActualPrices = new ArrayList<>();
        //ArrayList<String> shopModels = shopManager.getShopInfo("businessModel");
        /*for(int compt = 0; compt < shops.size(); compt++){
            shopsAdapted.add(shopManager.adaptShop(shops.get(compt)));
        }*/
        for(int comptShops = 0; comptShops < shopIncomes.size(); comptShops++){
            if(shopModels.get(comptShops).equals("SPONSORED")){
                ShopSponsored shopAux = (ShopSponsored) shops.get(comptShops);
                sponsoredBrands.add(shopAux.getSponsoredBrand());
                thresholdPrices.add("0.0");

            } else if(shopModels.get(comptShops).equals("LOYALTY")){
                ShopLoyalty shopAux2 = (ShopLoyalty) shops.get(comptShops);
                sponsoredBrands.add(null);
                thresholdPrices.add(Float.toString(shopAux2.getLoyaltyThreshold()));
            } else{
                sponsoredBrands.add(null);
                thresholdPrices.add("0.0");
            }
            Float income = shops.get(comptShops).getIncome();
            thresholdActualPrices.add(income != null ? Float.toString(income) : "0.0");
            shopWithDiscount.add(false);
        }
        cart.setPossibleSponsoredBrand(sponsoredBrands);
        cart.setPossibleThresholdPrice(thresholdPrices);
        cart.setCurrentThresholdPrice(thresholdActualPrices);
        //System.out.println("Possible Sponsored Brands: " + cart.getPossibleSponsoredBrand());
        //System.out.println("Possible Threshold Prices: " + cart.getPossibleThresholdPrice());
        //System.out.println("Current Threshold Prices: " + cart.getCurrentThresholdPrice());
    }

    /**
     * Gets the names of all unique shops in the shopping cart.
     *
     * @return A list of shop names.
     */
    private ArrayList<String> getAllShopsInCartNames(){
        ArrayList<String> shopNames = new ArrayList<>();
        for(int compt = 0; compt < getCartSize(); compt++){
            shopNames.add(cart.getShopName(compt));
        }
        return shopNames;
    }
    /**
     * Gets the names of all unique shops in the shopping cart.
     *
     * @return A list of shop names without duplicates.
     */
    public ArrayList<String> getShopsNames(){
        ArrayList<String> shopNames = getAllShopsInCartNames();
        shopNames = removeDuplicates(shopNames);
        return shopNames;
    }
    /**
     * Removes duplicates from a list of strings.
     *
     * @param list The list containing potential duplicates.
     * @return A new list with duplicates removed.
     */
    private ArrayList<String> removeDuplicates(ArrayList<String> list) {
        Set<String> set = new HashSet<>(list);
        return new ArrayList<>(set);
    }

    /**
     * Removes a product from the cart based on the product and shop name.
     * If shopName is null, it removes the product from all shops in the cart.
     *
     * @param productName The name of the product to remove.
     * @param shopName    The name of the shop where the product is located, null for all shops.
     */
    public void removeProductFromCart(String productName, String shopName) {
        ArrayList<Shop> shops = cart.getShops();
        if (shops != null && !shops.isEmpty()) {
            if (shopName == null) {
                for (int compt = 0; compt < shops.size(); compt++) {
                    ArrayList<SpecificProduct> catalogue = shops.get(compt).getCatalogue();
                    if (catalogue != null && !catalogue.isEmpty()) {
                        for (int comptProducts = 0; comptProducts < catalogue.size(); comptProducts++) {
                            if (catalogue.get(comptProducts).getName().equals(productName)) {
                                catalogue.remove(comptProducts);
                                if (catalogue.isEmpty()) {
                                    shops.remove(compt);
                                }
                            }
                        }
                    }
                }
            } else {
                for (int compt = 0; compt < shops.size(); compt++) {
                    if (shops.get(compt).getName().equals(shopName)) {
                        ArrayList<SpecificProduct> catalogue = shops.get(compt).getCatalogue();

                        if (catalogue != null && !catalogue.isEmpty()) {
                            for (int comptProducts = 0; comptProducts < catalogue.size(); comptProducts++) {
                                if (catalogue.get(comptProducts).getName().equals(productName)) {
                                    catalogue.remove(comptProducts);
                                    if (catalogue.isEmpty()) {
                                        shops.remove(compt);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            this.cart = new Cart();
            cart.setShops(shops);
        }
    }
    /**
     * Checks if the threshold discount is required for a specific shop and activates it if necessary.
     *
     * @param shopName The name of the shop to check.
     * @return true if the threshold discount is required and activated, false otherwise.
     * @throws ApiException if an error occurs while retrieving shop information.
     */
    public boolean getThresholdPriceIfNeed(String shopName) throws ApiException {
        ArrayList<Shop> shops = cart.getShops();
        //System.out.println(shops.size());
        ArrayList<Boolean> thresholdActivated = cart.getActiveThresholdDiscount();
        //System.out.println(thresholdActivated);
        for(int compt = 0; compt < shops.size(); compt++){
            if(shops.get(compt).getName().equals(shopName)){
                return activeThreshold(compt, shopInThreshold(compt));
            }
        }
        return false;
    }
    /**
     * Gets the list of shops where the threshold discount is activated.
     *
     * @return The list of shops where the threshold discount is activated.
     */
    public ArrayList<String> getAlreadySaidShop() {
        return alreadySaidShop;
    }
    /**
     * Adds a shop to the list of shops where the threshold discount is activated.
     *
     * @param shopName The name of the shop to add.
     */
    public void addAlreadySaidShop(String shopName){
        alreadySaidShop.add(shopName);

    }
    /**
     * Gets the discounted price of a product in the cart at a specific index.
     *
     * @param compt The index of the product in the cart.
     * @return The discounted price of the product.
     * @throws ApiException if an error occurs while retrieving product or shop information.
     */
    public float getCartProductDiscountedPrice(int compt) throws ApiException {
        ProductManager productManager = new ProductManager();
        ArrayList<String> productNames = productManager.getProductNames();
        ArrayList<Float> productDiscounts = productManager.getProductDiscounts();
        ArrayList<Shop> shops = cart.getShops();
        float productPrice = shops.get(compt).getCatalogue().get(0).getPrice();
        float discountAdded = adaptProductDiscounts(productNames, productDiscounts, shops).get(compt);
        if(alreadyInThreshold.contains(shops.get(compt).getName())){
            return (productPrice / (1 + discountAdded));
        }else{
            return (productPrice / (1 + discountAdded));
        }
    }
    /**
     * Checks if the loyalty threshold discount is active for a specific shop and purchase.
     *
     * @param compt         The index of the shop in the cart.
     * @param comptSameBuy  A boolean indicating if it's the same purchase for loyalty discount.
     * @return true if the loyalty threshold discount is active, false otherwise.
     * @throws ApiException if an error occurs while retrieving shop information.
     */
    public boolean activeThreshold(int compt, boolean comptSameBuy) throws ApiException {
        ArrayList<Shop> shops = cart.getShops();
        if(Float.parseFloat(cart.getPossibleThresholdPrice().get(compt)) <= Float.parseFloat(cart.getCurrentThresholdPrice().get(compt))){
            alreadyInThreshold.add(shops.get(compt).getName());
        } else{
            alreadyInThreshold.add("not Added");
        }
        return alreadyInThreshold.contains(shops.get(compt).getName()) && shops.get(compt).getModel().equals("LOYALTY") && comptSameBuy;
    }
    /**
     * Checks if a specific shop is in the threshold.
     *
     * @param compt The index of the shop in the cart.
     * @return true if the shop is in the threshold, false otherwise.
     */
    public boolean shopInThreshold(int compt) {
        ArrayList<Shop> shops = cart.getShops();
        return alreadyInThreshold.contains(shops.get(compt).getName());
    }
    /**
     * Gets the product discount for a specific shop and product name.
     *
     * @param compt The index of the shop in the cart.
     * @return The discount applied to the product.
     * @throws ApiException if an error occurs while retrieving product information.
     */
    public float getDiscounts(int compt) throws ApiException {
        // Obtiene la información necesaria para calcular el descuento
        ProductManager productManager = new ProductManager();
        ArrayList<String> productNames = productManager.getProductNames();
        ArrayList<Float> productDiscounts = productManager.getProductDiscounts();
        ArrayList<Shop> shops = cart.getShops();

        // Verifica si 'compt' está dentro de los límites
        /*if (compt < 0 || compt >= shops.size()) {
            throw new ApiException("Índice de tienda inválido");
        }*/

        // Obtiene la información específica de la tienda en la posición 'compt'
        String productName = shops.get(compt).getCatalogueNames().get(0); // Supongo que el nombre del producto está en la posición 0

        // Aquí puedes aplicar lógica adicional según tus requisitos
        // ...

        // Devuelve el descuento calculado
        return getProductDiscount(productNames, productDiscounts, productName);
    }
    /**
     * Gets the discount applied to a specific product based on its name.
     *
     * @param productNames    The list of product names.
     * @param productDiscounts The list of corresponding product discounts.
     * @param productName     The name of the product to retrieve the discount for.
     * @return The discount applied to the product.
     */
    private float getProductDiscount(ArrayList<String> productNames, ArrayList<Float> productDiscounts, String productName) {
        // Encuentra el descuento correspondiente al nombre del producto
        for (int i = 0; i < productNames.size(); i++) {
            if (productNames.get(i).equals(productName)) {
                return productDiscounts.get(i);
            }
        }
        return 0.0f; // Si no se encuentra el producto, se devuelve un descuento predeterminado de 0
    }
    /**
     * Sets the discount flag for a specific shop.
     *
     * @param comptShops The index of the shop in the cart.
     */
    public void setDiscountToShop(int comptShops) {
        for(int compt = 0; compt < getCartSize(); compt++){
            if(comptShops == compt){
                shopWithDiscount.set(compt, true);
            }
        }
    }
    /**
     * Gets the discount flag for a specific shop.
     *
     * @param comptShops The index of the shop in the cart.
     * @return true if the shop has a discount, false otherwise.
     */
    public boolean getDiscountFromShop(int comptShops) {
        for(int compt = 0; compt < getCartSize(); compt++){
            return shopWithDiscount.get(compt);
        }
        return false;
    }

}
