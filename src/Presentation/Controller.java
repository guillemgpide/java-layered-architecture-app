package Presentation;

import Business.CartManager;
import Business.ProductManager;
import Business.ShopManager;
import edu.salle.url.api.exception.ApiException;
import edu.salle.url.api.exception.status.IncorrectRequestException;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * The Controller class handles user input, manages the flow of the program, and interacts with the UI, ProductManager,
 * ShopManager, and CartManager.
 *
 * @author : Bruno Bordoy, Guillem Gil
 * @version : 13/11/2023
 */
public class Controller {
    private final UIManager uiManager;
    private final ProductManager productManager;
    private final ShopManager shopManager;
    private final CartManager cartManager;
    /**
     * Constructs a new Controller with associated UI, ProductManager, ShopManager, and CartManager instances.
     */
    public Controller(){
        uiManager = new UIManager();
        productManager = new ProductManager();
        shopManager = new ShopManager();
        cartManager = new CartManager();
    }
    /**
     * Runs the main program, initializing necessary components and presenting the main menu to the user.
     */
    public void run() {

        uiManager.presentationToMall();

        uiManager.showMessage("Checking API status...");
        try {
            if(startingApiDAO()){
                uiManager.showMessage("Starting program...");
                this.mainMenu();
            } else{
                uiManager.showMessage("Error: The API isn't available.\n\nVerifying local files...");
                if(productManager.workingProductDAO() && productManager.getCurrentDAOType().equals("JSON")){
                    uiManager.showMessage("Starting program...");
                    this.mainMenu();
                } else{
                    uiManager.showMessage("Error: The products.json file can't be accessed.\n");
                    uiManager.showMessage("Shutting down...");
                }
            }
        } catch (ApiException e) {
            uiManager.showMessage("Error sending a request to the products endpoint");
        }
    }
    /**
     * Checks if the ProductManager successfully initializes data from the products.json file.
     *
     * @return True if the initialization is successful, false otherwise.
     */
    private boolean startingApiDAO() throws ApiException {
        if(shopManager.getCurrentDAOType().equals("API") && productManager.getCurrentDAOType().equals("API")){
            //AIXO HO HARDCODEJO PER GESTIONAR COM VAN ELS JSONS
            //return false;
            return productManager.workingProductDAO() && shopManager.workingShopDAO();
        }else{
            return false;
        }
    }
    /**
     * Displays the main menu and handles user input to navigate through different program functionalities.
     */
    private void mainMenu() {
        int option;
        boolean firstProductManager = false;
        try {
            if(productManager.updateInfoFromProducts()){
                shopManager.updateInfoFromShops();
            } else{
                uiManager.showMessage("There was an error updating The json from the Api");
            }

        } catch (ApiException e) {
            uiManager.showMessage("ERROR sending a request to the Api");
        }
        do{
            option = this.uiManager.askForMainMenu();
            switch (option) {
                case 1 -> firstProductManager = manageProducts();
                case 2 -> {
                    do{
                        //aixo no funciona, no diferencia de si estem amb JSON o amb API
                        try {
                            if (shopManager.workingShopDAO()) {
                                this.manageShops();
                                break;
                            } else if(shopManager.getCurrentDAOType().equals("JSON")){
                                if(shopManager.informShopDao()){
                                    uiManager.showMessage("The shops.json is being created now.");
                                    this.manageShops();
                                } else{
                                    uiManager.showMessage("There was an error creating the shops.json");
                                }
                                break;
                            }
                        } catch (ApiException e) {
                            uiManager.showMessage("ERROR sending a request to the shops Api");
                        }
                        //System.out.println("SI ARRIBA AQUI ALGO S'ESTA GESTIONANT FATAL. EN PRINCIPI NI HAURIA D'ARRIBAR AQUI");
                    }while (true);
                }
                case 3 -> this.searchProducts(firstProductManager);
                case 4 -> this.listsShops(firstProductManager);
                case 5 -> this.yourCart();
                case 6 -> {
                    try {
                        productManager.updateInfoFromProducts();
                        shopManager.updateInfoFromShops();
                    } catch (ApiException e) {
                        uiManager.showMessage("ERROR sending a request to the Api");
                    }
                    this.uiManager.closeShops();
                }
            }

        } while(option != 6);
    }
    /**
     * Displays the contents of the user's cart, allowing them to proceed to check out, clear the cart, or go back.
     */
    private void yourCart() {
        float totalPriceToAdd = 0;
        //boolean addDiscountToIncome = false;
        int cartOption;
        boolean sameShopBuy = false;
        boolean fromNowWorkingThreshold;

        if(cartManager.getCartSize() > 0){
            uiManager.showMessage("Your cart contains the following items:");
            try {

            for(int compt = 0; compt < cartManager.getCartSize(); compt++){
                cartManager.initializeCartArrays();
                fromNowWorkingThreshold = cartManager.shopInThreshold(0);
                if(cartManager.activeThreshold(compt, sameShopBuy) || fromNowWorkingThreshold){
                    uiManager.showMessage("\n\t- \"" + cartManager.getCartProductName(compt) + "\" by \"" + cartManager.getCartProductBrand(compt) + "\"\n\t  Price: " + cartManager.getCartProductDiscountedPrice(compt));
                    totalPriceToAdd += (cartManager.getCartProductPrice(compt)) / (1 + cartManager.getDiscounts(compt));
                    sameShopBuy = cartManager.shopInThreshold(compt);
                    cartManager.setDiscountToShop(compt);
                    //cartManager.setWorkingThreshold(true);
                } else{
                    uiManager.showMessage("\n\t- \"" + cartManager.getCartProductName(compt) + "\" by \"" + cartManager.getCartProductBrand(compt) + "\"\n\t  Price: " + cartManager.getCartProductPrice(compt));
                    totalPriceToAdd += cartManager.getCartProductPrice(compt);
                    sameShopBuy = cartManager.shopInThreshold(compt);
                }

            }

            } catch (ApiException e) {
                uiManager.showMessage("ERROR connecting with the API");
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String formattedTotal = decimalFormat.format(totalPriceToAdd);
            uiManager.showMessage("\nTotal: " + formattedTotal + "\n\n\t1) Checkout\n\t2) Clear cart\n\n\t3) Back");
            cartOption = uiManager.askForInt("\nChoose an option: ", 1, 3);
            switch (cartOption){
                case 1:
                    cartCheckout();
                    //quizas aqui tengo que ponerlo en false
                    break;
                case 2:
                    cartClearOut();
                    break;
                case 3:
            }
        }else{
            uiManager.showMessage("\nThe cart is empty right now.");
        }
    }
    /**
     * Clears all products from the user's cart.
     */
    private void cartClearOut() {
        String confirmationClearCart = uiManager.askForString("Are you sure you want to clear your cart? ");
        do {
            confirmationClearCart = confirmationClearCart.toLowerCase();
            if (!confirmationClearCart.equals("yes") && !confirmationClearCart.equals("no")) {
                confirmationClearCart = uiManager.askForString("\nPlease enter 'yes' or 'no': ");
                confirmationClearCart = confirmationClearCart.toLowerCase();
            }
        } while (!confirmationClearCart.equals("yes") && !confirmationClearCart.equals("no"));
        if(confirmationClearCart.equals("yes")){
            cartManager.removeCartProducts();
            uiManager.showMessage("\nYour cart has been cleared");
        }else{
            uiManager.showMessage("\nThe cart remained as it was");
        }
    }
    /**
     * Initiates the checkout process, updating shop incomes and clearing the user's cart.
     */
    private void cartCheckout() {
        ArrayList<Float> totalIncomeOfShops = null;
        ArrayList<Float> adaptedMoneyForShops = null;
        String confirmationBuyCart = uiManager.askForString("Are you sure you want to checkout? ");
        do {
            confirmationBuyCart = confirmationBuyCart.toLowerCase();
            if (!confirmationBuyCart.equals("yes") && !confirmationBuyCart.equals("no")) {
                confirmationBuyCart = uiManager.askForString("\nPlease enter 'yes' or 'no': ");
                confirmationBuyCart = confirmationBuyCart.toLowerCase();
            }
        } while (!confirmationBuyCart.equals("yes") && !confirmationBuyCart.equals("no"));
        if(confirmationBuyCart.equals("yes")){
                ArrayList<String> shopNames = cartManager.getShopsNames();
            try {
                adaptedMoneyForShops = cartManager.getMoneyForEachShop();
                shopManager.addMoreIncomeToShops(shopNames, adaptedMoneyForShops);
                totalIncomeOfShops = shopManager.getIncomesForShops(shopNames);
            } catch (ApiException e) {
                uiManager.showMessage("ERROR sending a request to the Api");
            }
            uiManager.showMessage("\n");
            for(int compt = 0; compt < shopNames.size(); compt++){
                //ESTA PARTE NO ES CORRECTA DEL TOD0
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String formattedHistoric = decimalFormat.format(totalIncomeOfShops.get(compt));
                DecimalFormat decimalFormatForMoney = new DecimalFormat("#.##");
                String formattedMoney = decimalFormatForMoney.format(adaptedMoneyForShops.get(compt));
                uiManager.showMessage("\"" + shopNames.get(compt) + "\" has earned " + formattedMoney + ", for an historic total of " + formattedHistoric + ".");
                //cartManager.addIncomeForThresholdCount(compt, totalIncomeOfShops.get(compt));
                try {

                    if(cartManager.getThresholdPriceIfNeed(shopNames.get(compt)) && !cartManager.getAlreadySaidShop().contains(shopNames.get(compt))){
                        uiManager.showMessage("You are now a regular at \"" + shopNames.get(compt) + "\".");
                        cartManager.addAlreadySaidShop(shopNames.get(compt));
                    }
                } catch (ApiException e) {
                    uiManager.showMessage("There was an error sending a request to the API");
                }
            }

            cartManager.removeCartProducts();
            uiManager.showMessage("\nYour cart has been cleared");
        }else{
            uiManager.showMessage("\nThe cart remained as it was");
        }
    }
    /**
     * Lists all shops in the user's cart and their corresponding incomes.
     */
    private void listsShops(boolean firstProductManager) {
        try {
            if(shopManager.thereAreShops()){
                ArrayList<String> shopNames = shopManager.getShopInfo("names");
                ArrayList<String> shopYears = shopManager.getShopInfo("years");
                ArrayList<String> shopDescriptions = shopManager.getShopInfo("descriptions");
                ArrayList<String> shopModels = shopManager.getShopInfo("businessModel");
                if(!shopNames.isEmpty()){
                    uiManager.showMessage("The elCofre family is formed by the following shops:");
                    boolean end = false;
                    while(!end) {
                        uiManager.showMessage("\n");
                        for (int comptShops = 0; comptShops < shopNames.size(); comptShops++) {
                            //System.out.println("si no es alla es ki");
                            uiManager.showMessage("\t" + (comptShops + 1) + ") " + shopNames.get(comptShops));
                            if (comptShops == (shopNames.size() - 1)) {
                                uiManager.showMessage("\n\t" + (shopNames.size() + 1) + ") Back");
                                end = shopListOptions(comptShops, shopNames, shopModels, shopYears, shopDescriptions, firstProductManager);
                            }
                        }
                    }
                }
            }else{
                uiManager.showMessage("\nThere are not shops yet.");
            }
        } catch (ApiException e) {
            uiManager.showMessage("ERROR sending a request to the Api");
        }
    }
    /**
     * Provides options for displaying and interacting with the product catalogue of a selected shop.
     *
     * @param comptShops           The index of the shop being processed.
     * @param shopNames            ArrayList containing names of all shops.
     * @param shopYears            ArrayList containing establishment years of all shops.
     * @param shopDescriptions     ArrayList containing descriptions of all shops.
     * @param firstProductManager  Flag indicating if the ProductManager has been initialized.
     * @return True if the user chooses to go back, false otherwise.
     */
    private boolean shopListOptions(int comptShops, ArrayList<String> shopNames, ArrayList<String> shopModels, ArrayList<String> shopYears, ArrayList<String> shopDescriptions, boolean firstProductManager) {
        if (comptShops == (shopNames.size() - 1)) {
            int selectedShop = uiManager.askForInt("\nWhich catalogue do you want to see? ", 1, shopNames.size() + 1);
            try {
                if (!(selectedShop == shopNames.size() + 1) && shopManager.shopHasProducts(shopNames.get(selectedShop - 1))) {
                    uiManager.showMessage(shopNames.get(selectedShop - 1) + " - Since " + shopYears.get(selectedShop - 1) + "\n" + shopDescriptions.get(selectedShop - 1));
                    ArrayList<String> productNamesFromShop = shopManager.getProductInfoFromShop(shopNames.get(selectedShop - 1), "names");
                    ArrayList<String> productBrandsFromShop = shopManager.getProductInfoFromShop(shopNames.get(selectedShop - 1), "brands");
                    ArrayList<String> productPricesFromShop = shopManager.getProductInfoFromShop(shopNames.get(selectedShop - 1), "prices");
                    for (int comptProducts = 0; comptProducts < productNamesFromShop.size(); comptProducts++) {
                        uiManager.showMessage("\n\t" + (comptProducts + 1) + ") \"" + productNamesFromShop.get(comptProducts) + "\" by \"" + productBrandsFromShop.get(comptProducts) + "\"\n\t\tPrice: " + productPricesFromShop.get(comptProducts));
                        if (comptProducts == (productNamesFromShop.size() - 1)) {
                            uiManager.showMessage("\n\t" + (productNamesFromShop.size() + 1) + ") Back\n");
                            return subMenuListShops(productNamesFromShop, productBrandsFromShop, productPricesFromShop, firstProductManager, shopNames, shopModels, selectedShop);
                        }

                    }
                } else {
                    uiManager.showMessage("The selected Shop has no products");
                    return true;
                }
            } catch (ApiException e) {
                uiManager.showMessage("ERROR sending a request to the Api");
            }

        }
        return false;
    }
    /**
     * Manages the display of products in a selected shop and provides options for reading reviews, reviewing a product, or adding it to the cart.
     *
     * @param productNamesFromShop   A list containing names of products in the selected shop.
     * @param productBrandsFromShop  A list containing brands of products in the selected shop.
     * @param productPricesFromShop  A list containing prices of products in the selected shop.
     * @param firstProductManager    A flag indicating whether it is the first product manager.
     * @param shopNames              A list containing names of all available shops.
     * @param selectedShop           The index of the selected shop (1-based index).
     * @return                       A boolean indicating whether the user chose to exit the current shop menu ("true") or not ("false").
     */
    private boolean subMenuListShops(ArrayList<String> productNamesFromShop, ArrayList<String> productBrandsFromShop, ArrayList<String> productPricesFromShop, boolean firstProductManager, ArrayList<String> shopNames, ArrayList<String> shopModels, int selectedShop) {
        boolean finalLoop = false;
        int selectedProductInShop = uiManager.askForInt("Which one are you interested in? ", 1, productNamesFromShop.size() + 1);
        if (selectedProductInShop != productNamesFromShop.size() + 1) {
            uiManager.showMessage("\n\t1) Read Reviews\n\t2) Review Product\n\t3) Add to Cart\n");
            int selectedOption = uiManager.askForInt("Choose an option: ", 1, 3);
            switch (selectedOption) {
                case 1 -> readReviews(productNamesFromShop.get(selectedProductInShop - 1), productBrandsFromShop.get(selectedProductInShop - 1), firstProductManager);
                case 2 -> addReviews(productNamesFromShop.get(selectedProductInShop - 1), productBrandsFromShop.get(selectedProductInShop - 1));
                case 3 -> {
                    addToCart(productNamesFromShop.get(selectedProductInShop - 1), productBrandsFromShop.get(selectedProductInShop - 1), productPricesFromShop.get(selectedProductInShop - 1), shopNames.get(selectedShop - 1), shopModels.get(selectedShop - 1));
                    finalLoop = true;
                }
            }
        } else {
            finalLoop = true;
        }
        return finalLoop;
    }
    /**
     * Adds a specified product to the shopping cart.
     *
     * @param productName  The name of the product to be added to the cart.
     * @param productBrand The brand of the product to be added to the cart.
     * @param productPrice The price of the product to be added to the cart.
     * @param shopName     The name of the shop from which the product is being added.
     */
    private void addToCart(String productName, String productBrand, String productPrice, String shopName, String shopModel) {
        cartManager.addProductToCart(productName, productBrand, productPrice, shopName, shopModel);
        uiManager.showMessage("\n1x \"" + productName + "\" by \"" + productBrand + "\" has been added to your cart.");
    }
    /**
     * Searches for products based on a user-specified query and provides information about the found products.
     *
     * @param firstProductManager Indicates whether the product manager has been initialized for the first time.
     */
    private void searchProducts(boolean firstProductManager) {
        try {
            if(productManager.thereAreProducts()){
                String selectedQuery = uiManager.askForString("Enter your query: ");
                ArrayList<String> posibleProductNames = productManager.getProductsNamesBySimilarities(selectedQuery);
                ArrayList<String> posibleProductBrands = productManager.getProductsBrandsBySimilarities(selectedQuery);
                if(posibleProductNames.isEmpty()){
                    uiManager.showMessage("\nThere are not any products containing this query");
                } else{
                    ArrayList<String> shopNamesForProduct;
                    ArrayList<Float> shopPricesForProduct;
                    uiManager.showMessage("\nThe following products were found: ");
                    for(int comptNames = 0; comptNames < posibleProductNames.size(); comptNames++){
                        shopNamesForProduct = shopManager.getShopNamesForProduct(posibleProductNames.get(comptNames));
                        shopPricesForProduct = shopManager.getShopPricesForProduct(posibleProductNames.get(comptNames));
                        uiManager.showMessage("\n\t" + (comptNames+1) + ") \"" + posibleProductNames.get(comptNames) + "\" by \"" + posibleProductBrands.get(comptNames) + "\"");
                        if(shopNamesForProduct == null){
                            uiManager.showMessage("This product is not currently being sold in any shops.");
                        }else if(!shopNamesForProduct.isEmpty()){
                            uiManager.showMessage("\t\tsold at:");
                            for(int comptShops = 0; comptShops < shopNamesForProduct.size(); comptShops++){
                                uiManager.showMessage("\t\t\t- " + shopNamesForProduct.get(comptShops) + ": " + shopPricesForProduct.get(comptShops));
                            }
                        } else{
                            uiManager.showMessage("This product is not currently being sold in any shops.");
                        }
                        if(comptNames == posibleProductNames.size()-1){
                            uiManager.showMessage("\n\t" + (comptNames+2) + ") Back");
                            boolean goBack = subMenuSearchProduct(comptNames, posibleProductNames, posibleProductBrands, firstProductManager);
                            if(goBack){
                                return;
                            }
                        }
                    }
                }
            }else{
                uiManager.showMessage("\nThere are no products yet.");
            }
        } catch (ApiException e) {
            uiManager.showMessage("Error sending a request to the products endpoint");
        }
    }
    /**
     * Manages the search for a product and provides options for reviewing or reading reviews.
     *
     * @param comptNames            The number of available products.
     * @param possibleProductNames  A list containing names of possible products.
     * @param possibleProductBrands A list containing brands of possible products.
     * @param firstProductManager   A flag indicating whether it is the first product manager.
     * @return                      A boolean indicating whether the user chose to go back to the main menu ("true") or not ("false").
     */
    private boolean subMenuSearchProduct(int comptNames, ArrayList<String> possibleProductNames, ArrayList<String> possibleProductBrands, boolean firstProductManager) {
        int selectedProduct = uiManager.askForInt("Which one would you like to review? ", 1, (comptNames+2));
        boolean goBack = false;
        if(selectedProduct == (comptNames+2)){
            uiManager.showMessage("Going back to main menu");
        } else{
            uiManager.showMessage("\n\t1) Read Reviews\n\t2) Review Product\n\n");
            int selectedOption = uiManager.askForInt("Choose an option: ", 1, 2);
            switch (selectedOption) {
                case 1 ->
                        readReviews(possibleProductNames.get(selectedProduct - 1), possibleProductBrands.get(selectedProduct - 1), firstProductManager);
                case 2 ->
                        addReviews(possibleProductNames.get(selectedProduct - 1), possibleProductBrands.get(selectedProduct - 1));
                default -> goBack = true;
            }
        }
        return goBack;
    }
    /**
     * Adds a review for a specific product, including a rating and a comment.
     *
     * @param productSelected The name of the product for which the review is being added.
     * @param brandSelected The brand of the product for which the review is being added.
     */
    private void addReviews(String productSelected, String brandSelected) {
        String rating = uiManager.askForString("Please rate the product (1-5 stars): ");
        if(productManager.correctRating(rating)){
            String review = uiManager.askForString("Please add a comment to your review: ");
            try {
                productManager.addReviewToProduct(productSelected, rating, review);
                shopManager.addReviewToProductInShop(productSelected, rating, review);
            } catch (ApiException e) {
                uiManager.showMessage("ERROR sending a request to the Api");
            }

            uiManager.showMessage("\nThank you for your review of \"" + productSelected + "\" by \"" + brandSelected + "\".");
        }else{
            uiManager.showMessage("The rating is not in a correct format!");
        }
    }
    /**
     * Displays the reviews for a specific product, including the average rating.
     *
     * @param productSelected The name of the product for which the reviews are being displayed.
     * @param brandSelected The brand of the product for which the reviews are being displayed.
     * @param firstProductManager A boolean indicating whether this is the first product manager.
     */
    private void readReviews(String productSelected, String brandSelected, boolean firstProductManager) {
        ArrayList<String> reviews = null;
        try {
            reviews = productManager.getProductReviews(productSelected);
        } catch (IncorrectRequestException e) {
            uiManager.showMessage(e.getMessage());
        }
        try {
            if(!reviews.isEmpty() || shopManager.productInAnyCatalogue(productSelected) && firstProductManager){
                uiManager.showMessage("\nThese are the reviews for \"" + productSelected + "\" by \"" + brandSelected + "\": \n");
                for(int comptReviews = 0; comptReviews < reviews.size(); comptReviews++){
                    uiManager.showMessage("\t" + reviews.get(comptReviews));
                }
                uiManager.showMessage("\n\tAverage rating: " + productManager.getAverageReview(productSelected) + "*");
            } else{
                uiManager.showMessage("\nThere is not available information for this request");
            }
        } catch (ApiException e) {
            uiManager.showMessage("ERROR sending a request to the Api");
        }

    }
    /**
     * Manages various operations related to shops, providing options for creating, expanding, and reducing shop catalogues.
     * This method continues to prompt the user for shop management options until the user chooses to exit.
     */
    private void manageShops() {
        int option;
        boolean end = false;
        do{
            option = uiManager.askForManageShopMenu();
            switch (option) {
                case 1 -> end = createShop();
                case 2 -> {
                    end = expandShopCatalogue();
                }
                case 3 -> {
                    reduceShopCatalogue();
                }
                case 4 -> {
                    return;
                }
            }
        } while(end);
    }
    /**
     * Reduces the catalogue of a shop by removing a product from its inventory.
     *
     */
    private void reduceShopCatalogue() {
        boolean error = false;
        try {
            if(shopManager.workingShopDAO()){
                String shopName = uiManager.askForString("Please enter the shop's name: ");
                try {
                    error = shopManager.correctShopName(shopName);
                } catch (ApiException e) {
                    uiManager.showMessage("ERROR sending a request to the Api");
                }
                if(error){
                    uiManager.showMessage("This shop does not exist.");
                } else{
                    ArrayList<String> productsNamesLists = null;
                    ArrayList<String> productsBrandLists = null;
                    try {
                        productsNamesLists = shopManager.getProductInfoFromShop(shopName, "names");
                        productsBrandLists = shopManager.getProductInfoFromShop(shopName, "brands");
                    } catch (ApiException e) {
                        uiManager.showMessage("ERROR sending a request to the shops persistence");
                    }
                    if(productsNamesLists.size() != 0){
                        uiManager.showMessage("\nThese are the currently available products:\n");
                        for(int comptProductsNamesLists = 0; comptProductsNamesLists < productsNamesLists.size(); comptProductsNamesLists++){
                            uiManager.showMessage("\t" + (comptProductsNamesLists+1) + ") \"" + productsNamesLists.get(comptProductsNamesLists) + "\" by \"" + productsBrandLists.get(comptProductsNamesLists) + "\"");
                        }
                        uiManager.showMessage("\n\t" + (productsNamesLists.size()+1) + ") Back");
                        int productToRemoveFromShop = uiManager.askForInt("\nWhich one wouLd you like to remove? ",1, productsNamesLists.size()+1);
                        if(productToRemoveFromShop != productsNamesLists.size()+1){
                            uiManager.showMessage("\n\"" + productsNamesLists.get(productToRemoveFromShop-1) + "\" by \"" + productsBrandLists.get(productToRemoveFromShop-1) + "\" is no longer being sold at \"" + shopName + "\".");
                            try {
                                shopManager.removeProductFromShop(productsNamesLists.get(productToRemoveFromShop-1), shopName);
                            } catch (ApiException e) {
                                uiManager.showMessage("ERROR sending a request to the Api");
                            }
                            cartManager.removeProductFromCart(productsNamesLists.get(productToRemoveFromShop-1), shopName);
                        }
                    }else{
                        uiManager.showMessage("\nThere are no products yet in this shop");
                    }
                }
            }else{
                uiManager.showMessage("There are not shops registered yet.");
            }
        } catch (ApiException e) {
            uiManager.showMessage("ERROR getting access to the shops persistence");
        }
    }
    /**
     * Expands the catalogue of a shop by adding a new product along with its price. The method prompts the user to enter
     * the shop's name, the product's name, and the product's price at the shop. It then performs various checks to ensure
     * the validity of the input and adds the product to the shop's inventory.
     *
     * @return true if the operation is successful or if the user chooses to go back; false otherwise.
     */
    private boolean expandShopCatalogue(){
        ArrayList<String> productNames = null;
        ArrayList<Float> productMaxPrices = null;
        try {
            productNames = productManager.getProductNames();
            productMaxPrices = productManager.getProductMaxPrices();
        } catch (ApiException e) {
            uiManager.showMessage("ERROR sending a request to the products persistence");
        }
        boolean error = false;
        try {
            if(shopManager.workingShopDAO()){
                String shopName = uiManager.askForString("Please enter the shop's name: ");
                try {
                    error = shopManager.correctShopName(shopName);
                } catch (ApiException e) {
                    uiManager.showMessage("ERROR sending a request to the Api");
                }
                if(error){
                    uiManager.showMessage("This shop does not exist.");
                    return true;
                } else{
                    String productName = uiManager.askForString("Please enter the product's name: ");
                    error = shopManager.insertAProduct(productName, productNames);
                    if(error){
                        uiManager.showMessage("This product does not exist");
                        return true;
                    }else{
                        String price = uiManager.askForString("Please enter the product's price at this shop: ");
                        price = price.replace(",",".");
                        error = shopManager.insertAPriceForProduct(productName, price, productNames, productMaxPrices);
                        if(error){
                            uiManager.showMessage("This price is not available.");
                            return true;
                        }else{
                            String productBrand = null;
                            ArrayList<String> reviewsFromProduct = null;
                            try {
                                productBrand = productManager.getBrandForProduct(productName);
                                reviewsFromProduct = productManager.getProductReviews(productName);
                            } catch (ApiException e) {
                                uiManager.showMessage("ERROR sending a request to the Api");
                            }
                            try {
                                error = shopManager.addProductToShop(shopName, productName, price, productBrand, reviewsFromProduct);
                                if(error){
                                    //if(shopManager.alreadyInShopButDiferentPrice(shopName, productName, price)){
                                        uiManager.showMessage("There cant be different prices for the same product in a shop.");
                                    /*}else{
                                        uiManager.showMessage("\"" + productName + "\" is now a part of " + shopName + "\n");
                                    }*/
                                }else{
                                    uiManager.showMessage("\"" + productName + "\" is now a part of " + shopName + "\n");
                                }
                            } catch (ApiException e) {
                                uiManager.showMessage("ERROR sending a request to the Api");
                            }
                        }
                    }
                }
            } else{
                uiManager.showMessage("There are not shops registered yet.");
                return true;
            }
        } catch (ApiException e) {
            uiManager.showMessage("ERROR getting access to the Shop persistence");
        }

        return error;
    }
    /**
     * Creates a new shop by collecting information such as the shop's name, description, and founding year from the user.
     *
     * @return true if the operation is unsuccessful or if the user chooses to go back; false otherwise.
     */
    private boolean createShop() {
        ArrayList<String> shopAttributes = new ArrayList<>();

        String atributPosible = uiManager.askForString("Please enter the shop's name: ");
        boolean atributCorrecte = false;
        try {
            atributCorrecte = shopManager.correctShopName(atributPosible);
        } catch (ApiException e) {
            uiManager.showMessage("ERROR sending a request to the Api");
        }
        if(atributCorrecte){
            shopAttributes.add(atributPosible);
            atributPosible = uiManager.askForString("Please enter the shop's description: ");
            shopAttributes.add(atributPosible);
            atributPosible = uiManager.askForString("Please enter the shop's founding year: ");
            do{
                atributCorrecte = shopManager.correctShopYear(atributPosible);
                if(!atributCorrecte){
                    uiManager.showMessageWithNoSpace("The selected attribute is not correct\n");
                    atributPosible = uiManager.askForString("Please enter the shop's founding year: ");
                }
            } while(!atributCorrecte);
            shopAttributes.add(atributPosible);
            uiManager.showMessageWithNoSpace("\nThe system supports the following business models:\n\n\tA)\tMaximum Benefits\n\tB)\tLoyalty\n\tC)\tSponsored\n");
            String model = uiManager.askForSelectedStrings(false, "\nPlease pick the shop's business model: ");
            String extraShopAttribute = correcteExtraShopAttribute(model);
            try {
                shopManager.addShop(shopAttributes, model, extraShopAttribute);
                uiManager.showMessage("\n\"" + shopAttributes.get(0) +"\" is now a part of the elCofre family");
            } catch (ApiException e) {
                uiManager.showMessage("ERROR sending a request to the Api");
            }
            return false;
        } else{
            uiManager.showMessage("That is not a valid name");
            return true;
        }
    }
    /**
     * Corrects and retrieves extra attributes based on the shop model.
     *
     * @param model The model of the shop (e.g., "LOYALTY", "SPONSORED").
     * @return A string representation of the corrected extra attribute based on the model.
     */
    private String correcteExtraShopAttribute(String model) {
        switch (model) {
            case "LOYALTY":
                float loyaltyThreshold = uiManager.askForFloat("\nPlease enter the shop's loyalty threshold: ");
                return String.valueOf(loyaltyThreshold);
            case "SPONSORED":
                String sponsorBrand = uiManager.askForString("\nPlease enter the shop's sponsoring brand: ");
                sponsorBrand = uiManager.adaptStringToFirstMayus(sponsorBrand);
                /*ArrayList<String> productBrands = null;
                try {
                    productBrands = productManager.getProductBrand();
                } catch (ApiException e) {
                    e.getMessage();
                }
                while (true) {
                    if (productBrands.contains(sponsorBrand)) {
                        break;
                    } else {
                        sponsorBrand = uiManager.askForString("That is not an existent brand, could you spell it again?");
                    }
                }*/
                return sponsorBrand;
            default:
                return "null";
        }
    }
    /**
     * Allows the user to manage products by presenting a menu with options to create a new product, remove an existing product,
     * or return to the main menu. The method continues to prompt the user for input until the user chooses to return to the
     * main menu.
     *
     * @return true if the operation is unsuccessful or if the user chooses to go back; false otherwise.
     */
    private boolean manageProducts() {
        int option;
        boolean end = false;
        do{
            option = uiManager.askForManageProductMenu();
            switch (option) {
                case 1 -> end = this.createProduct();
                case 2 -> end = this.removeProduct();
                case 3 -> {
                    return end;
                }
            }
        } while(end);
        return true;
    }
    /**
     * Presents the user with a list of currently available products and allows them to choose a product for removal.
     *
     * @return true if the operation is unsuccessful or if the user chooses to go back; false otherwise.
     */
    private boolean removeProduct() {
        uiManager.showMessage("\nThese are the currently available products:\n");
        ArrayList<String> productsNamesLists = null;
        ArrayList<String> productsBrandLists = null;
        try {
            productsNamesLists = productManager.getProductNames();
            productsBrandLists = productManager.getProductBrand();
        } catch (ApiException e) {
            uiManager.showMessage("ERROR sending a request to the Api");
        }
        if(productsBrandLists.size() > 0){
            for(int compt = 0; compt < productsBrandLists.size(); compt++){
                uiManager.showMessage("\t" + (compt+1) + ") \""+ productsNamesLists.get(compt) + "\" by \"" + productsBrandLists.get(compt) + "\"");
                if(compt == productsBrandLists.size()-1){
                    uiManager.showMessage("\t" + (productsBrandLists.size()+1) + ") Back\n");
                    int option = uiManager.askForInt("Which one would you like to remove? ", 1, (productsBrandLists.size()+1));
                    if(option != productsBrandLists.size()+1){
                        String response = uiManager.askForString("\nAre you sure you want to remove \"" + productsNamesLists.get(option-1) + "\" by \"" + productsBrandLists.get(option-1) + "\"? ");
                        response = response.toLowerCase();
                        boolean noRemovedProduct = false;
                        noRemovedProduct = subMenuRemoveProduct(response, option, productsNamesLists, productsBrandLists);
                        if(noRemovedProduct){
                            return true;
                        }
                    }
                    else{
                        uiManager.showMessage("\nNo product was removed");
                        return true;
                    }
                } else if (compt == productsBrandLists.size()) {
                    return true;
                }
            }
        }else{
            uiManager.showMessage("There are not products yet");
        }
        return false;
    }
    /**
     * Manages the removal of a product based on user input.
     *
     * @param response             The user's response to the removal prompt ("yes" or "no").
     * @param option               The index of the product in the lists (1-based index).
     * @param productsNamesLists   A list containing names of products.
     * @param productsBrandLists   A list containing brands of products.
     * @return                     A boolean indicating whether the product was not removed ("true") or removed ("false").
     */
    private boolean subMenuRemoveProduct(String response, int option, ArrayList<String> productsNamesLists, ArrayList<String> productsBrandLists) {
        boolean noRemovedProduct = false;
        do{
            if(response.equals("yes")){

                try {
                    productManager.eraseProduct(productsNamesLists.get(option-1));
                } catch (ApiException e) {
                    uiManager.showMessage("ERROR sending a request to the Api");
                }
                try {
                    shopManager.removeProductFromAllShops(productsNamesLists.get(option-1));
                } catch (ApiException e) {
                    uiManager.showMessage("ERROR sending a request to the Api");
                }
                cartManager.removeProductFromCart(productsNamesLists.get(option-1), null);
                uiManager.showMessage("\n\"" + productsNamesLists.get(option-1) + "\" by \"" + productsBrandLists.get(option-1) + "\" has been withdrawn from sale.");
                break;
            } else if(response.equals("no")) {
                removeProduct();
                noRemovedProduct = true;
                break;
            } else{
                response = uiManager.askForString("\nThat is not a valid answer, do you want to remove \"" + productsNamesLists.get(option-1) + "\" by \"" + productsBrandLists.get(option-1) + "\"? ");
                response = response.toLowerCase();
                if(response.equals("yes")){
                    try {
                        productManager.eraseProduct(productsNamesLists.get(option-1));
                    } catch (ApiException e) {
                        uiManager.showMessage("ERROR sending a request to the Api");
                    }
                    uiManager.showMessage("\n\"" + productsNamesLists.get(option-1) + "\" by \"" + productsBrandLists.get(option-1) + "\" has been withdrawn from sale.");
                    break;
                }
            }
        } while(true);
        return noRemovedProduct;
    }
    /**
     * Guides the user through the process of creating a new product by collecting relevant attributes such as name,
     * brand, maximum retail price, and category. The user's input is validated at each step to ensure correctness.
     * If the input is valid, the new product is added to the product manager with the specified attributes and category.
     *
     * @return true if the operation is unsuccessful or if the user chooses to go back; false otherwise.
     */
    private boolean createProduct() {
        ArrayList<String> productAttributes = new ArrayList<>();
        String atributPosible = uiManager.askForString("Please enter the product's name: ");
        boolean atributCorrecte = false;
        try {
            atributCorrecte = productManager.correctProductName(atributPosible);
        } catch (ApiException e) {
            uiManager.showMessage("ERROR sending a request to the Api");
        }
        if (atributCorrecte) {
            productAttributes.add(atributPosible);
            atributPosible = uiManager.askForString("Please enter the product's brand: ");
            productAttributes.add(productManager.correctProductBrand(atributPosible));
            atributPosible = Float.toString(uiManager.askForFloat("Please enter the Product's maximum retail price: "));
            do {
                atributCorrecte = productManager.correctProductPriceMax(atributPosible);
                if (!atributCorrecte) {
                    uiManager.showMessageWithNoSpace("The number can't be negative\n");
                    atributPosible = Float.toString(uiManager.askForFloat("Please enter the Product's maximum retail price: "));
                }
            } while (!atributCorrecte);
            productAttributes.add(atributPosible);
            uiManager.showMessageWithNoSpace("\nThe system supports the following product categories:\n\n\tA)\tGeneral\n\tB)\tReduced Taxes\n\tC)\tSuperreduced Taxes\n");
            String category = uiManager.askForSelectedStrings(true, "\nPlease pick the product's category: ");
            try {
                productManager.addProduct(productAttributes, category);
                uiManager.showMessage("\nThe product \"" + productAttributes.get(0) + "\" by \"" + productAttributes.get(1) + "\" was added to the system");
            } catch (ApiException e) {
                uiManager.showMessage("ERROR sending a request to the Api");
            }
            return false;
        } else {
            uiManager.showMessage("That is not a valid name");
            return true;
        }
    }
}