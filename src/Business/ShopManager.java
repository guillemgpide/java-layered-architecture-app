package Business;

import Business.Products.SpecificProduct;
import Business.Shops.Shop;
import Business.Shops.ShopLoyalty;
import Business.Shops.ShopMaxBenefit;
import Business.Shops.ShopSponsored;
import Exceptions.CreateDirectoryException;
import Persistance.*;
import edu.salle.url.api.exception.ApiException;

import java.util.ArrayList;

/**
 * Manages shops, providing functionalities related to shop data and operations.
 * This class includes methods for adding, removing, and retrieving information about shops and products.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public class ShopManager {
    private ShopDAO shopDAO;
    //private final ShopDAO jsonDAO;
    /**
     * Constructs a ShopManager and initializes the ShopJsonDAO.
     */
    public ShopManager() {
       shopDAO = new ShopApiDAO();
       if(!shopDAO.workingShopsDAO()){
           shopDAO = new ShopJsonDAO();
       }
    }
    /**
     * Checks if the ShopDAO is working.
     *
     * @return True if the ShopDAO is operational, false otherwise.
     * @throws ApiException If an exception occurs during the API call.
     */
    public boolean workingShopDAO() throws ApiException {
        return shopDAO.workingShopsDAO();
    }
    /**
     * Checks if the provided shop name is correct by verifying its uniqueness among existing shops.
     *
     * @param namePossible The shop name to be checked.
     * @return True if the shop name is correct (unique), false otherwise.
     * @throws ApiException If an exception occurs during the API call.
     */
    public boolean correctShopName(String namePossible) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        for (Shop shop : shops) {
            if (shop.getName().equals(namePossible)) {
                return false;
            }
        }
        return true;
    }
    /**
     * Checks if the provided attribute for a shop's founding year is correct.
     *
     * @param attributePossible The attribute representing the founding year to be checked.
     * @return True if the founding year attribute is correct (a non-negative integer), false otherwise.
     */
    public boolean correctShopYear(String attributePossible) {// aqui potser he de fer throw
        int number;
        try {
            number = Integer.parseInt(attributePossible);
        } catch (NumberFormatException e) {
            return false;
        }
        return !(number < 0);
    }
    /**
     * Adds a new shop to the system based on the provided attributes.
     *
     * @param shopAttributes      A list of attributes for the new shop, including name, description, founding year, and business model.
     * @param model               The business model to which the shop belongs.
     * @param extraShopAttribute Additional attribute specific to certain business models.
     * @throws ApiException If an exception occurs during the API call.
     */
    public void addShop(ArrayList<String> shopAttributes, String model, String extraShopAttribute) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        //System.out.println(shops);
        Shop newShop = createShopFromAttributes(shopAttributes, model, "0.0", new ArrayList<>(), extraShopAttribute);
        shops.add(newShop);
        shopDAO.writeShops(shops);
    }
    /**
     * Creates a new shop instance based on the provided attributes, model, and extra shop attribute.
     *
     * @param shopAttributes     A list of attributes for the new shop, including name, description, founding year, and business model.
     * @param model              The business model to which the shop belongs.
     * @param earnings           The earnings of the shop.
     * @param catalogue          The list of specific products in the shop's catalogue.
     * @param extraShopAttribute The extra attribute specific to the business model of the shop.
     * @return A new Shop instance created from the given attributes.
     */
    private Shop createShopFromAttributes(ArrayList<String> shopAttributes, String model, String earnings, ArrayList<SpecificProduct> catalogue, String extraShopAttribute) {
        // Asegúrate de que tienes los atributos necesarios en la lista shopAttributes
        String name = shopAttributes.get(0);
        String description = shopAttributes.get(1);
        int since = Integer.parseInt(shopAttributes.get(2));
        // Asumiendo que el modelo de negocio se obtiene del parámetro model

        return adaptShop(name, description, Float.parseFloat(earnings), since, model, catalogue, extraShopAttribute);
    }
    /**
     * Adapts a generic Shop instance to a specialized business model.
     *
     * @param name              The name of the shop.
     * @param description       The description of the shop.
     * @param earnings          The earnings of the shop.
     * @param since             The founding year of the shop.
     * @param businessModel     The business model to which the shop belongs.
     * @param catalogue         The list of specific products in the shop's catalogue.
     * @param extraShopAttribute The extra attribute specific to the business model of the shop.
     * @return A specialized Shop instance based on the given business model.
     */
    private Shop adaptShop(String name, String description, float earnings, int since, String businessModel, ArrayList<SpecificProduct> catalogue, String extraShopAttribute) {
        return switch (businessModel) {
            case "LOYALTY" -> new ShopLoyalty(name, description, since, earnings, businessModel, catalogue, Float.parseFloat(extraShopAttribute));
            case "MAXIMUM BENEFITS" -> new ShopMaxBenefit(name, description, since, earnings, businessModel, catalogue);
            case "SPONSORED" -> new ShopSponsored(name, description, since, earnings, businessModel, catalogue, extraShopAttribute);
            default -> null;
        };
    }
    /**
     * Checks if a product with the given name is already present in the list of products.
     *
     * @param productName  The name of the product to check.
     * @param productNames The list of existing product names.
     * @return True if the product with the given name is not present in the list, false otherwise.
     */
    public boolean insertAProduct(String productName, ArrayList<String> productNames) {
        return !productNames.contains(productName);
    }
    /**
     * Validates and checks if the given price for a product is greater than the existing maximum price.
     *
     * @param productName      The name of the product.
     * @param priceS           The string representation of the price to be validated.
     * @param productNames     The list of existing product names.
     * @param productMaxPrices The list of existing maximum prices for products.
     * @return True if the price is valid and greater than the existing maximum price, false otherwise.
     */
    public boolean insertAPriceForProduct(String productName, String priceS, ArrayList<String> productNames, ArrayList<Float> productMaxPrices) {
        int position = -1;
        boolean error = false;
        priceS = priceS.replaceAll("[^0-9.]", "");
        try{
            float price = Float.parseFloat(priceS);
            if(price >= 0){
                for(int compt = 0; compt < productNames.size(); compt++){
                    if(productNames.get(compt).equals(productName)){
                        position = compt;
                    }
                }
                if(position > 0){
                    return price > productMaxPrices.get(position);
                }
            }else{
                error = true;
            }
        }catch(NumberFormatException e){
            error =  true;
        }
        return error;
    }
    /**
     * Adds a new product to a shop if the conditions are met.
     *
     * @param shopName           The name of the shop where the product will be added.
     * @param productName        The name of the product to be added.
     * @param price              The price of the product.
     * @param productBrand       The brand of the product.
     * @param reviewsFromProduct The list of reviews for the product.
     * @throws ApiException If an exception occurs during the API call.
     * @return True if the product is not already in the shop or has the same price, false otherwise.
     */
    public boolean addProductToShop(String shopName, String productName, String price, String productBrand, ArrayList<String> reviewsFromProduct) throws ApiException {
        ArrayList<String> ratings = takeRatingFromString(reviewsFromProduct);
        ArrayList<String> reviews = takeReviewsFromString(reviewsFromProduct);
        if(samePriceForProduct(shopName, productName, price)){
            if(!alreadyInJsonDAO(shopName, productName)){
                setSpecificProductToShop(shopName, productName, price, productBrand, ratings, reviews);
            }
            return false;
        }else{
            return true;
        }
    }
    /**
     * Extracts reviews from a list of strings containing ratings and reviews.
     *
     * @param reviewsFromProduct The list of strings containing ratings and reviews.
     * @return The extracted list of reviews.
     */
    public ArrayList<String> takeReviewsFromString(ArrayList<String> reviewsFromProduct) {
        ArrayList<String> reviews = new ArrayList<>();
        for(int compt = 0; compt < reviewsFromProduct.size(); compt++){
            String[] palabras = reviewsFromProduct.get(compt).split("\\s+");
            for (int i = 0; i < palabras.length; i++) {
                if (palabras[i].contains("*") && i + 1 < palabras.length) {
                    StringBuilder subcadena = new StringBuilder();
                    for (int j = i + 1; j < palabras.length; j++) {
                        subcadena.append(palabras[j]).append(" ");
                    }
                    reviews.add(subcadena.toString().trim());
                }
            }
        }
        return reviews;
    }
    /**
     * Extracts ratings from a list of strings containing ratings and reviews.
     *
     * @param reviewsFromProduct The list of strings containing ratings and reviews.
     * @return The extracted list of ratings.
     */
    public ArrayList<String> takeRatingFromString(ArrayList<String> reviewsFromProduct) {
        ArrayList<String> rating = new ArrayList<>();
        for(int compt = 0; compt < reviewsFromProduct.size(); compt++){
            String[] palabras = reviewsFromProduct.get(compt).split("\\s+");

            for (String palabra : palabras) {
                if (palabra.contains("*")) {
                    int numero = Integer.parseInt(palabra.replace("*", ""));
                    rating.add("*".repeat(Math.max(0, numero)));
                }
            }
        }
        return rating;
    }
    /**
     * Adds a specific product to the catalogue of a shop. The specific product is created from the provided information,
     * including the product name, price, brand, ratings, and reviews. If the shop does not exist, an ApiException is thrown.
     *
     * @param shopName   The name of the shop to which the specific product will be added.
     * @param productName   The name of the specific product.
     * @param price     The price of the specific product.
     * @param productBrand  The brand of the specific product.
     * @param ratings   The list of ratings for the specific product.
     * @param reviews   The list of reviews for the specific product.
     * @throws ApiException If the shop is not found.
     */
    public void setSpecificProductToShop(String shopName, String productName, String price, String productBrand, ArrayList<String> ratings, ArrayList<String> reviews) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        ArrayList<Shop> shopsAdapted = new ArrayList<>();

        for(int compt = 0; compt < shops.size(); compt++){
            if(shops.get(compt).getName().equals(shopName)){
                shops.get(compt).getCatalogue().add(new SpecificProduct(productName, Float.parseFloat(price), productBrand, ratings, reviews));
            }
            //shopsAdapted.set(compt, createShopFromAttributes(shopAttributes, model, earnings, shops.get(compt).getCatalogue()));
            shopsAdapted.add(adaptShop(shops.get(compt)));
        }
        shopDAO.writeShops(shopsAdapted);
    }
    /**
     * Adapts a generic Shop object to its specific subclass based on the business model.
     *
     * @param shop The generic Shop object to adapt.
     * @return A specific Shop subclass object based on the business model.
     */
    public Shop adaptShop(Shop shop){
        return switch (shop.getModel()) {
            case "LOYALTY" -> new ShopLoyalty(shop.getName(), shop.getDescription(), shop.getYear(), shop.getIncome(), shop.getModel(), shop.getCatalogue(), ((ShopLoyalty) shop).getLoyaltyThreshold());
            case "MAXIMUM BENEFITS" -> new ShopMaxBenefit(shop.getName(), shop.getDescription(), shop.getYear(), shop.getIncome(), shop.getModel(), shop.getCatalogue());
            case "SPONSORED" -> new ShopSponsored(shop.getName(), shop.getDescription(), shop.getYear(), shop.getIncome(), shop.getModel(), shop.getCatalogue(), ((ShopSponsored) shop).getSponsoredBrand());
            default -> null;
        };
    }
    /**
     * Checks if a product with the given name is already present in the shop's catalogue.
     *
     * @param shopName     The name of the shop.
     * @param productName  The name of the product.
     * @return True if the product is already in the shop's catalogue, false otherwise.
     * @throws ApiException If an error occurs during API interaction.
     */
    public boolean alreadyInJsonDAO(String shopName, String productName) throws ApiException {
        boolean alreadyInPrice = false;
        ArrayList<Shop> shops = shopDAO.readShops();;
        for (int compt = 0; compt < shops.size(); compt++) {
            if (shops.get(compt).getName().equals(shopName)) {
                if(getProductInfoFromShop(shopName, "names").contains(productName)){
                    alreadyInPrice = true;
                    break;
                }
            }
        }
        return  alreadyInPrice;
    }
    /**
     * Checks if a product with the same name and price is already present in the shop's catalogue.
     *
     * @param shopName     The name of the shop.
     * @param productName  The name of the product.
     * @param price        The price of the product.
     * @return True if the product with the same name and price is already in the shop's catalogue, false otherwise.
     * @throws ApiException If an error occurs during API interaction.
     */
    public boolean samePriceForProduct(String shopName, String productName, String price) throws ApiException {
        boolean incorrectPrice = true;
        ArrayList<Shop> shops = shopDAO.readShops();

        ArrayList<String> productNames;
        ArrayList<String> productPrices;
        for(int compt = 0; compt < shops.size(); compt++){
            if(shops.get(compt).getName().equals(shopName)){
                //System.out.println("Revisem les tendes per comprobar preus.");
                productNames = getProductInfoFromShop(shops.get(compt).getName(), "names");
                productPrices = getProductInfoFromShop(shops.get(compt).getName(), "prices");
                for(int comptProductes = 0; comptProductes < shops.get(compt).getCatalogue().size(); comptProductes++){
                    if ((productName.equals(productNames.get(comptProductes)))) {
                        //System.out.println("The price inserted for " + productName + " is " + Float.parseFloat(price) + " and the price that must be the same is " + Float.parseFloat(productPrices.get(comptProductes)));
                        incorrectPrice = (Float.parseFloat(productPrices.get(comptProductes)) == Float.parseFloat(price));
                    }
                }
            }
        }
        return incorrectPrice;
    }
    /*public boolean alreadyInShopButDiferentPrice(String shopName, String productName, String price) throws ApiException {
        if(!samePriceForProduct(shopName, productName, price)){

        }
    }*/
    /**
     * Retrieves product information from a shop's catalogue based on the specified parameter.
     *
     * @param shopName       The name of the shop.
     * @param infoParameter  The parameter for which the product information is requested (names, brands, or prices).
     * @return A list containing the requested product information.
     * @throws ApiException If an error occurs during API interaction.
     */
    public ArrayList<String> getProductInfoFromShop(String shopName, String infoParameter) throws ApiException {
        ArrayList<String> productInfo = new ArrayList<>();
        ArrayList<Shop> shops = shopDAO.readShops();
        switch (infoParameter) {
            case "names" -> {
                for (int compt = 0; compt < shops.size(); compt++) {
                    if (shops.get(compt).getName().equals(shopName)) {
                        for (int comptProducts = 0; comptProducts < shops.get(compt).getCatalogue().size(); comptProducts++) {
                            productInfo.add(shops.get(compt).getCatalogue().get(comptProducts).getName());
                        }
                    }
                }
            }
            case "brands" -> {
                for (int compt = 0; compt < shops.size(); compt++) {
                    if (shops.get(compt).getName().equals(shopName)) {
                        for (int comptProducts = 0; comptProducts < shops.get(compt).getCatalogue().size(); comptProducts++) {
                            productInfo.add(shops.get(compt).getCatalogue().get(comptProducts).getBrand());
                        }
                    }
                }
            }
            case "prices" -> {
                for (int compt = 0; compt < shops.size(); compt++) {
                    if (shops.get(compt).getName().equals(shopName)) {
                        for (int comptProducts = 0; comptProducts < shops.get(compt).getCatalogue().size(); comptProducts++) {
                            productInfo.add(Float.toString(shops.get(compt).getCatalogue().get(comptProducts).getPrice()));
                        }
                    }
                }
            }
        }

        return productInfo;
    }
    /**
     * Removes a product from the catalogue of all shops.
     *
     * @param productName The name of the product to be removed.
     * @throws ApiException If an error occurs during API interaction.
     */
    public void removeProductFromAllShops(String productName) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        ArrayList<Shop> updatedShops = new ArrayList<>();
        for (Shop shop : shops) {
            // Elimina el producto si coincide con el nombre
            shop.getCatalogue().removeIf(product -> product.getName().equals(productName));
            // Asegúrate de tener los atributos correctos para la creación de la tienda
            String name = shop.getName();
            String description = shop.getDescription();
            String model = shop.getModel();
            String extraShopAttribute = null;
            switch (model){
                case "MAXIMUM BENEFIT":
                    extraShopAttribute = "null";
                    break;
                case "LOYALTY":
                    extraShopAttribute = Float.toString(((ShopLoyalty)shop).getLoyaltyThreshold());
                    break;
                case "SPONSORED":
                    extraShopAttribute = ((ShopSponsored)shop).getSponsoredBrand();
                    break;
            }
            String earnings = String.valueOf(shop.getIncome());
            int since = shop.getYear();
            // Utiliza la función createShopFromAttributes para crear la tienda actualizada
            ArrayList<String> shopAttributes = new ArrayList<>();
            shopAttributes.add(name);
            shopAttributes.add(description);
            shopAttributes.add(String.valueOf(since));
            Shop updatedShop = createShopFromAttributes(shopAttributes, model, earnings, shop.getCatalogue(), extraShopAttribute);
            updatedShops.add(updatedShop);
        }
        // Escribe las tiendas actualizadas
        shopDAO.writeShops(updatedShops);
    }

    /**
     * Removes a product from the catalogue of a specific shop.
     *
     * @param productName The name of the product to be removed.
     * @param shopName The name of the shop from which the product should be removed.
     * @throws ApiException If an error occurs during API interaction.
     */
    public void removeProductFromShop(String productName, String shopName) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        ArrayList<Shop> shopsAdapted = new ArrayList<>();
        for(int compt = 0; compt < shops.size(); compt++){
            if(shops.get(compt).getName().equals(shopName)){
                for(int comptProducts = 0; comptProducts < shops.get(compt).getCatalogue().size(); comptProducts++){
                    if(shops.get(compt).getCatalogue().get(comptProducts).getName().equals(productName)){
                        shops.get(compt).getCatalogue().remove(comptProducts);
                    }
                }
            }
            shopsAdapted.add(adaptShop(shops.get(compt)));
        }
        shopDAO.writeShops(shopsAdapted);
    }
    /**
     * Retrieves the names of shops where a specific product is available.
     *
     * @param productName The name of the product.
     * @return An ArrayList containing the names of shops where the product is available.
     * @throws ApiException If an error occurs during API interaction.
     */
    public ArrayList<String> getShopNamesForProduct(String productName) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        ArrayList<String> shopNames = new ArrayList<>();
        for(int compt = 0; compt < shops.size(); compt++){
            for(int comptProducts = 0; comptProducts < shops.get(compt).getCatalogue().size();  comptProducts++){
                if(shops.get(compt).getCatalogue().get(comptProducts).getName().equals(productName)){
                    shopNames.add(shops.get(compt).getName());
                }
            }
        }
        return shopNames;
    }
    /**
     * Retrieves the prices of a specific product across different shops.
     *
     * @param productName The name of the product.
     * @return An ArrayList containing the prices of the product in different shops.
     * @throws ApiException If an error occurs during API interaction.
     */
    public ArrayList<Float> getShopPricesForProduct(String productName) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        ArrayList<Float> shopPrices = new ArrayList<>();
        for(int compt = 0; compt < shops.size(); compt++){
            for(int comptCatalogue = 0; comptCatalogue < shops.get(compt).getCatalogue().size(); comptCatalogue++){
                if(shops.get(compt).getCatalogue().get(comptCatalogue).getName().equals(productName)){
                    if(shops.get(compt).getCatalogue().get(comptCatalogue).getName().equals(productName)){
                        shopPrices.add(shops.get(compt).getCatalogue().get(comptCatalogue).getPrice());
                    }
                }
            }
        }
        return shopPrices;
    }
    /**
     * Adds a review and rating to a specific product in a shop's catalogue.
     *
     * @param productSelected The name of the product to which the review and rating are added.
     * @param rating The rating to be added to the product.
     * @param review The review to be added to the product.
     * @throws ApiException If an error occurs during API interaction.
     */
    public void addReviewToProductInShop(String productSelected, String rating, String review) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        ArrayList<Shop> shopsAdapted = new ArrayList<>();
        for(int compt = 0; compt < shops.size(); compt++){
            for(int comptCatalogue = 0; comptCatalogue < shops.get(compt).getCatalogue().size(); comptCatalogue++){
                if(shops.get(compt).getCatalogue().get(comptCatalogue).getName().equals(productSelected)){
                    shops.get(compt).getCatalogue().get(comptCatalogue).addProductsReviews(review);
                    shops.get(compt).getCatalogue().get(comptCatalogue).addRatings(rating);
                }
            }
            shopsAdapted.add(adaptShop(shops.get(compt)));        }
        shopDAO.writeShops(shops);
    }
    /**
     * Retrieves information about shops based on the specified parameter.
     *
     * @param infoParameter The parameter to determine the type of information to retrieve ("names", "years", or "descriptions").
     * @return An ArrayList containing the requested shop information.
     * @throws ApiException If an error occurs during API interaction.
     */
    public ArrayList<String> getShopInfo(String infoParameter) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        ArrayList<String> shopInfo = new ArrayList<>();
        switch (infoParameter) {
            case "names" -> {
                for (int compt = 0; compt < shops.size(); compt++) {
                    shopInfo.add(shops.get(compt).getName());
                }
            }
            case "years" -> {
                for (int compt = 0; compt < shops.size(); compt++) {
                    shopInfo.add(Integer.toString(shops.get(compt).getYear()));
                }
            }
            case "descriptions" -> {
                for (int compt = 0; compt < shops.size(); compt++) {
                    shopInfo.add(shops.get(compt).getDescription());
                }
            }
            case "businessModel" -> {
                for (int compt = 0; compt < shops.size(); compt++) {
                    shopInfo.add(shops.get(compt).getModel());
                }
            }
        }
        return shopInfo;
    }
    /**
     * Checks if a product exists in any shop's catalogue.
     *
     * @param productSelected The name of the product to check.
     * @return True if the product exists in any catalogue, false otherwise.
     * @throws ApiException If an error occurs during API interaction.
     */
    public boolean productInAnyCatalogue(String productSelected) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        boolean correctProduct = false;
        for(int compt = 0; compt < shops.size(); compt++){
            for(int comptCatalogue = 0; comptCatalogue < shops.get(compt).getCatalogue().size(); comptCatalogue++){
                correctProduct = shops.get(compt).getCatalogue().get(comptCatalogue).getName().equals(productSelected);
            }
        }
        return correctProduct;
    }
    /**
     * Checks if a shop has any products in its catalogue.
     *
     * @param shopName The name of the shop to check.
     * @return True if the shop has products, false otherwise.
     * @throws ApiException If an error occurs during API interaction.
     */
    public boolean shopHasProducts(String shopName) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        for (Shop shop : shops) {
            if (shop.getName().equals(shopName)) {
                return !shop.getCatalogue().isEmpty();
            }
        }
        return false;
    }
    /**
     * Adds additional income to the specified shops.
     *
     * @param shopNames      The names of the shops to which additional income will be added.
     * @param moneyForShops  The additional income corresponding to each shop.
     * @throws ApiException If an error occurs during API interaction.
     */
    public void addMoreIncomeToShops(ArrayList<String> shopNames, ArrayList<Float> moneyForShops) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        ArrayList<Shop> shopsAdapted = new ArrayList<>();
        for (int i = 0; i < shopNames.size(); i++) {
            String shopName = shopNames.get(i);
            float additionalIncome = moneyForShops.get(i);
            for (Shop shop : shops) {
                if (shop.getName().equals(shopName)) {
                    float currentIncome = shop.getIncome();
                    shop.setIncome(currentIncome + additionalIncome);
                    break;
                }
            }
            shopsAdapted.add(adaptShop(shops.get(i)));        }
        shopDAO.writeShops(shops);
    }
    /**
     * Gets the incomes for the specified shops.
     *
     * @param shopNames The names of the shops for which incomes will be retrieved.
     * @return An ArrayList containing the incomes corresponding to each shop.
     * @throws ApiException If an error occurs during API interaction.
     */
    public ArrayList<Float> getIncomesForShops(ArrayList<String> shopNames) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        ArrayList<Float> incomesForShops = new ArrayList<>();
        for (String shopName : shopNames) {
            float income = 0.0f;
            for (Shop shop : shops) {
                if (shop.getName().equals(shopName)) {
                    income = shop.getIncome();
                    break;
                }
            }
            incomesForShops.add(income);
        }
        return incomesForShops;
    }
    /**
     * Checks if there are any shops in the system.
     *
     * @return True if there are shops, false otherwise.
     * @throws ApiException If an error occurs during API interaction.
     */
    public boolean thereAreShops() throws ApiException {
        return !shopDAO.isShopInfoEmpty();
    }
    /**
     * Informs the shop DAO to create its data source.
     *
     * @return True if the shop DAO is successfully informed, false otherwise.
     */
    public boolean informShopDao() {
        try{
            shopDAO.informDaoToCreate();
            return true;
        } catch( CreateDirectoryException e){
            return false;
        }
    }
    /**
     * Updates information from shops based on the current DAO type.
     *
     * @throws ApiException If an error occurs during API interaction.
     */
    public void updateInfoFromShops() throws ApiException {
        ArrayList<Shop> shops;
        if(shopDAO.workingShopsDAO() && getCurrentDAOType().equals("API")){
            shops = shopDAO.readShops();
            shopDAO = new ShopJsonDAO();
            shopDAO.writeShops(shops);
            shopDAO = new ShopApiDAO();
            workingShopDAO();
        }else if (shopDAO.workingShopsDAO() && getCurrentDAOType().equals("JSON")){
            shops = shopDAO.readShops();
            try{
                shopDAO = new ShopApiDAO();
                workingShopDAO();
                shopDAO.writeShops(shops);

            } catch(NullPointerException e){
            }
            shopDAO = new ShopJsonDAO();

        }
    }
    /**
     * Gets the current type of the shop DAO.
     *
     * @return A String representing the current DAO type ("API", "JSON", or "Unknown").
     */
    public String getCurrentDAOType() {
        if (shopDAO instanceof ShopApiDAO) {
            return "API";
        } else if (shopDAO instanceof ShopJsonDAO) {
            return "JSON";
        } else {
            return "Unknown";
        }
    }
    /**
     * Gets the shop information.
     *
     * @return An ArrayList of Shop objects containing the shop information.
     * @throws ApiException If an error occurs during API interaction.
     */
    public ArrayList<Shop> getShopInfo() throws ApiException {
        return shopDAO.readShops();
    }
    /**
     * Gets additional information for shops based on the specified kind of extra attribute.
     *
     * @param kindOfExtraAttribute The kind of extra attribute for which information is requested.
     * @return An ArrayList containing the requested additional information for each shop.
     * @throws ApiException If an error occurs during API interaction.
     */
    public ArrayList<String> getExtraShopInfo(String kindOfExtraAttribute) throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        ArrayList<String> extraInfoForShop = new ArrayList<>();
        Shop shopAux;
        switch(kindOfExtraAttribute){
            case "thresholdPrice":
                for(int compt = 0; compt < shops.size(); compt++){
                    if(shops.get(compt).getModel().equals("LOYALTY")){
                        shopAux = adaptShop(shops.get(compt));
                        extraInfoForShop.add(Float.toString(((ShopLoyalty)shopAux).getLoyaltyThreshold()));
                    }else{
                        extraInfoForShop.add("0.0");
                    }
                }
                break;
            case "sponsoredBrand":
                for(int compt = 0; compt < shops.size(); compt++){
                    if(shops.get(compt).getModel().equals("SPONSORED")){
                        shopAux = adaptShop(shops.get(compt));
                        extraInfoForShop.add(((ShopSponsored)shopAux).getSponsoredBrand());
                    }else{
                        extraInfoForShop.add("NO SPONSORED BRAND HERE");
                    }
                }
                break;
            default:
                break;
        }
        return extraInfoForShop;
    }
    /**
     * Gets the possible sponsored brands for shops.
     *
     * @return An ArrayList containing the possible sponsored brands for each shop.
     * @throws ApiException If an error occurs during API interaction.
     */
    public ArrayList<String> getPossibleSponsoredBrand() throws ApiException {
        ArrayList<Shop> shops = shopDAO.readShops();
        /*ArrayList<Shop> shopsAdapted = new ArrayList<>();
        for(int comptShops = 0; comptShops < shops.size(); comptShops++){
            shopsAdapted.add(adaptShop(shops.get(comptShops)));
        }*/
        ArrayList<String> shopModels = getShopInfo("businessModel");
        ArrayList<String> possibleSponsoredBrands = new ArrayList<>();
        for(int compt = 0; compt < shops.size(); compt++){
            if(shopModels.get(compt).equals("SPONSORED")){
                ShopSponsored shopAux = (ShopSponsored) shops.get(compt);
                possibleSponsoredBrands.add(shopAux.getSponsoredBrand());
            }
            else{
                possibleSponsoredBrands.add(null);
            }
        }
        return possibleSponsoredBrands;
    }
}
