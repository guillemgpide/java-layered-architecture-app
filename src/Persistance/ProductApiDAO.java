package Persistance;

import Business.Products.Product;
import Business.Products.ProductGeneral;
import Business.Products.ProductReduced;
import Business.Products.ProductSuperReduced;
import com.google.gson.*;
import edu.salle.url.api.ApiHelper;
import edu.salle.url.api.exception.ApiException;
import edu.salle.url.api.exception.UnreachableServerException;

import java.io.IOException;
import java.util.ArrayList;
/**
 * The ProductApiDAO class provides data access functionality for managing product data using an API with JSON format.
 * It implements the ProductDAO interface and defines methods to interact with the API, such as reading and writing
 * products, checking the existence of product data, and determining if the data is empty.
 * This class uses an ApiHelper instance to perform HTTP requests for CRUD operations on the product data.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public class ProductApiDAO implements ProductDAO {
    private final Gson gson;
    private ApiHelper apiHelper;
    private final String API_ENDPOINT = "/products";
    private final String API_URL = "https://balandrau.salle.url.edu/dpoo/S1_Project_102";
    /**
     * Constructs a new ProductApiDAO and initializes the Gson instance.
     */
    public ProductApiDAO() {
        this.gson = new Gson();
    }
    /**
     * Checks if the API connection for product data is working.
     *
     * @return true if the API connection is working, false otherwise.
     */
    @Override
    public boolean workingProductsDAO() {
        try {
            this.apiHelper = new ApiHelper();
            return true;
        } catch (ApiException e) {
            return false;
        }
    }
    /**
     * Reads product data from the API and converts it into an ArrayList of Product objects.
     *
     * @return An ArrayList of Product objects read from the API.
     * @throws ApiException If an API-related exception occurs during the operation.
     */
    @Override
    public ArrayList<Product> readProducts() throws ApiException {
        String apiResponse = apiHelper.getFromUrl(API_URL + API_ENDPOINT);
        JsonArray productsArray = gson.fromJson(apiResponse, JsonArray.class);
        return convertJsonArrayToProductList(productsArray);
    }
    /**
     * Writes the provided list of products to the API.
     *
     * @param products The ArrayList of Product objects to be written to the API.
     * @throws ApiException If an API-related exception occurs during the operation.
     */
    @Override
    public void writeProducts(ArrayList<Product> products) throws ApiException {
        //System.out.println("ahora borro todos");
        deleteAllProducts();
        //System.out.println("he podido borrarlos");
        JsonArray productsArray = new JsonArray();
        for (Product product : products) {
            JsonObject productObject = new JsonObject();
            productObject.addProperty("name", product.getName());
            productObject.addProperty("brand", product.getBrand());
            productObject.addProperty("mrp", product.getPriceMax());
            productObject.addProperty("category", product.getCategory());
            // Crear un array JSON para las revisiones
            JsonArray reviewsArray = new JsonArray();
            if(product.getReviews() != null){
                for (String review : product.getReviews()) {
                    reviewsArray.add(new JsonPrimitive(review));
                }
            }
            productObject.add("reviews", reviewsArray);
            productsArray.add(productObject);
        }
        String prettyJson = gson.toJson(productsArray);
        String response = apiHelper.postToUrl(API_URL + API_ENDPOINT, prettyJson);
    }
    /**
     * Deletes all product data from the API.
     *
     * @throws ApiException If an API-related exception occurs during the operation.
     */
    private void deleteAllProducts() throws ApiException {
        if(!isProductInfoEmpty()){
            String deleteResponse = apiHelper.deleteFromUrl(API_URL + API_ENDPOINT);
        }
    }
    /**
     * Checks if the product data from the API is empty.
     *
     * @return true if the product data is empty, false otherwise.
     * @throws ApiException If an API-related exception occurs during the operation.
     */
    @Override
    public boolean isProductInfoEmpty() throws ApiException {
        try{
            String apiResponse = apiHelper.getFromUrl(API_URL + API_ENDPOINT);
            JsonArray productsArray = gson.fromJson(apiResponse, JsonArray.class);
            return productsArray == null || productsArray.size() == 0;
        } catch(ApiException e){
            throw  new UnreachableServerException(API_URL + API_ENDPOINT, e);
        }
    }
    /**
     * Converts a JSON array representing product data into an ArrayList of Product objects.
     *
     * @param outerArray The JSON array containing product data.
     * @return An ArrayList of Product objects converted from the JSON array.
     */
    private ArrayList<Product> convertJsonArrayToProductList(JsonArray outerArray) {
        ArrayList<Product> productList = new ArrayList<>();
        for (JsonElement innerElement : outerArray) {
            if (innerElement.isJsonArray()) {
                JsonArray productsArray = innerElement.getAsJsonArray();
                for (JsonElement element : productsArray) {
                    if (element.isJsonObject()) {
                        JsonObject productObject = element.getAsJsonObject();
                        // Verifica si los atributos existen antes de intentar obtenerlos
                        JsonElement nameElement = productObject.get("name");
                        JsonElement brandElement = productObject.get("brand");
                        JsonElement categoryElement = productObject.get("category");
                        JsonElement mrpElement = productObject.get("mrp");
                        if (nameElement != null && brandElement != null && categoryElement != null && mrpElement != null) {
                            String name = nameElement.getAsString();
                            String brand = brandElement.getAsString();
                            String category = categoryElement.getAsString();
                            float mrp = mrpElement.getAsFloat();
                            // Obtén la lista de reviews
                            JsonArray reviewsArray = productObject.has("reviews") ? productObject.getAsJsonArray("reviews") : new JsonArray();
                            ArrayList<String> reviewsList = convertJsonArrayToReviewList(reviewsArray);
                            // Crea el objeto Product y añádelo a la lista
                            Product product = createProduct(name, brand, category, mrp, reviewsList);
                            productList.add(product);
                        }
                    }
                }
            }
        }
        return productList;
    }
    /**
     * Converts a JSON array representing product reviews into an ArrayList of strings.
     *
     * @param reviewsArray The JSON array containing product reviews.
     * @return An ArrayList of strings representing product reviews.
     */
    private ArrayList<String> convertJsonArrayToReviewList(JsonArray reviewsArray) {
        ArrayList<String> reviewList = new ArrayList<>();
        for (JsonElement reviewElement : reviewsArray) {
            reviewList.add(reviewElement.getAsString());
        }
        return reviewList;
    }
    /**
     * Creates a Product object based on the provided attributes.
     *
     * @param name         The name of the product.
     * @param brand        The brand of the product.
     * @param category     The category of the product.
     * @param mrp          The maximum retail price of the product.
     * @param reviewsList  An ArrayList of strings representing product reviews.
     * @return A Product object created based on the provided attributes.
     */
    private Product createProduct(String name, String brand, String category, float mrp, ArrayList<String> reviewsList) {
        return switch (category) {
            case "GENERAL" -> new ProductGeneral(name, brand, category, mrp, reviewsList);
            case "REDUCED TAXES" -> new ProductReduced(name, brand, category, mrp, reviewsList);
            case "SUPERREDUCED TAXES" -> new ProductSuperReduced(name, brand, category, mrp, reviewsList);
            default -> null;
        };
    }
}
