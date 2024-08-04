import java.io.*;
import java.net.*;
import java.util.*;

public final class WebServer {
    public static void main(String argv[]) throws Exception {
        int port = 6789;
        ServerSocket listenSocket = new ServerSocket(port);

        while (true) {
            // Listen for a TCP connection request.
            Socket connectionSocket = listenSocket.accept();

            // Construct an object to process the HTTP request message.
            HttpRequest request = new HttpRequest(connectionSocket);

            // Create a new thread to process the request.
            Thread thread = new Thread(request);

            // Start the thread.
            thread.start();
        }
    }
}

final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    // Constructor
    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    // Implement the run() method of the Runnable interface.
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        // Get a reference to the socket's input and output streams.
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // Get the request line of the HTTP request message.
        String requestLine = br.readLine();

        // Check if the request line is not null and has tokens.
        if (requestLine != null) {
            // Extract the filename from the request line.
            StringTokenizer tokens = new StringTokenizer(requestLine);

            // Check if there are tokens available before extracting.
            if (tokens.hasMoreTokens()) {
                tokens.nextToken();  // skip over the method, which should be "GET"

                // Check if there are more tokens before extracting the filename.
                if (tokens.hasMoreTokens()) {
                    String fileName = tokens.nextToken();

                    // Prepend a "." so that the file request is within the current directory.
                    fileName = "." + fileName;

                    // Open the requested file.
                    FileInputStream fis = null;
                    boolean fileExists = true;
                    // Exception Handling
                    try {
                        fis = new FileInputStream(fileName);
                    } catch (FileNotFoundException e) {
                        fileExists = false;
                    }

                    // Construct the response message and assign null for error handling
                    String statusLine = null;
                    String contentTypeLine = null;
                    String entityBody = null;
                    if (fileExists) { // In case of correct file name and extension
                        statusLine = "HTTP/1.1 200 OK" + CRLF;
                        contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
                    } else { // Error Handling
                        statusLine = "HTTP/1.1 404 Not Found" + CRLF;
                        contentTypeLine = "Content-type: text/html" + CRLF;
                        entityBody = "<HTML>" +
                                "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
                                "<BODY>Not Found</BODY></HTML>";
                    }

                    // Send the status line.
                    os.writeBytes(statusLine);

                    // Send the content type line.
                    os.writeBytes(contentTypeLine);

                    // Send a blank line to indicate the end of the header lines.
                    os.writeBytes(CRLF);

                    // Send the data of file and then close
                    if (fileExists) {
                        sendBytes(fis, os);
                        fis.close();
                    } else {
                        os.writeBytes(entityBody);
                    }

                    // Close streams and socket.
                    os.close();
                    br.close();
                    socket.close();
                }
            }
        }
    }
    // Function to send file data

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes;
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }
    // Function to handle files for displaying on web page
    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }
}
