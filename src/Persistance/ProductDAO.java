package Persistance;

import Business.Products.Product;
import edu.salle.url.api.exception.ApiException;

import java.util.ArrayList;
/**
 * The ProductDAO interface defines data access functionality for managing product data using JSON files.
 * Implementing classes should provide methods to check the existence of the product data file, read and
 * write products to/from the file, and check if the product data is empty.

 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public interface ProductDAO {
    /**
     * Checks if the product data file exists.
     *
     * @return true if the product data file exists, false otherwise.
     */
    boolean workingProductsDAO();
    /**
     * Reads product data from the JSON file and converts it into an ArrayList of Product objects.
     *
     * @return An ArrayList of Product objects read from the JSON file.
     * @throws ApiException If an API-related exception occurs during the operation.
     */
    ArrayList<Product> readProducts() throws ApiException;
    /**
     * Writes the provided list of products to the JSON file.
     *
     * @param products The ArrayList of Product objects to be written to the JSON file.
     * @throws ApiException If an API-related exception occurs during the operation.
     */
    void writeProducts(ArrayList<Product> products) throws ApiException;
    /**
     * Checks if the JSON array representing product data is empty.
     *
     * @return true if the JSON array is empty, false otherwise.
     * @throws ApiException If an API-related exception occurs during the operation.
     */
    boolean isProductInfoEmpty() throws ApiException;
}
