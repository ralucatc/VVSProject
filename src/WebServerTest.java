import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class WebServerTest {

    private int port = 8080;

    @Test
    public void setPort_GetPort() {
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        Server.setPort(0);
        Assertions.assertEquals(0,Server.getPort());
    }

    @Test
    public void setHome_GetHome() {
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        Server.setHome("C:/Users/ADMIN/Desktop/WebPages/Different");
        Assertions.assertEquals("C:/Users/ADMIN/Desktop/WebPages/Different", Server.getHome());
    }

    @Test
    public void setStatus_GetStatus() {
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        Server.setStatus("Stopped");
        Assertions.assertEquals("Stopped", Server.getStatus());
    }

    @Test
    public void createServerSocket_Success() {
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");

        try {
            ServerSocket socket = Server.createServerSocket(port);

            Assertions.assertTrue(socket.isBound());

            socket.close();
        }catch(BindException bindException) {
            Assertions.fail(bindException);
        }catch(IOException ioException) {
            Assertions.fail(ioException);
        }
    }

    @Test
    public void createServerSocket_PortOutOfRange_ThrowsIllegalArgumentException() {
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        Assertions.assertThrows(IllegalArgumentException.class,() -> {
            ServerSocket socket = Server.createServerSocket(-1);
        });
    }

    @Test
    public void createServerSocket_PortOccupied_ThrowsBindException() {
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        Assertions.assertThrows(BindException.class,() -> {
            ServerSocket socket1 = Server.createServerSocket(port);
            ServerSocket socket2 = Server.createServerSocket(port);
        });
    }

    @Test
    public void closeServerSocket_Success() {
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        try {
            ServerSocket socket = Server.createServerSocket(port);
            Server.closeServerSocket(socket);
            Assertions.assertTrue(socket.isClosed());
        }catch(BindException bindException) {
            Assertions.fail(bindException);
        }
    }

    @Test
    public void closeServerSocket_NullSocket_ThrowsNullPointerException() {
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        Assertions.assertThrows(NullPointerException.class,() -> {
            ServerSocket socket = null;
            Server.closeServerSocket(socket);
        });
    }

    @Test
    public void acceptConnectedSocket_Success() {
        /* Connect to localhost:8080 when running this test */
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        try {
            ServerSocket socket = Server.createServerSocket(port);
            Socket acceptedSocket = Server.acceptConnectedSocket(socket);

            Assertions.assertTrue(acceptedSocket.isBound());

            socket.close();
            acceptedSocket.close();
        }catch(BindException bindException) {
            Assertions.fail(bindException);
        }catch(IOException ioException) {
            Assertions.fail(ioException);
        }catch(Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void acceptConnectedSocket_AcceptFromClosedServerSocket_ThrowsSocketException() {
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        Assertions.assertThrows(SocketException.class,() -> {
            ServerSocket socket = Server.createServerSocket(port);
            socket.close();
            Socket acceptedSocket = Server.acceptConnectedSocket(socket);
            acceptedSocket.close();
        });
    }

    @Test
    public void closeAcceptedSocket_Success() {
        /* Connect to localhost:8080 when running this test */
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        try {
            ServerSocket socket = Server.createServerSocket(port);
            Socket acceptedSocket = Server.acceptConnectedSocket(socket);
            Server.closeAcceptedSocket(acceptedSocket);
            Assertions.assertTrue(acceptedSocket.isClosed());

            socket.close();
        }catch(BindException bindException) {
            Assertions.fail(bindException);
        }catch(IOException ioException) {
            Assertions.fail(ioException);
        }catch(Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void closeAcceptedSocket_NullSocket_ThrowsNullPointerException() {
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        Assertions.assertThrows(NullPointerException.class,() -> {
            Socket acceptedSocket = null;
            Server.closeAcceptedSocket(acceptedSocket);
        });
    }

    @Test
    public void readInputStream_Success() {
        /* Connect to localhost:8080 when running this test */
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        try {
            ServerSocket socket = Server.createServerSocket(port);
            Socket acceptedSocket = Server.acceptConnectedSocket(socket);
            ArrayList<String> inputStream = null;
            inputStream = Server.readInputStream(acceptedSocket);
            Assertions.assertFalse(inputStream.isEmpty());
            acceptedSocket.close();
            socket.close();
        }catch(BindException bindException) {
            Assertions.fail(bindException);
        }catch(IOException ioException) {
            Assertions.fail(ioException);
        }catch(Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void readInputStream_NullSocket_ThrowsNullPointerException() {
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        Assertions.assertThrows(NullPointerException.class,() -> {
            Socket acceptedSocket = null;
            Server.readInputStream(acceptedSocket);
        });
    }

    @Test
    public void sendOutputStream_Success_ServerRunning() {
        /* Connect to localhost:8082 when running this test */
        WebServer Server = new WebServer(8082, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        try {
            ServerSocket socket = Server.createServerSocket(8082);
            Socket acceptedSocket = Server.acceptConnectedSocket(socket);
            Server.sendOutputStream(acceptedSocket, Paths.get("C:/Users/ADMIN/Desktop/WebPages/a.html"),"HTTP/1.1");

            acceptedSocket.close();
            socket.close();
        }catch(BindException bindException) {
            Assertions.fail(bindException);
        }catch(IOException ioException) {
            Assertions.fail(ioException);
        }catch(Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void sendOutputStream_NullSocket_ThrowsNullPointerException() {
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        Assertions.assertThrows(NullPointerException.class,() -> {
            Socket acceptedSocket = null;
            Server.sendOutputStream(acceptedSocket, Paths.get("C:/Users/ADMIN/Desktop/WebPages/a.html"),"HTTP/1.1");

        });
    }

    @Test
    public void handleRequest_Success_ServerRunning() {
        /* Connect to localhost:8080 when running this test */
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        try {
            Server.handleRequest();
        }catch(Exception e)
        {
            Assertions.fail(e);
        }
    }

    @Test
    public void handleRequest_Success_ServerRunning_FileNotFound() {
        /* Connect to localhost:8080/different.html when running this test */
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Running");
        try {
            Server.handleRequest();
        }catch(Exception e)
        {
            Assertions.fail(e);
        }
    }

    @Test
    public void handleRequest_Success_ServerMaintenance() {
        /* Connect to localhost:8080 when running this test */
        WebServer Server = new WebServer(port, "C:/Users/ADMIN/Desktop/WebPages", "Maintenance");
        try {
            Server.handleRequest();
        }catch(Exception e)
        {
            Assertions.fail(e);
        }
    }

    @Test
    public void handleRequest_Success_ServerStopped() {
        /* Connect to localhost:8081 when running this test */
        WebServer Server = new WebServer(8081, "C:/Users/ADMIN/Desktop/WebPages", "Stopped");
        try {
            Server.handleRequest();
        }catch(Exception e)
        {
            Assertions.fail(e);
        }
    }

    @Test
    public void handleRequest_NullHome_ThrowsNoSuchFileException() {
        /* Connect to localhost:8081 when running this test */
        WebServer Server = new WebServer(8081, null, "Running");
        Assertions.assertThrows(NoSuchFileException.class,() -> {
            Server.handleRequest();
        });
    }

    @Test
    public void main() {
        Main main = new Main();
        main.main(new String[] {"Test"});
    }
}