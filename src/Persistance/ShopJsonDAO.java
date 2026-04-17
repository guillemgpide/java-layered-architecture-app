package Persistance;

import Business.Shops.Shop;
import Business.Products.SpecificProduct;
import Business.Shops.ShopLoyalty;
import Business.Shops.ShopMaxBenefit;
import Business.Shops.ShopSponsored;
import Exceptions.CreateDirectoryException;
import Exceptions.JsonException;
import com.google.gson.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
/**
 * The ShopJsonDAO class provides data access functionality for managing shop data using JSON files.
 *
 * @author : Bruno Bordoy, Guillem Gil
 * @version : 13/11/2023
 */
public class ShopJsonDAO implements ShopDAO{
    private final Path JSON_SHOPS_PATH = Paths.get("Data/shops.json");
    private final Gson gson;
    private JsonArray shopJson;

    /**
     * Constructs a new ShopJsonDAO with default values and initializes the Gson instance.
     */
    public ShopJsonDAO() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        if (Files.exists(JSON_SHOPS_PATH)) {
            try {
                String content = Files.readString(JSON_SHOPS_PATH);
                if (content != null && !content.isEmpty()) {
                    // Si el archivo no está vacío, lo cargamos en la variable shopJson
                    this.shopJson = JsonParser.parseString(content).getAsJsonArray();
                } else {
                    // Si está vacío, inicializamos shopJson como un array vacío
                    this.shopJson = new JsonArray();
                }
            } catch (JsonException | IOException e) {
                throw new JsonException("There was a problem parsing the json");
            }
        } else {
            // Si el archivo no existe, inicializamos shopJson como un array vacío
            this.shopJson = new JsonArray();
        }
    }
    /**
     * Checks if the shop data file exists.
     *
     * @return true if the shop data file exists, false otherwise.
     */
    @Override
    public boolean workingShopsDAO() {
        return Files.exists(JSON_SHOPS_PATH);
    }

    /**
     * Reads shop data from the JSON file and converts it into an ArrayList of Shop objects.
     *
     * @return An ArrayList of Shop objects read from the JSON file.
     */

    @Override
    public ArrayList<Shop> readShops() {
        ArrayList<Shop> shops = new ArrayList<>();
        for (int i = 0; i < shopJson.size(); i++) {
            JsonObject shopObject = shopJson.get(i).getAsJsonObject();
            Shop shop = adaptShop(shopObject); // Utiliza el método adaptShop

            // Procesa el catálogo de productos
            JsonArray productArray = shopObject.getAsJsonArray("catalogue");
            ArrayList<SpecificProduct> products = new ArrayList<>();
            for (int j = 0; j < productArray.size(); j++) {
                JsonObject productObject = productArray.get(j).getAsJsonObject();
                SpecificProduct specificProduct = gson.fromJson(productObject, SpecificProduct.class);
                products.add(specificProduct);
            }
            shop.setCatalogue(products);

            shops.add(shop);
        }
        return shops;
    }
    /**
     * Adapts a JsonObject representing shop attributes from JSON to a corresponding Shop object.
     *
     * @param shopObject The JsonObject containing shop attributes.
     * @return A Shop object adapted from the provided JsonObject.
     */
    private Shop adaptShop(JsonObject shopObject) {
        String name = shopObject.get("name").getAsString();
        String description = shopObject.get("description").getAsString();
        float earnings = shopObject.get("earnings").getAsFloat();
        int since = shopObject.get("since").getAsInt();
        String businessModel = shopObject.get("businessModel").getAsString();
        String sponsoredBrand = shopObject.has("sponsoredBrand") ? shopObject.get("sponsoredBrand").getAsString() : null;
        String loyaltyThreshold = shopObject.has("loyaltyThreshold") ? shopObject.get("loyaltyThreshold").getAsString() : null;

        // Deserializa el catálogo de productos
        JsonArray productArray = shopObject.getAsJsonArray("catalogue");
        ArrayList<SpecificProduct> catalogue = new ArrayList<>();
        for (int j = 0; j < productArray.size(); j++) {
            JsonObject productObject = productArray.get(j).getAsJsonObject();
            SpecificProduct specificProduct = gson.fromJson(productObject, SpecificProduct.class);
            catalogue.add(specificProduct);
        }

        // Determina el tipo de tienda y llama al método correspondiente
        switch (businessModel) {
            case "LOYALTY":
                return new ShopLoyalty(name, description, since, earnings, businessModel, catalogue, Float.parseFloat(loyaltyThreshold));
            case "MAXIMUM BENEFITS":
                return new ShopMaxBenefit(name, description, since, earnings, businessModel, catalogue);
            case "SPONSORED":
                return new ShopSponsored(name, description, since, earnings, businessModel, catalogue, sponsoredBrand);
            default:
                return null;
        }
    }
    /**
     * Writes the provided list of shops to the JSON file.
     *
     * @param shops The ArrayList of Shop objects to be written to the JSON file.
     */

    @Override
    public void writeShops(ArrayList<Shop> shops) {
        JsonArray shopsArray = new JsonArray();
        for (Shop shop : shops) {
            JsonObject shopObject = gson.toJsonTree(shop).getAsJsonObject();

            // Serializa el catálogo de productos
            JsonArray productArray = new JsonArray();
            for (SpecificProduct product : shop.getCatalogue()) {
                JsonObject productObject = gson.toJsonTree(product).getAsJsonObject();
                productObject.remove("reviews");
                productObject.remove("mrp");
                productArray.add(productObject);
            }

            shopObject.add("catalogue", productArray);
            shopsArray.add(shopObject);
        }
        this.shopJson = shopsArray;
        try {
            Files.writeString(JSON_SHOPS_PATH, gson.toJson(shopsArray));
        } catch (JsonException | IOException e) {
            throw new JsonException("There was a problem parsing the json");
        }
    }
    /**
     * Checks if the JSON array representing shop data is empty.
     *
     * @return true if the JSON array is empty, false otherwise.
     */
    @Override
    public boolean isShopInfoEmpty() {
        return shopJson.size() == 0;
    }
    /**
     * Is called when the shops.json doesn't exist, so it creates it and initializes the ShopJsonDAO again.
     */
    @Override
    public void informDaoToCreate() throws CreateDirectoryException {
        Path dataDirectory = Paths.get("Data");
        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                throw new CreateDirectoryException("There was an error creating shops.json");
            }
        }
        if (!Files.exists(JSON_SHOPS_PATH)) {
            this.shopJson = new JsonArray();
            new ShopJsonDAO();
            try {
                Files.writeString(JSON_SHOPS_PATH, gson.toJson(shopJson));
            } catch (IOException e) {
                throw new CreateDirectoryException("There was an error creating shops.json");

            }
        }
    }
}
