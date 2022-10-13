# Json-Database
Json database is an application that gives possibility to store data and manipulate stored data. It consist of client and server. Server is managing data by sotring, deleteing and returning demanded data. Data is stored in JSON format. Client can request a server for some data. It does so by sending request via socket to specified ip. Database is saving all of data in file that is created in jar file directory (only if such doesn't exist). Server supports multithreading, so there will not be a problem to handle many requests at the time.

## How to use it
### Server
To start server open a terminal in directory containing jar file of it and use this command:
```bash
java -jar [name of jar file]
```

To set up a server that is visible in public network, you need to do port fowarding in your router admin panel. Add rule for fowarding of port 38500 to your local machine ip. Also you can use hamachi application for that purpose.

Once server is running it will inform you about incoming request as well as responses to that requests.

### Client
If you want to start client open a terminal in directory containing jar file of it and use this command:
```bash
java -jar [name of jar file] -arg -arg...
```

arg is an option that you have to specify if you want to send request to the server. Arguments you can use are present: <br>
- ip - Server ip.
- v - Value to set in the certain database key.
- k - Key of database cell.
- t - Type of request which has to be performed.
- in - file cointaning json data to delete/add/get
- f - field of json object to retrieve

<b>Example: </b><br>
```bash
java -jar client.jar  -ip 127.0.0.1 -t set -k Name -v Paul
```
```bash
java -jar client.jar  -ip 127.0.0.1 -in request.json
```
Value can be whatever you want, numeric value or just a text. There are three types of operations you can perform: set, get, delete. Set sets an key-value data in database, delete removes some data by specified key and get prints requested data. There is a possibility to load json from a file by an -in parameter, file should be in the same folder as client jar file. File must containing json format and should have json extension. Example of file containing request.
```code
{
   "type":"set",
   "key":"person",
   "value":{
      "name":"Elon Musk",
      "car":{
         "model":"Tesla Roadster",
         "year":"2018"
      },
      "rocket":{
         "name":"Falcon 9",
         "launches":"87"
      }
   }
}
```
There is a possibility to retrieve an concrete field from an object. To do that use -f parameter. For example:
```code
java -jar client.jar -f Country,City,Street -t get -ip 127.0.0.1
```
Will give a value of street field that is stored in City field which is sotred in Country key. <br>
You can also set a value in this way:
```code
java -jar client.jar -t set -f person,name -v Anne -ip 127.0.0.1
```
Will set value of name field to anne.<br><br>
By -ip parameter you can specify address of server, but there is also other way to specify it. Create text file named ip.txt with ip in it, that you want to connect to. Ip file must be placed in same folder as client jar. Ip file gives an option to skip the -ip parameter.
## License
Programme is released under GNU GPL license.
## Download
- [Client](https://github.com/pawelwuuu/Json-Database/releases/download/first-release/JSON-Database.Client.jar)
- [Server](https://github.com/pawelwuuu/Json-Database/releases/download/first-release/JSON-Database.Server.jar)
