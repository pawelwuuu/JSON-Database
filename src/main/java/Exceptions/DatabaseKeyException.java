package Exceptions;

/**
 * @author pawelwuuu
 * Points that, operation in the database that is associated with supplied key of the cell gone wrong.
 */
public class DatabaseKeyException extends RuntimeException{
    public DatabaseKeyException(String message) {
        super(message);
    }
}
