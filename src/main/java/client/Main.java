package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    @Parameter(names = "-t", description = "Type of request which has to be performed")
    String requestType;

    @Parameter(names = "-k", description = "Index of database cell")
    String key;

    @Parameter(names = "-v", description = "Value to set in the certain database cell")
    String setValue = null;

    @Parameter(names = "-in", description = "Name of file which contains requests, it should be placed in data folder")
    String filename;

    @Parameter(names = "-ip", description = "Ip of server")
    String ip;

    @Parameter(names = "-f", description = "Ip of server")
    List list = new ArrayList<String>();

    public static void main(String[] args) {

        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);

        main.run();
    }

    public void run(){
        Client client;

        if (ip == null){
            String path = System.getProperty("user.dir") + File.separator + "ip.txt";

            File f = new File(path);
            if (f.exists()){
                try {
                    Scanner scanner = new Scanner(f);
                    ip = scanner.nextLine();
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Put ip into ip.txt file in the same folder as jar or define it by -ip parameter.");
                return;
            }
        }


        if (filename != null){
            client = new Client(filename, ip);
        } else if (setValue != null) {
            if (list.size() > 0){
                client = new Client(requestType, list, setValue, null, ip);
            } else {
                client = new Client(requestType, key, setValue, null, ip);
            }
        } else {
            if (list.size() > 0){
                client = new Client(requestType, list, ip);
            } else {
                client = new Client(requestType, key, ip);
            }
        }

        client.init();
    }
}
