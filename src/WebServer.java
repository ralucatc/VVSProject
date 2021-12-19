import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class WebServer extends Thread {
    private int port;
    private String home;
    private String status;


    public WebServer(final int port, final String home, final String status) {
        this.port = port;
        this.home = home;
        this.status = status;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public String getHome() {
        return home;
    }

    public void setHome(final String home) {
        this.home = home;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public ServerSocket createServerSocket(final int port) throws IllegalArgumentException, BindException {
        try {
            ServerSocket socket = new ServerSocket(port);
            this.setPort(port);
            System.out.println("Created server socket on port: " + port);
            return socket;
        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Port is out of range.");
            throw illegalArgumentException;
        } catch (BindException bindException) {
            System.out.println("Port already occupied.");
            throw bindException;
        } catch (Exception e) {
            System.out.println("Failed creating server socket on port: " + port);
            System.out.println("Exception: " + e);
            return null;
        }
    }

    public void closeServerSocket(final ServerSocket socket) throws NullPointerException {
        try {
            socket.close();
            System.out.println("Closed server socket on port: " + socket.getLocalPort());
        } catch (NullPointerException nullPointerException) {
            System.out.println("Socket is null.");
            throw nullPointerException;
        } catch (Exception e) {
            System.out.println("Error closing server socket on port: " + socket.getLocalPort());
            System.out.println("Exception: " + e);
        }
    }

    public Socket acceptConnectedSocket(final ServerSocket socket) throws Exception {
        try {
            return socket.accept();
        } catch (Exception e) {
            System.out.println("Error accepting connected socket.");
            System.out.println("Exception: " + e);
            throw e;
        }
    }

    public void closeAcceptedSocket(final Socket acceptedSocket) throws Exception {
        try {
            acceptedSocket.close();

        } catch (Exception e) {
            System.out.println("Error closing accepted socket.");
            System.out.println("Exception: " + e);
            throw e;
        }
    }

    public ArrayList<String> readInputStream(final Socket acceptedSocket) throws Exception {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(acceptedSocket.getInputStream()));
            ArrayList<String> inputStream = new ArrayList<>();
            String s;
            while ((s = in.readLine()) != null) {
                inputStream.add(s);
                if (s.trim().equals("")) {
                    break;
                }
            }
            return inputStream;
        } catch (Exception e) {
            System.out.println("Error reading input stream");
            System.out.println("Exception: " + e);
            throw e;
        }
    }

    public void sendOutputStream(final Socket acceptedSocket, final Path filePath, final String version) throws Exception {
        try {
            OutputStream out = acceptedSocket.getOutputStream();
            String status = this.getStatus();
            switch (status) {
                case "Running": {
                    if (Files.exists(filePath)) {
                        String contentType = Files.probeContentType(filePath);
                        byte[] fileContent = Files.readAllBytes(filePath);
                        out.write((version + " \r\n200 OK").getBytes());
                        out.write(("ContentType: " + contentType + "\r\n").getBytes());
                        out.write("\r\n".getBytes());
                        out.write(fileContent);
                        out.write("\r\n\r\n".getBytes());
                    } else {
                        String contentType = Files.probeContentType(Paths.get(this.getHome(), "404.html"));
                        byte[] fileContent = Files.readAllBytes(Paths.get(this.getHome(), "404.html"));
                        out.write((version + " \r\n404 Not Found").getBytes());
                        out.write(("ContentType: " + contentType + "\r\n").getBytes());
                        out.write("\r\n".getBytes());
                        out.write(fileContent);
                        out.write("\r\n\r\n".getBytes());
                    }
                }
                case "Maintenance": {
                    String contentType = Files.probeContentType(Paths.get(this.getHome(), "Maintenance.html"));
                    byte[] fileContent = Files.readAllBytes(Paths.get(this.getHome(), "Maintenance.html"));
                    out.write((version + " \r\n503 Service Unavailable").getBytes());
                    out.write(("ContentType: " + contentType + "\r\n").getBytes());
                    out.write("\r\n".getBytes());
                    out.write(fileContent);
                    out.write("\r\n\r\n".getBytes());
                }
                case "Stopped": {
                    System.out.println("Web Server is stopped.");
                }
                default: {
                    System.out.println("The current Web Server status is not defined.");
                }
            }
            out.close();

        } catch (Exception e) {
            System.out.println("Error sending output stream.");
            System.out.println("Exception: " + e);
            throw e;
        }
    }

    public void handleRequest() throws Exception {
        try {
            ServerSocket socket = createServerSocket(this.getPort());
            Socket acceptedSocket = acceptConnectedSocket(socket);
            ArrayList<String> inputStream = readInputStream(acceptedSocket);
            if (inputStream.size() != 0) {
                String path = inputStream.get(0).split(" ")[1];
                if (path.equals("/")) {
                    path = "/index.html";
                }
                try {
                    path = this.getHome() + path;
                } catch (NullPointerException nullPointerException) {
                    this.closeAcceptedSocket(acceptedSocket);
                    this.closeServerSocket(socket);
                    throw nullPointerException;
                }
                Path filePath = Paths.get(path);
                String version = inputStream.get(0).split(" ")[2];
                sendOutputStream(acceptedSocket, filePath, version);
            }
            this.closeServerSocket(socket);
            this.closeAcceptedSocket(acceptedSocket);
        } catch (Exception e) {
            System.out.println("Error handling request.");
            System.out.println("Exception: " + e);
            throw e;
        }
    }
}

class Main {
    public static void main(String[] args) {
        WebServer Server = new WebServer(8080,
                "C:/Users/ADMIN/Desktop/WebPages",
                "Stopped");
        for (;;) {
            try {
                Server.handleRequest();
                if (args.length > 0) {
                    if (args[0].equals("Test")) {
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        }
    }
}
