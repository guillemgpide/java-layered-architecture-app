package Persistance;

import Business.Products.Product;
import Business.Products.ProductGeneral;
import Business.Products.ProductReduced;
import Business.Products.ProductSuperReduced;
import com.google.gson.*;

import java.lang.reflect.Type;
/**
 * The ProductDeserializer class provides custom deserialization logic for adapting JSON objects
 * to specific subclasses of the Product class based on the "category" field.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public class ProductDeserializer implements JsonDeserializer<Product> {
    /**
     * Deserializes a JSON element into a Product object of the appropriate subclass based on the "category" field.
     *
     * @param json      The JSON element to be deserialized.
     * @param typeOfT   The type of the object to deserialize.
     * @param context   The context for deserialization, providing additional information.
     * @return A deserialized Product object of the appropriate subclass.
     * @throws JsonParseException If the "category" field is unknown or unsupported.
     */
    @Override
    public Product deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String category = jsonObject.get("category").getAsString();

        // Adaptar según la categoría
        switch (category) {
            case "GENERAL":
                return context.deserialize(jsonObject, ProductGeneral.class);
            case "REDUCED TAXES":
                return context.deserialize(jsonObject, ProductReduced.class);
            case "SUPERREDUCED TAXES":
                return context.deserialize(jsonObject, ProductSuperReduced.class);
            default:
                throw new JsonParseException("Unknown product category: " + category);
        }
    }
}