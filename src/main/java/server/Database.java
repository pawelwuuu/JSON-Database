package server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Exceptions.DatabaseKeyException;

/**
 * @author pawelwuuu
 * Server database representation, offers methods to work on the data stored in the database file, also read and save
 * data to it. Database works only in format of JSON.
 */
public class Database {
    HashMap<Object, Object> cells;
    //TODO add implementation to specify the filename for a database
    /**
     * Constructs the new database.
     */
    public Database() {
        this.cells = new HashMap<>();
    }

    /**
     * Sets the supplied data into the cell with specified key, also returns response object of the performed action.
     * @param key object key of the cell.
     * @param valueToSet object value of the cell.
     * @return response containing information if the request was performed without errors, also the requested value.
     */
    public Response setCell(Object key, Object valueToSet){
        cells.put(key, valueToSet);

        return new Response("OK", null, null);
    }

    /**
     * Deletes the specified by a key database cell, also returns response object of the performed action.
     * @param key object key of the cell.
     * @return response containing information if the request was performed without errors.
     */
    public Response deleteCell(Object key){
        try {
            if (! cells.containsKey(key)){
                throw new DatabaseKeyException("No such key");
            }

            cells.remove(key);

            return new Response("OK", null, null);
        } catch (Throwable e){
            return new Response("ERROR", null, e.getMessage());
        }
    }

    /**
     * Gets information stored in the specified by a key database cell, and returns response object of the performed action
     * @param key key object key of the cell.
     * @return response containing information if the request was performed without errors, also containing demanded value.
     */
    public Response getCell(Object key){
        try{
            if (!cells.containsKey(key)){
                throw new DatabaseKeyException("No such key");
            }

            Object value = cells.get(key);
            return new Response("OK", value, null);
        } catch (Throwable e){
            return new Response("ERROR", null, e.getMessage());
        }

    }

}

