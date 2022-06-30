package visa.mocktest;


import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.ArrayList;


public class HttpClientConnection implements Runnable {

    public Socket sock;
    public String directory1;
    public String directory2;
    //Need a File because File class allows to check if the path
    //exist, directory or can read
    public File directory1File; 
    public File directory2File;
    public boolean filesFound = false;
    
    /*The 3 variables in the parentheses are from Main.java.
    Hence, need to link it to the variables that are defined in this class
    */
    public HttpClientConnection (Socket sock, String directory1, String directory2) {
        this.sock = sock;
        this.directory1 = directory1;
        this.directory2 = directory2;
        this.directory1File = new File(directory1);
        if (null != directory2) {
            this.directory2File = new File (directory2);  
        }
    }

    public void taskFour () {
        //for directoryFile1
        //CHECK 1: check if path exists
        if (directory1File.exists()) {
            System.out.println("Path 1 exists");
        }
        else {
            System.out.println("Path 1 does not exists\n");
            System.exit(1);  
        }
        //CHECK 2:check if path is directory
        if (directory1File.isDirectory()) {
            System.out.println("Path 1 is a directory");
        }
        else {
            System.out.println("Path  1 is not a directory");
            System.exit(1);
        }
        //CHECK 3: check if path is readable by server
        if (directory1File.canRead()) {
            System.out.println("Path 1 is readable");
        }
        else {
            System.out.println("Path 1 is not readable");
            System.exit(1);
        }

        //for directoryFile2
        //CHECK 1: check if path exists
        if (null != directory2) {
            if (directory2File.exists()) {
            System.out.println("Path 2 exists");
            }
            else {
                System.out.println("Path 2 does not exists\n");
                System.exit(1);  
            }
            //CHECK 2:check if path is directory
            if (directory2File.isDirectory()) {
                System.out.println("Path 2 is a directory");
            }
            else {
                System.out.println("Path  2 is not a directory");
                System.exit(1);
            }
            //CHECK 3: check if path is readable by server
            if (directory2File.canRead()) {
                System.out.println("Path 2 is readable");
            }
            else {
                System.out.println("Path 2 is not readable");
                System.exit(1);
            }
        }
    }
    
    @Override
    public void run() {
        System.out.println("Starting a new client thread!");
        //Using the IO, hence must make an object first
        HttpServer netIO;

        try {
            netIO = new HttpServer(sock);
            //read the client's request and make it into a String.
            String request = netIO.read(); 
            System.out.println("TESTING - REQUEST: " + request);
            //input the items in the string into a String [] to be used later
            String[] requestParam = request.split(" ");
            String method = requestParam[0];
            String resourceName = requestParam[1];
            if (resourceName.equals("/")) {
                resourceName = "/index.html";
            }
            //remove all the / so that can compare the files later
            resourceName = resourceName.replace("/", "");
            //created this ArrayList to store all the Files in the directory given earlier
            ArrayList<File> combinedDirectory = new ArrayList<>();

                //File file is like a "i", so if file not inside, will add into combinedDirectory
                for (File file : directory1File.listFiles()) {
                    combinedDirectory.add(file);
                }
                if (null != directory2File) {
                    for (File file : directory2File.listFiles()){
                        combinedDirectory.add(file);
                    }
                }
            //TASK 6  
            //Action 1
            if(!method.equals("GET")) {
                netIO.writeString("HTTP/1.1 405 Method Not Allowed \r\n" + "POST method not supported \r\n");
                System.out.println("HTTP/1.1 405 Method Not Allowed \r\n" + "POST method not supported \r\n");
                netIO.close();
                sock.close();
            }
            //Action 2
            //for file inside the combinedDirectory
            for (File file : combinedDirectory){
                System.out.println("TESTING - resource name " + resourceName);
                System.out.println("TESTING - filename " + file.getName());
                //if any of the name in the file matches the resourceName
                if (file.getName().equals(resourceName)) {
                    //make an object of fileinputstream so that system can read the files (in bytes) later on
                    FileInputStream fis = new FileInputStream(file);
                    //convert file into byte array, so that system can write bytes
                    //write String dont need ^ abv step (fyi)
                    byte[] bytefile = fis.readAllBytes();
                    fis.close();
                    //Action 3 and Action 4
                    //if the resourse is not PNG
                    if (!resourceName.contains(".png")) {
                        System.out.println("ITS NOT A PNG FILE");
                        netIO.writeString("HTTP/1.1 200 OK\r\n\r\n");
                        //able to write bytes thanks to the steps above
                        netIO.writeBytes(bytefile);
                        System.out.println("TESTING - HTTP/1.1 200 OK\n" + "<resource contents as bytes>");
                        filesFound = true;
                        break;
                    }
                    //if resourse is PNG
                    else {
                        System.out.println("ITS A PNG FILE");
                        netIO.writeString("HTTP/1.1 200 OK\r\n Content-Type: image/png\r\n\r\n");
                        netIO.writeBytes(bytefile);
                        System.out.println("TESTING -HTTP/1.1 200 OK\n" + "Content-Type: image/png\n" 
                        + "<resource contents as bytes>");
                        filesFound =  true;   
                        break;         
                    }    
                }
                else {
                    filesFound = false;
                }         
            }
            if (!filesFound) {
                netIO.writeString("HTTP/1.1 404 Not Found\r\n" + resourceName + " not found\r\n");
                System.out.printf("TESTING - HTTP/1.1 404 Not Found\n" + "%s not found\n", resourceName);
            }
            
            
            netIO.close();
            sock.close(); 
                
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
} 
