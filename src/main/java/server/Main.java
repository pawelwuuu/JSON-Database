package server;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Database database = new Database();

        String path = System.getProperty("user.dir") + File.separator + "database.db";
        File f = new File(path);
        try {
            f.createNewFile();
            if (! f.exists()){
                System.out.println("Did not find database file, it has been created in " + path);
            }
        } catch (IOException e) {
            System.out.println("Problem with file creation occurred. Try running programme with admin permissions.");
        }

        ServerService serverService = null;
        try {
            serverService = new ServerService(path);
        } catch (IOException e) {
            System.out.println("Error occurred! " + e.getMessage());
            return;
        }
        serverService.handleMultipleRequests();
    }
}
