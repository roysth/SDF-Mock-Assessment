package visa.mocktest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//java -jar target/visa-mocktest-1.0-SNAPSHOT.jar

public class Main {

    public static void main( String[] args ) throws IOException {

        String directory1 = "./static";
        String directory2 = null;
        int port = 3000;

        if (args.length > 0) {
            for (int i = 0; i < args.length; i ++)
                if (args[i].contains("--port")){
                    port = Integer.parseInt(args[i +1]);
            }
            else if (args[i].contains ("--docRoot")){
                String[] directories = args[i+ 1].split(":");
                directory1 = directories [0];
                if (directories.length > 1) {
                    directory2 = directories [1];
                }
            }
        System.out.printf("The server will start on port %s\n", port);
        System.out.printf("The directories are %s/n", directory1, "and %s\n", directory2);
        }
        
        //Creating the object threadPool and defining the no. of threads
        ExecutorService threadPool = Executors.newFixedThreadPool (3);

        //need to manually close server. if not will disrupt other clients
        ServerSocket server = new ServerSocket(port);

        while(true){
            System.out.println("Waiting for client connection");
            Socket sock = server.accept(); 
            System.out.println("Connected ...");
            /*by inputing hte vairables (sock, directory1 and directory2) from Main
             * into the parenthesis, it allows the HttpClientConenction class to
             * use the values
             */
            HttpClientConnection thread = new HttpClientConnection(sock, directory1, directory2);
            threadPool.submit(thread);
            thread.taskFour();
            System.out.println("Submitted to threadpool");
        }
    }
}
