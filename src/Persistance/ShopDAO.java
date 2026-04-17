package Persistance;

import Business.Shops.Shop;
import Exceptions.CreateDirectoryException;
import edu.salle.url.api.exception.ApiException;

import java.util.ArrayList;
/**
 * The ShopDAO interface defines the contract for data access functionality related to managing shop data using JSON files.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public interface ShopDAO {
    /**
     * Checks if the shop data file exists.
     *
     * @return true if the shop data file exists, false otherwise.
     */
    boolean workingShopsDAO();
    /**
     * Reads shop data from the storage and converts it into an ArrayList of Shop objects.
     *
     * @return An ArrayList of Shop objects read from the storage.
     * @throws ApiException If there is an issue with the API during the data retrieval process.
     */
    ArrayList<Shop> readShops() throws ApiException;
    /**
     * Writes the provided list of shops to the storage.
     *
     * @param shops The ArrayList of Shop objects to be written to the storage.
     * @throws ApiException If there is an issue with the API during the data writing process.
     */
    void writeShops(ArrayList<Shop> shops) throws ApiException;
    /**
     * Checks if the storage representing shop data is empty.
     *
     * @return true if the storage is empty, false otherwise.
     * @throws ApiException If there is an issue with the API during the data retrieval process.
     */
    boolean isShopInfoEmpty() throws ApiException;
    /**
     * Informs the DAO to create data when the shop data storage doesn't exist.
     *
     * @throws CreateDirectoryException If there is an issue creating the directory for the shop data storage.
     */
    void informDaoToCreate() throws CreateDirectoryException;
}
