package client;
/**
 * @author pawelwuuu
 * Request that is sending to the database server in form of query. It contains information about type of command that
 * have to be performed, key of the database cell and value which have to be set (in case of set command).
 */
public class Request {
    private final String type;
    private final Object value;
    private final Object key;
    /**
     * Constructs a request with the given parameters, value could be null in case of types other than set.
     * @param type type of command.
     * @param key key of the database cell.
     * @param value value to set in database cell.
     */
    public Request(String type, Object key, Object value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }
    /**
     * Returns a key of the request.
     * @return String with the key.
     */
    public Object getKey() {
        return key;
    }
    /**
     * Returns value of the request.
     * @return Object which is value of the request.
     */

    public Object getValue() {
        return value;
    }

    /**
     * Returns type of the request.
     * @return type of the request.
     */
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type='" + type + '\'' +
                ", value=" + value +
                ", key=" + key +
                '}';
    }
}
