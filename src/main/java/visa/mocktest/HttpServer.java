package visa.mocktest;

import java.io.BufferedReader;

//Setting up IO
//dont use readUTF/write UTF. use line

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class HttpServer {

    public InputStream is;
    public OutputStream os;
    public InputStreamReader isr;
    public BufferedReader br;
    

    /*Constructing the socket and 
    *instantiating the getInputStream etc
    */
    public HttpServer (Socket sock) throws IOException{
        this.is = sock.getInputStream();
        this.os = sock.getOutputStream();
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
    
    }
    //READ
    public String read() throws IOException {
       return this.br.readLine();
       
    }

    //WRITE
	 public void flush() throws Exception {
		 this.os.flush();
	 }

    public void close() throws Exception {
        os.flush();
        os.close();
    }

    public void writeString() throws Exception {
        writeString("");
    }
    public void writeString(String line) throws Exception {
        writeBytes(line.getBytes());
    }

    public void writeBytes(byte[] buffer) throws Exception {
        writeBytes(buffer, 0, buffer.length);
    }
    public void writeBytes(byte[] buffer, int start, int offset) throws Exception {
        os.write(buffer, start, offset);
    }
}
