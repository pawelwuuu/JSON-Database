package server;

/**
 * @author pawelwuuu
 * Representation of the server response to the client request. Contains information about errors of the performed action,
 * also contains demanded value.
 */
public class Response {
    String response, reason;
    Object value;

    /**
     * Constructs response that contains all information needed to send back to client.
     * @param response string containing information if the client request succeeded.
     * @param value object containing value demanded by the request.
     * @param reason string containing the reason of the error which occurred in database.
     */

    public Response(String response, Object value, String reason) {
        this.response = response;
        this.value = value;
        this.reason = reason;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Server response: " + response + '\n' +
                "Reason: " + reason + '\n' +
                "Value: " + value;
    }
}
