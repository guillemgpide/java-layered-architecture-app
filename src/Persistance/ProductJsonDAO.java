package Persistance;

import Business.Products.Product;
import Business.Products.ProductGeneral;
import Business.Products.ProductReduced;
import Business.Products.ProductSuperReduced;
import Exceptions.JsonException;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The ProductJsonDAO class provides data access functionality for managing product data using JSON files.
 *
 * @author : Bruno Bordoy, Guillem Gil
 * @version : 13/11/2023
 */
public class ProductJsonDAO implements ProductDAO{
    private final Path JSON_PRODUCTS_PATH;
    private final Gson gson;
    private JsonArray productJson;
    /**
     * Constructs a new ProductJsonDAO with default values and initializes the Gson instance.
     */
    public ProductJsonDAO() throws JsonException {
        this.JSON_PRODUCTS_PATH = Paths.get("Data/products.json");
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Product.class, new ProductDeserializer())
                .setPrettyPrinting()
                .create();

        if (Files.exists(JSON_PRODUCTS_PATH)) {
            try {
                String content = Files.readString(JSON_PRODUCTS_PATH);
                if (content != null && !content.isEmpty()) {
                    this.productJson = (JsonArray) JsonParser.parseString(content);
                } else {
                    this.productJson = new JsonArray();
                }
            } catch (JsonException | IOException e) {
                throw new JsonException("There was a problem parsing the json");
            }
        } else {
            this.productJson = new JsonArray();
        }
    }
    /**
     * Checks if the product data file exists.
     *
     * @return true if the product data file exists, false otherwise.
     */
    @Override
    public boolean workingProductsDAO() {
        return Files.exists(JSON_PRODUCTS_PATH);
    }
    /**
     * Reads product data from the JSON file and converts it into an ArrayList of Product objects.
     *
     * @return An ArrayList of Product objects read from the JSON file.
     */
    @Override
    public ArrayList<Product> readProducts() {
        try {
            String content = Files.readString(JSON_PRODUCTS_PATH);
            if (content != null && !content.isEmpty()) {
                // Deserializar directamente usando un deserializador personalizado
                return gson.fromJson(content, new TypeToken<ArrayList<Product>>() {}.getType());
            }
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    /**
     * Writes the provided list of products to the JSON file.
     *
     * @param products The ArrayList of Product objects to be written to the JSON file.
     */
    @Override
    public void writeProducts(ArrayList<Product> products) {
        // Adaptar los productos antes de escribirlos
        ArrayList<Product> adaptedProducts = new ArrayList<>();
        for (Product product : products) {
            Product adaptedProduct = adaptProduct(product);
            if (adaptedProduct != null) {
                adaptedProducts.add(adaptedProduct);
            }
        }

        // Convertir y escribir en el archivo JSON
        try {
            productJson = gson.toJsonTree(adaptedProducts).getAsJsonArray();

            // Modificar el JsonArray directamente antes de escribirlo
            for (int i = 0; i < productJson.size(); i++) {
                JsonObject productObject = productJson.get(i).getAsJsonObject();
                Product product = adaptedProducts.get(i);

                // Añadir las revisiones al JsonObject
                JsonArray reviewsArray = new JsonArray();
                if (product.getReviews() != null) {
                    reviewsArray = gson.toJsonTree(product.getReviews()).getAsJsonArray();
                }
                productObject.add("reviews", reviewsArray);
            }

            Files.writeString(JSON_PRODUCTS_PATH, gson.toJson(productJson));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Adapts a given Product object to a specific subclass based on its category.
     *
     * @param product The Product object to be adapted.
     * @return A new Product object of the appropriate subclass based on its category.
     *         Returns null if the category is unknown or unsupported.
     */
    private Product adaptProduct(Product product) {
        return switch (product.getCategory()) {
            case "GENERAL" -> new ProductGeneral(product.getName(), product.getBrand(), product.getCategory(), product.getPriceMax(), product.getReviews());
            case "REDUCED TAXES" -> new ProductReduced(product.getName(), product.getBrand(), product.getCategory(), product.getPriceMax(), product.getReviews());
            case "SUPERREDUCED TAXES" -> new ProductSuperReduced(product.getName(), product.getBrand(), product.getCategory(), product.getPriceMax(), product.getReviews());
            default -> null;
        };
    }
    /**
     * Checks if the JSON array representing product data is empty.
     *
     * @return true if the JSON array is empty, false otherwise.
     */
    @Override
    public boolean isProductInfoEmpty() {
        return productJson.isEmpty();
    }
}