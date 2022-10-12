package server;

import client.Request;
import com.google.gson.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static jsonUtils.JsonManager.*;

/**
 * @author pawelwuuu
 * Manges incoming request from the clients, sends back demanded information, provides multithreaded use of the database.
 */
public class ServerService {
    //TODO add implementation of the custom port and address
    static int port = 38500;
    static String address = "127.0.0.1";
    ServerSocket server;
    Database database;
    boolean isOn = true;
    String pathToDb;
    int avalaibleRequestId;

    /**
     * Constructs the server service with specified path to database.
     * @param pathToDb string containing path to database.
     */
    public ServerService(String pathToDb) throws IOException {
        try {
            this.server = new ServerSocket(port, 50, InetAddress.getByName(address));
            this.pathToDb = pathToDb;
            this.avalaibleRequestId = 0;
            System.out.println("Server started!");

        } catch (Throwable e){
            throw e;
        }
    }

    /**
     * Handles request from many clients (gets request, sends back responses), uses multithreading.
     * After exit command shuts down all the threads immediately. This method is working till the exit command is provided.
     */
    public void handleMultipleRequests(){
        ExecutorService executor = Executors.newFixedThreadPool(4);
        while (isOn){
            try {
                Socket socket = server.accept();

                avalaibleRequestId++;

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        handleRequest(socket, avalaibleRequestId);
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        executor.shutdownNow();
    }

    /**
     * Synchronized method that handles request from the client (gets request, sends back responses).
     * @param acceptedSocket socket that was provided with the certain client request.
     */
    public synchronized void handleRequest(Socket acceptedSocket, int id){
        try (Socket socket = acceptedSocket;
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream())) {

            database = new Gson().fromJson(new FileReader(pathToDb), Database.class);

            if (database == null){
                database = new Database();
            }

            String requestString = input.readUTF();
            Request request = objectDeserialization(requestString, Request.class);

            System.out.println("Request with id [" + id + "]: " + request); //info about request

            Object key = request.getKey();
            String requestType = request.getType();

            ReadWriteLock lock = new ReentrantReadWriteLock();
            Lock readLock = lock.readLock();
            Lock writeLock = lock.writeLock();

            Response response = null;


                switch (requestType){
                    case "set":
                        if (key instanceof ArrayList<?>){
                            readLock.lock();
                            output.writeUTF(objectSerialization(setJsonField(request)));
                            readLock.unlock();

                            break;
                        }

                        Object valueToSet = request.getValue();

                        writeLock.lock();
                        response = database.setCell(key, valueToSet);
                        writeDatabaseToFile();
                        writeLock.unlock();
                        break;

                    case "delete":
                        if (key instanceof ArrayList<?>){
                            readLock.lock();
                            output.writeUTF(objectSerialization(deleteJsonField(request)));
                            readLock.unlock();

                            break;
                        }

                        writeLock.lock();
                        response = database.deleteCell(key);
                        writeDatabaseToFile();
                        writeLock.unlock();
                        break;

                    case "get":
                        if (key instanceof ArrayList<?>){
                            readLock.lock();
                            output.writeUTF(objectSerialization(getJsonField(request)));
                            readLock.unlock();

                            break;
                        }

                        readLock.lock();
                        response = database.getCell(key);
                        readLock.unlock();
                        break;

                    case "exit":
                        response = new Response("OK", null, null);
                        output.writeUTF(objectSerialization(response));

                        server.close();
                        isOn = false;
                        break;

                    default:
                        response = new Response(null, null, null);

                }


            System.out.println("Response to request with id [" + id + "]: " + response.response);
            output.writeUTF(objectSerialization(response));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes database to file specified by provided path to database (in constructor).
     */
    private void writeDatabaseToFile(){
        String serializedDatabase = objectSerialization(database);

        try (FileWriter fileWriter = new FileWriter(pathToDb)){

            fileWriter.write(serializedDatabase);
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * Gets the certain field of an object that is demanded in request from client and returns it as response object.
     * For example if a class named Foo is stored in the database and contains fields named day request will look like this
     * {"type" : "get", "key" : ["Foo","day"]} . Method will only return the value of the day.
     * It is also saving changes to database file.
     * @param request
     * @return response containing information if the request was performed without errors, also the requested value.
     */
    Response getJsonField(Request request){
        String jsonString = objectSerialization(database);
        ArrayList<String> keys = (ArrayList<String>) request.getKey();

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        JsonElement jsonElement = jsonObject.get("cells");
        for (int i = 0; i < keys.size(); i++) {
            jsonElement = jsonElement.getAsJsonObject().get(keys.get(i));
        }

        return new Response("OK", jsonElement, null);
    }

    /**
     * Sets the certain field of an object that is demanded in request from client and returns response object.
     * For example if a class named Foo is stored in the database and contains fields named day request will look like this
     * {"type" : "set", "key" : ["Foo","day"], "value" : "monday"} . Method will only set the value of the day.
     * It is also saving changes to database file.
     * @param request request that was sent by a client
     * @return response containing information if the request was performed without errors.
     */
    Response setJsonField(Request request){
        String jsonString = objectSerialization(database);
        ArrayList<String> keys = (ArrayList<String>) request.getKey();

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        JsonElement jsonElement = jsonObject.get("cells");
        for (int i = 0; i < keys.size() - 1; i++) {
            jsonElement = jsonElement.getAsJsonObject().get(keys.get(i));
        }

        JsonObject referenceToCell = jsonElement.getAsJsonObject();
        referenceToCell.addProperty(keys.get(keys.size() - 1), (String) request.getValue());

        database = new Gson().fromJson(jsonObject, Database.class);
        writeDatabaseToFile();

        return new Response("OK", null, null);
    }

    /**
     * Deletes the certain field of an object that is demanded in request from client and returns response object.
     * For example if a class named Foo is stored in the database and contains fields named day request will look like this
     * {"type" : "delete", "key" : ["Foo","day"]} . Method will only delete the value of the day. It is also saving changes
     * to database file.
     * @param request that was sent by a client.
     * @return response containing information if the request was performed without errors.
     */
    Response deleteJsonField(Request request){
        String jsonString = objectSerialization(database);
        ArrayList<String> keys = (ArrayList<String>) request.getKey();

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        JsonElement jsonElement = jsonObject.get("cells");
        for (int i = 0; i < keys.size() - 1; i++) {
            jsonElement = jsonElement.getAsJsonObject().get(keys.get(i));
        }

        JsonObject referenceToCell = jsonElement.getAsJsonObject();
        referenceToCell.remove(keys.get(keys.size() - 1));

        database = new Gson().fromJson(jsonObject, Database.class);
        writeDatabaseToFile();

        return new Response("OK", null, null);
    }

}
