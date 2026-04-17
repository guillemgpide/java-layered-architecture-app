package Exceptions;

import java.io.IOException;
/**
 * Custom exception class for handling directory creation-related exceptions in the ProductJsonDAO.
 *
 * This exception is thrown when there is a problem creating a directory.
 *
 * @author Bruno Bordoy, Guillem Gil
 * @version 13/11/2023
 */
public final class CreateDirectoryException extends IOException {
    /**
     * Constructs a new CreateDirectoryException with the specified error message.
     *
     * @param message The detail message for the exception.
     */
    public CreateDirectoryException(String message){
        super(message);
    }
}
