package client;

import server.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

import static jsonUtils.JsonManager.*;

/**
 * @author pawelwuuu
 * Client side main class, provides communication with the server side via sockets. Creates sockets, sends, gets sockets and
 * prints incoming server responses into the console output.
 */
public class Client {
    private final String requestType;
    private final Object key;
    private final Object valueToSet;
    private Socket socket;
    private final String fileName;
    private final String pathToRequest;

    /**
     * Constructs client with all it's fields.
     * @param requestType type of the request.
     * @param key key of the database's cell.
     * @param valueToSet object containing value to set in the cell.
     */
    public Client(String requestType, Object key, Object valueToSet,  String fileName, String ip) {
        this.fileName = fileName;
        this.requestType = requestType;
        this.valueToSet = valueToSet;
        this.key = key;

        pathToRequest = System.getProperty("user.dir") + File.separator + fileName;

        try {
            this.socket = new Socket(ip, 38500);
            System.out.println("Client started!");

        }catch (ConnectException e){
            System.out.println("Connection refused, check if ip is correct.");
            System.exit(0);
        }catch (Throwable e){
            System.out.println("Error! " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Constructs client that have get, delete or any command that doesn't need value or a file name as a type of request.
     * @param requestType type of the request.
     * @param key key of the database's cell.
     */
    public Client(String requestType, Object key, String ip) {
        this(requestType, key, null, null, ip);
    }

    /**
     * Constructs a client that is using file to send a request.
     * @param fileName String containing name of the file.
     */
    public Client(String fileName, String ip){
        this(null, null, null, fileName, ip);
    }

    /**
     * Sends a request to the database server
     */
    public void sendRequest(){
        Request request = new Request(null, null, null);

        try{
            if (fileName != null){
                request = objectDeserialization(loadRequestFromFile(), Request.class);
            } else if (valueToSet != null){
                request = new Request(requestType, key, valueToSet);
            } else {
                request = new Request(requestType, key, null);
            }
        } catch (Throwable e){
            e.printStackTrace();
        }



        try{
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            String serializedRequest = objectSerialization(request);

            output.writeUTF(serializedRequest);
            System.out.println("Sent: " + serializedRequest);
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * Returns response from server or other computer.
     * @return string containing request from other computer.
     */
    public String getResponse(){
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());

            return input.readUTF();
        } catch (Throwable e){
            e.printStackTrace();
        }

        return "CLIENT SIDE FAILED";
    }

    /**
     * Gets and prints incoming data into console output.
     */
    public void printIncomingData(){
        Response response = objectDeserialization(getResponse(), Response.class);
        System.out.println("Received:\n" + response);
    }

    /**
     * Closes connection with another computer.
     */
    private void closeConnection(){
        try{
            socket.close();
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * Initiates client, more specific, sends a request passed with the console arguments, prints incoming
     * data and closes connection with the server.
     */
    public void init(){
        sendRequest();
        printIncomingData();
        closeConnection();
    }

    /**
     * Loads request that is in a specified file.
     * @return string containing request from a file.
     */
    private String loadRequestFromFile(){
        String parsedJson = "";

        try {
            parsedJson = Files.readString(Path.of(pathToRequest));
        } catch (Throwable e){
            e.printStackTrace();
        }

        return parsedJson;
    }

}
