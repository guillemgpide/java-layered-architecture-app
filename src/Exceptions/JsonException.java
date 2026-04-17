package Exceptions;

import com.google.gson.JsonParseException;

import java.io.IOException;
/**
 * Custom exception class for handling JSON-related exceptions in the ProductJsonDAO.
 *
 * This exception is thrown when there is a problem parsing or processing JSON data.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public final class JsonException extends JsonParseException {
    /**
     * Constructs a new JsonException with the specified error message.
     *
     * @param message The detail message for the exception.
     */
    public JsonException(String message){
        super(message);
    }
}
