package Business;

import Business.Products.*;
import Exceptions.JsonException;
import Persistance.*;
import edu.salle.url.api.exception.ApiException;
import edu.salle.url.api.exception.status.IncorrectRequestException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The `ProductManager` class manages operations related to products, including adding, correcting,
 * and retrieving product information.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public class ProductManager {
    private  ProductDAO productDAO;
    //private final ProductDAO jsonDAO;
    /**
     * Constructs a `ProductManager` and initializes the associated `ProductJsonDAO` or `ProductApiDAO`
     * based on availability and operability.
     *
     * @throws JsonException Throws it to notify there was an error with the JSON.
     */
    public ProductManager() throws JsonException {
        productDAO = new ProductApiDAO();
        if (!productDAO.workingProductsDAO()) {
            productDAO = new ProductJsonDAO();
        }
        //productDAO = new ProductJsonDAO();
    }
    /**
     * Checks if the `ProductDAO` is operational.
     *
     * @return True if the `ProductDAO` is operational, false otherwise.
     * @throws ApiException If an API-related exception occurs.
     */
    public boolean workingProductDAO() throws ApiException {
        return productDAO.workingProductsDAO();
    }
    /**
     * Adapts a generic `Product` to a specialized product based on its category.
     * The adapted product is of type `ProductGeneral`, `ProductReduced`, or `ProductSuperReduced`
     * depending on the category of the input product.
     *
     * @param product The generic product to adapt.
     * @return The adapted product based on the category.
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
     * Adds a new product with the specified attributes to the product list.
     *
     * @param productAttributes The attributes of the product.
     * @param category          The category of the product.
     * @throws ApiException If an API-related exception occurs.
     */
    public void addProduct(ArrayList<String> productAttributes, String category) throws ApiException {
        //System.out.println(getCurrentDAOType() + " ES EL K ESTEM USANT ARA");
        ArrayList<Product> products = productDAO.readProducts();
        //System.out.println(products);
        // Crea el producto específico según la categoría
        Product product = adaptProductByArrayList(category, productAttributes, null);
        // Añade el producto a la lista
        products.add(product);
        // Actualiza la lista de atributos con la categoría
        productAttributes.add(category);
        // Escribe la lista actualizada de productos en la API
        productDAO.writeProducts(products);
    }
    /**
     * Adapts a product using the provided attributes and reviews based on its category.
     * The adapted product is of type `ProductGeneral`, `ProductReduced`, or `ProductSuperReduced`
     * depending on the specified category.
     *
     * @param category          The category of the product.
     * @param productAttributes The attributes of the product.
     * @param reviews           The reviews associated with the product.
     * @return The adapted product based on the category and attributes.
     */
    private Product adaptProductByArrayList(String category, ArrayList<String> productAttributes, ArrayList<String> reviews) {
        //System.out.println(category);
        switch (category.toUpperCase()) {
            case "GENERAL":
                return new ProductGeneral(productAttributes.get(0), productAttributes.get(1), category, Float.parseFloat(productAttributes.get(2)), reviews);
            case "REDUCED TAXES":
                return new ProductReduced(productAttributes.get(0), productAttributes.get(1), category, Float.parseFloat(productAttributes.get(2)), reviews);
            case "SUPERREDUCED TAXES":
                return new ProductSuperReduced(productAttributes.get(0), productAttributes.get(1), category, Float.parseFloat(productAttributes.get(2)), reviews);
            default:
                // Puedes manejar otros casos según sea necesario
                return null;
        }
    }
    /**
     * Adds a review with a rating to a specific product.
     *
     * @param productSelected The name of the product to add a review to.
     * @param rating          The rating for the review.
     * @param review          The review content.
     * @throws ApiException If an API-related exception occurs.
     */
    public void addReviewToProduct(String productSelected, String rating, String review) throws ApiException {
        int realRating = countAsterisks(rating);//Tengo que gestionar que se haga bien el createProduct en todas las funciones en las que debo leer y luego volver a escribir la información.
        ArrayList<String> productAttributes = new ArrayList<>();
        ArrayList<String> productReviews;
        ArrayList<Product> products = productDAO.readProducts();
        for(int compt = 0; compt < products.size(); compt++){
            if(products.get(compt).getName().equals(productSelected)){
                //System.out.println("LLEGA HASTA AQUI PARA AÑADIR EL REVIEW QUE LE TOCA");
                productAttributes.add(products.get(compt).getName());
                productAttributes.add(products.get(compt).getBrand());
                productAttributes.add(String.valueOf(products.get(compt).getPriceMax()));
                productReviews = products.get(compt).getReviews();
                productReviews.add(realRating + "* " + review);
                Product adaptedProduct = adaptProductByArrayList(products.get(compt).getCategory(), productAttributes, productReviews);
                products.set(compt, adaptedProduct);
                //products.get(compt).setReview(realRating + "* " + review);
            }
        }
        productDAO.writeProducts(products);
    }
    /**
     * Checks if the provided product name is correct.
     *
     * @param namePossible The possible product name to check.
     * @return True if the name is correct, false otherwise.
     * @throws ApiException If an API-related exception occurs.
     */
    public boolean correctProductName(String namePossible) throws ApiException {
        ArrayList<Product> products = productDAO.readProducts();
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(namePossible)) {
                return false;
            }
        }
        return true;
    }
    /**
     * Corrects the product brand format.
     *
     * @param attributePossible The possible product brand to correct.
     * @return The corrected product brand.
     */
    public String correctProductBrand(String attributePossible) {
        StringBuilder result = new StringBuilder();
        String[] words = attributePossible.split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                String formattedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                result.append(formattedWord).append(" ");
            }
        }
        return result.toString().trim();
    }
    /**
     * Checks if the provided product price is correct.
     *
     * @param attributePossible The possible product price to check.
     * @return True if the price is correct, false otherwise.
     */
    public boolean correctProductPriceMax(String attributePossible) {
        float number = Float.parseFloat(attributePossible);
        return !(number < 0);
    }
    /**
     * Retrieves the names of all products.
     *
     * @return The list of product names.
     * @throws ApiException If an API-related exception occurs.
     */
    public ArrayList<String> getProductNames() throws ApiException {
        ArrayList<Product> products = productDAO.readProducts();
        ArrayList<String> productNames = new ArrayList<>();
        for (int compt = 0; compt < products.size(); compt++) {
            productNames.add(products.get(compt).getName());
        }
        return productNames;
    }
    /**
     * Retrieves the brands of all products.
     *
     * @return The list of product brands.
     * @throws ApiException If an API-related exception occurs.
     */
    public ArrayList<String> getProductBrand() throws ApiException {
        ArrayList<Product> products = productDAO.readProducts();
        ArrayList<String> productBrands = new ArrayList<>();
        for (int compt = 0; compt < products.size(); compt++) {
            productBrands.add(products.get(compt).getBrand());
        }
        return productBrands;
    }
    /**
     * Removes a product from the list by its name.
     *
     * @param productName The name of the product to remove.
     * @throws ApiException If an API-related exception occurs.
     */
    public void eraseProduct(String productName) throws ApiException {
        ArrayList<Product> products = productDAO.readProducts();
        for(int compt = 0; compt < products.size(); compt++){
            if(products.get(compt).getName().equals(productName)){
                products.remove(compt);
            }
        }
        productDAO.writeProducts(products);
    }
    /**
     * Retrieves the maximum prices of all products.
     *
     * @return The list of maximum prices for all products.
     * @throws ApiException If an API-related exception occurs.
     */
    public ArrayList<Float> getProductMaxPrices() throws ApiException {
        ArrayList<Product> products = productDAO.readProducts();
        ArrayList<Float> maxPrices = new ArrayList<>();
        for (int compt = 0; compt < products.size(); compt++) {
            maxPrices.add(products.get(compt).getPriceMax());
        }
        return maxPrices;
    }
    /**
     * Retrieves the brand associated with a specific product.
     *
     * @param productName The name of the product.
     * @return The brand of the specified product.
     * @throws ApiException If an API-related exception occurs.
     */
    public String getBrandForProduct(String productName) throws ApiException {
        ArrayList<String> productNames = getProductNames();
        ArrayList<String> brandNames = getProductBrand();
        String selectedBrand = null;
        for(int compt = 0; compt < productNames.size(); compt++){
            if(productName.equals(productNames.get(compt))){
                selectedBrand = brandNames.get(compt);
            }
        }
        return selectedBrand;
    }
    /**
     * Retrieves a list of product names with similarities to the provided query.
     *
     * @param selectedQuery The query string to match against product names and brands.
     * @return The list of product names with similarities to the query.
     * @throws ApiException If an API-related exception occurs.
     */
    public ArrayList<String> getProductsNamesBySimilarities(String selectedQuery) throws ApiException {
        ArrayList<Product> products = productDAO.readProducts();
        String queryLowerCase = selectedQuery.toLowerCase();
        ArrayList<String> similarProductsNames = new ArrayList<>();
        for(int compt = 0; compt < products.size(); compt++){
            String productNameLowerCase = products.get(compt).getName().toLowerCase();
            if (productNameLowerCase.contains(queryLowerCase) || products.get(compt).getBrand().equals(selectedQuery)) {
                similarProductsNames.add(products.get(compt).getName());
            }
        }
        return similarProductsNames;
    }
    /**
     * Retrieves a list of product brands with similarities to the provided query.
     *
     * @param selectedQuery The query string to match against product names and brands.
     * @return The list of product brands with similarities to the query.
     * @throws ApiException If an API-related exception occurs.
     */
    public ArrayList<String> getProductsBrandsBySimilarities(String selectedQuery) throws ApiException {
        ArrayList<Product> products = productDAO.readProducts();
        String queryLowerCase = selectedQuery.toLowerCase();
        ArrayList<String> similarProductsBrands = new ArrayList<>();
        for(int compt = 0; compt < products.size(); compt++){
            String productNameLowerCase = products.get(compt).getName().toLowerCase();
            if (productNameLowerCase.contains(queryLowerCase) || products.get(compt).getBrand().equals(selectedQuery)) {
                similarProductsBrands.add(products.get(compt).getBrand());
            }
        }
        return similarProductsBrands;
    }
    /**
     * Retrieves the reviews for a specific product.
     *
     * @param productSelected The name of the product to retrieve reviews for.
     * @return The list of reviews for the specified product.
     * @throws IncorrectRequestException If an incorrect request exception occurs.
     */
    public ArrayList<String> getProductReviews(String productSelected) throws IncorrectRequestException {
        try {
            ArrayList<Product> products = productDAO.readProducts();
            ArrayList<String> reviews = new ArrayList<>();
            for (Product product : products) {
                if (product.getName().equals(productSelected)) {
                    reviews.addAll(product.getReviews());
                }
            }
            return reviews;
        } catch (ApiException e){
            throw new IncorrectRequestException(" ", 500);
        }
    }
    /**
     * Calculates and retrieves the average review rating for a specific product.
     *
     * @param productSelected The name of the product to calculate the average review for.
     * @return The average review rating for the specified product.
     * @throws ApiException If an API-related exception occurs.
     */
    public float getAverageReview(String productSelected) throws ApiException {
        float sumOfReviews = 0;
        float numberOfReviews = 0;
        ArrayList<String> reviews = getProductReviews(productSelected);
        if (reviews != null) {
            for (String review : reviews) {
                int reviewNumber = obtainNumberOfTheSentence(review);
                if (reviewNumber >= 0) {
                    sumOfReviews += reviewNumber;
                    numberOfReviews++;
                }
            }
            if (numberOfReviews > 0) {
                float averageReview = sumOfReviews / numberOfReviews;
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator('.');
                symbols.setGroupingSeparator(',');
                DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);
                String formattedResult = decimalFormat.format(averageReview);
                formattedResult = formattedResult.replace(',', '.');
                return Float.parseFloat(formattedResult);
            }
        }
        return 0;
    }
    /**
     * Obtains the numerical part of a review sentence.
     *
     * @param review The review sentence to extract the numerical part from.
     * @return The numerical part of the review sentence.
     */
    public int obtainNumberOfTheSentence(String review) {
        String patron = "(\\d+)\\*";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(review);
        if (matcher.find()) {
            String numeroStr = matcher.group(1);
            return Integer.parseInt(numeroStr);
        } else {
            return -1;
        }
    }
    /**
     * Retrieves the discounts for all products.
     *
     * @return The list of discounts for all products.
     * @throws ApiException If an API-related exception occurs.
     */
    public ArrayList<Float> getProductDiscounts() throws ApiException {
        ArrayList<Float> discounts = new ArrayList<>();
        Product auxProduct;
        ArrayList<Product> products = productDAO.readProducts();
        for(int compt = 0; compt < products.size(); compt++){

            auxProduct = adaptProduct(products.get(compt));
            if(auxProduct.getDiscount() == 0.1F && getAverageReview(auxProduct.getName()) >= 3.5){
                auxProduct.setDiscount(0.05F);
            }
            discounts.add(auxProduct.getDiscount());
        }
        return discounts;
    }

    /**
     * Counts the number of asterisks in a rating.
     *
     * @param rating The rating string containing asterisks.
     * @return The number of asterisks in the rating.
     */
    public int countAsterisks(String rating) {
        if (rating != null && !rating.isEmpty() && rating.matches("[*]{1,5}")) {
            return rating.length();
        } else {
            return 0;
        }
    }
    /**
     * Checks if the provided rating string is correct.
     *
     * @param rating The rating string to check.
     * @return True if the rating is correct, false otherwise.
     */
    public boolean correctRating(String rating) {
        return rating != null && rating.matches("\\*{1,5}");
    }
    /**
     * Checks if there are products stored in the data source.
     *
     * @return True if there are products, false otherwise.
     * @throws ApiException If an API-related exception occurs.
     */
    public boolean thereAreProducts() throws ApiException {
        return !productDAO.isProductInfoEmpty();
    }

    //LA INFORMACIO ES GUARDA CORRECTAMENT PER SEPARAT, TANT SHOPS I PRODUCTS EN JSON COM EN API, TROBEM QUE L'UPDATE DE JSON CAP A API NO FUNCIONA Y NO SABEM PERQUE
    /**
     * Updates information from products, transferring data between JSON and API sources.
     *
     * @return True if the update is successful, false otherwise.
     * @throws ApiException If an API-related exception occurs.
     * @throws JsonException If a JSON-related exception occurs.
     */
    public boolean updateInfoFromProducts() throws ApiException, JsonException {
        ArrayList<Product> products;
        boolean error = true;
        if(productDAO.workingProductsDAO() && getCurrentDAOType().equals("API")){
            products = productDAO.readProducts();
            //System.out.println("This are from API to json" + products);
            productDAO = new ProductJsonDAO();
            productDAO.writeProducts(products);
            productDAO = new ProductApiDAO();
            workingProductDAO();
            //System.out.println("pasem info de api a json");
        }else if (productDAO.workingProductsDAO() && getCurrentDAOType().equals("JSON")){
            try{
                products = productDAO.readProducts();
                productDAO = new ProductApiDAO();
                workingProductDAO();
                productDAO.writeProducts(products);
                //System.out.println("This are from json to API " + products);
            } catch(NullPointerException ignored){
                error = false;
            }
            productDAO = new ProductJsonDAO();

           // System.out.println("pasem info de json a api");
        }
        return error;
    }
    /**
     * Retrieves the type of the current data access object (DAO) used for product management.
     *
     * @return The type of the current DAO, either "API", "JSON", or "Unknown" if not recognized.
     */
    public String getCurrentDAOType() {
        if (productDAO instanceof ProductApiDAO) {
            return "API";

        } else if (productDAO instanceof ProductJsonDAO) {
            return "JSON";
        } else {
            return "Unknown";
        }

    }
}
