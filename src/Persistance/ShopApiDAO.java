package Persistance;

import Business.Products.*;
import Business.Shops.Shop;
import Business.Shops.ShopLoyalty;
import Business.Shops.ShopMaxBenefit;
import Business.Shops.ShopSponsored;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.salle.url.api.ApiHelper;
import edu.salle.url.api.exception.ApiException;

import java.util.ArrayList;
/**
 * The ShopApiDAO class provides data access functionality for managing shop data using an API.
 *
 * @author : Bruno Bordoy, Guillem Gil
 * @version : 13/11/2023
 */
public class ShopApiDAO implements ShopDAO {
    private final Gson gson;
    private ApiHelper apiHelper;
    private static final String API_ENDPOINT = "/shops";
    private final String API_URL = "https://balandrau.salle.url.edu/dpoo/S1_Project_102";
    /**
     * Initializes the ShopApiDAO by creating a Gson instance for JSON processing.
     */
    public ShopApiDAO() {
        this.gson = new Gson();
    }
    /**
     * Checks if the ShopApiDAO is able to connect to the API successfully.
     *
     * @return True if the connection to the API is successful, false otherwise.
     */
    @Override
    public boolean workingShopsDAO() {
        try {
            this.apiHelper = new ApiHelper();
            return true;
        } catch (ApiException e) {
            return false;
        }
    }
    /**
     * Reads shop data from the API, converts the API response to a list of Shop objects,
     * and returns the list.
     *
     * @return An ArrayList of Shop objects read from the API.
     * @throws ApiException If there is an issue with the API request or response.
     */
    @Override
    public ArrayList<Shop> readShops() throws ApiException {
        String apiResponse = apiHelper.getFromUrl(API_URL + API_ENDPOINT);
        JsonArray shopsArray = gson.fromJson(apiResponse, JsonArray.class);
        return convertJsonArrayToShopList(shopsArray);
    }
    /**
     * Converts a JSON array representing shop data to a list of Shop objects.
     *
     * @param shopsArray The JSON array representing shop data from the API.
     * @return An ArrayList of Shop objects adapted from the API response.
     */
    private ArrayList<Shop> convertJsonArrayToShopList(JsonArray shopsArray) {
        ArrayList<Shop> shopList = new ArrayList<>();
        for (JsonElement shopElement : shopsArray) {
            if (shopElement.isJsonArray()) {
                JsonArray shopArray = shopElement.getAsJsonArray();
                for (JsonElement element : shopArray) {
                    if (element.isJsonObject()) {
                        JsonObject shopObject = element.getAsJsonObject();

                        JsonElement nameElement = shopObject.get("name");
                        JsonElement descriptionElement = shopObject.get("description");
                        JsonElement earningsElement = shopObject.get("earnings");
                        JsonElement sinceElement = shopObject.get("since");
                        JsonElement businessModelElement = shopObject.get("businessModel");
                        JsonElement loyaltyThresholdElement = shopObject.get("loyaltyThreshold");
                        JsonElement sponsoredBrandElement = shopObject.get("sponsoredBrand");

                        if (nameElement != null && descriptionElement != null && sinceElement != null && businessModelElement != null) {
                            String name = nameElement.getAsString();
                            String description = descriptionElement.getAsString();
                            float earnings = earningsElement.getAsFloat();
                            int since = sinceElement.getAsInt();
                            String businessModel = businessModelElement.getAsString();
                            float loyaltyThreshold = (loyaltyThresholdElement != null && !loyaltyThresholdElement.isJsonNull()) ? loyaltyThresholdElement.getAsFloat() : 0.0f;
                            String sponsoredBrand = (sponsoredBrandElement != null && !sponsoredBrandElement.isJsonNull()) ? sponsoredBrandElement.getAsString() : "";

                            JsonArray catalogueArray = shopObject.has("catalogue") ? shopObject.getAsJsonArray("catalogue") : new JsonArray();
                            ArrayList<SpecificProduct> catalogue = convertJsonArrayToSpecificProductList(catalogueArray);

                            Shop shop = createShop(name, description, earnings, since, businessModel, catalogue, sponsoredBrand, loyaltyThreshold);
                            shopList.add(shop);
                        }
                    }
                }
            }
        }
        return shopList;
    }
    /**
     * Converts a JSON array representing specific product data to a list of SpecificProduct objects.
     *
     * @param catalogueArray The JSON array representing specific product data from the API.
     * @return An ArrayList of SpecificProduct objects adapted from the API response.
     */
    private ArrayList<SpecificProduct> convertJsonArrayToSpecificProductList(JsonArray catalogueArray) {
        ArrayList<SpecificProduct> catalogue = new ArrayList<>();
        for (JsonElement productElement : catalogueArray) {
            if (productElement.isJsonObject()) {
                JsonObject productObject = productElement.getAsJsonObject();

                String productName = productObject.has("productName") ? productObject.get("productName").getAsString() : "";
                String productBrand = productObject.has("productBrand") ? productObject.get("productBrand").getAsString() : "";
                float productPrice = productObject.has("productPrice") ? productObject.get("productPrice").getAsFloat() : 0.0f;

                JsonArray productRatingArray = productObject.has("productRating") ? productObject.getAsJsonArray("productRating") : new JsonArray();
                ArrayList<String> productRating = convertJsonArrayToStringList(productRatingArray);

                JsonArray reviewsForShopArray = productObject.has("reviewsForShop") ? productObject.getAsJsonArray("reviewsForShop") : new JsonArray();
                ArrayList<String> reviewsForShop = convertJsonArrayToStringList(reviewsForShopArray);

                SpecificProduct specificProduct = new SpecificProduct(productName, productBrand, productPrice);
                specificProduct.setRatings(productRating);
                specificProduct.setReviewsForShop(reviewsForShop);
                catalogue.add(specificProduct);
            }
        }
        return catalogue;
    }
    /**
     * Converts a JSON array of strings to an ArrayList of strings.
     *
     * @param jsonArray The JSON array of strings from the API.
     * @return An ArrayList of strings adapted from the API response.
     */
    private ArrayList<String> convertJsonArrayToStringList(JsonArray jsonArray) {
        ArrayList<String> stringList = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                stringList.add(element.getAsString());
            }
        }
        return stringList;
    }
    /**
     * Adapts a Shop object received from the API by converting it to the appropriate subclass
     * based on the business model and returns the adapted Shop object.
     *
     * @param shop The Shop object received from the API.
     * @return An adapted Shop object based on its business model.
     */
    private Shop adaptShop(Shop shop) {
        //System.out.println(shop);
        String model = shop.getModel();
        //System.out.println(model);
        switch (model) {
            case "LOYALTY":
                return new ShopLoyalty(
                        shop.getName(), shop.getDescription(), shop.getYear(),
                        shop.getIncome(), model, shop.getCatalogue(),
                        ((ShopLoyalty) shop).getLoyaltyThreshold()
                );
            case "MAXIMUM BENEFITS":
                return new ShopMaxBenefit(
                        shop.getName(), shop.getDescription(), shop.getYear(),
                        shop.getIncome(), model, shop.getCatalogue()
                );
            case "SPONSORED":
                return new ShopSponsored(
                        shop.getName(), shop.getDescription(), shop.getYear(),
                        shop.getIncome(), model, shop.getCatalogue(),
                        ((ShopSponsored) shop).getSponsoredBrand()
                );
            default:
                return null;
        }
    }
    /**
     * Writes the provided list of Shop objects to the API after adapting them to the API format.
     * Deletes all existing shop data from the API before writing the new data.
     *
     * @param shops The ArrayList of Shop objects to be written to the API.
     * @throws ApiException If there is an issue with the API request or response.
     */
    @Override
    public void writeShops(ArrayList<Shop> shops) throws ApiException {
        deleteAllShops();
        ArrayList<Shop> adaptedShops = new ArrayList<>();
        for (int compt = 0; compt < shops.size(); compt++) {
            Shop adaptedShop = adaptShop(shops.get(compt));
        //System.out.println(adaptedShop);
            if (adaptedShop instanceof ShopLoyalty) {
                ShopLoyalty loyaltyShop = (ShopLoyalty) adaptedShop;
                adaptedShops.add(adaptedShop);
            } else if (adaptedShop instanceof ShopSponsored) {
                ShopSponsored sponsoredShop = (ShopSponsored) adaptedShop;
                adaptedShops.add(adaptedShop);
            } else if (adaptedShop instanceof ShopMaxBenefit) {
                ShopMaxBenefit maxBenefitShop = (ShopMaxBenefit) adaptedShop;
                adaptedShops.add(adaptedShop);
            }

        }
        JsonArray shopsArray = new JsonArray();
        for (Shop shop : adaptedShops) {
            //System.out.println(shop);
            JsonObject shopObject = new JsonObject();
            shopObject.addProperty("name", shop.getName());
            shopObject.addProperty("description", shop.getDescription());
            shopObject.addProperty("earnings", shop.getIncome());
            shopObject.addProperty("since", shop.getYear());
            shopObject.addProperty("businessModel", shop.getModel());
            if (shop instanceof ShopLoyalty) {
                ShopLoyalty loyaltyShop = (ShopLoyalty) shop;
                String loyaltyThreshold = String.valueOf(loyaltyShop.getLoyaltyThreshold());
                shopObject.addProperty("loyaltyThreshold", loyaltyThreshold);
            } else if (shop instanceof ShopSponsored) {
                ShopSponsored shopSponsored = (ShopSponsored) shop;
                String sponsored = shopSponsored.getSponsoredBrand();
                shopObject.addProperty("sponsoredBrand", sponsored.isEmpty() ? "null" : sponsored);
            } else if (shop instanceof ShopMaxBenefit) {
                ShopMaxBenefit shopMaxBenefit = (ShopMaxBenefit) shop;
            }
            JsonArray catalogueArray = new JsonArray();
            if (shop.getCatalogue() != null) {
                for (SpecificProduct specificProduct : shop.getCatalogue()) {
                    JsonObject specificProductObject = new JsonObject();
                    specificProductObject.addProperty("productName", specificProduct.getName());
                    specificProductObject.addProperty("productBrand", specificProduct.getBrand());
                    specificProductObject.addProperty("productPrice", specificProduct.getPrice());
                    if (specificProduct.getProductRating() != null) {
                        specificProductObject.add("productRating", gson.toJsonTree(specificProduct.getProductRating()));
                    } else {
                        specificProductObject.add("productRating", new JsonArray()); // Si es nulo, asigna una lista vacía
                    }
                    if (specificProduct.getReviewsForShop() != null) {
                        specificProductObject.add("reviewsForShop", gson.toJsonTree(specificProduct.getReviewsForShop()));
                    } else {
                        specificProductObject.add("reviewsForShop", new JsonArray()); // Si es nulo, asigna una lista vacía
                    }
                    catalogueArray.add(specificProductObject);
                }
            }
            shopObject.add("catalogue", catalogueArray);
            shopsArray.add(shopObject);
        }
        String prettyJson = gson.toJson(shopsArray);
        String response = apiHelper.postToUrl(API_URL + API_ENDPOINT, prettyJson);
    }
    /**
     * Deletes all shop data from the API.
     *
     * @throws ApiException If there is an issue with the API request or response.
     */
    private void deleteAllShops() throws ApiException {
        if(!isShopInfoEmpty()){
            String deleteResponse = apiHelper.deleteFromUrl(API_URL + API_ENDPOINT);
        }
    }
    /**
     * Checks if shop data is present in the API.
     *
     * @return True if the API has shop data, false if the data is empty or not present.
     * @throws ApiException If there is an issue with the API request or response.
     */
    @Override
    public boolean isShopInfoEmpty() throws ApiException {
        String apiResponse = apiHelper.getFromUrl(API_URL + API_ENDPOINT);
        JsonArray shopsArray = gson.fromJson(apiResponse, JsonArray.class);
        return shopsArray == null || shopsArray.size() == 0;
    }
    //Esta funcion no me hace falta en ShopApiDAO.
    /**
     * This method is not required in ShopApiDAO and is not implemented.
     * It is part of the ShopDAO interface.
     */
    @Override
    public void informDaoToCreate() {}
    /**
     * Creates a Shop object based on the provided attributes and business model.
     *
     * @param name The name of the shop.
     * @param description The description of the shop.
     * @param earnings The earnings of the shop.
     * @param since The year since the shop has been in operation.
     * @param businessModel The business model of the shop.
     * @param catalogue The catalog of specific products associated with the shop.
     * @param sponsoredBrand The brand associated with a sponsored shop.
     * @param loyaltyThreshold The loyalty threshold associated with a loyalty shop.
     * @return A Shop object based on the provided attributes and business model.
     */
    private Shop createShop(String name, String description, float earnings, int since, String businessModel, ArrayList<SpecificProduct> catalogue, String sponsoredBrand, float loyaltyThreshold) {
        return switch (businessModel) {
            case "LOYALTY" -> new ShopLoyalty(name, description, since, earnings, businessModel, catalogue, loyaltyThreshold);
            case "MAXIMUM BENEFITS" -> new ShopMaxBenefit(name, description, since, earnings, businessModel, catalogue);
            case "SPONSORED" -> new ShopSponsored(name, description, since, earnings, businessModel, catalogue, sponsoredBrand);
            default -> null;
        };
    }
}
