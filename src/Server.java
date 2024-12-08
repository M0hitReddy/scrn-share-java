import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.imageio.ImageIO;

public class Server {

    private static CopyOnWriteArrayList<Socket> viewers = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9090);
        System.out.println("Server started, waiting for connections...");

        while (true) {
            Socket client = server.accept();
            System.out.println("Client connected: " + client.getInetAddress());

            // Handle client connections in separate threads
            new Thread(() -> handleClient(client)).start();
        }
    }

    private static void handleClient(Socket client) {
        System.out.println("Handling client: " + client.getInetAddress());
        try {
            InputStream in = client.getInputStream();
            DataInputStream dataIn = new DataInputStream(in);

            // Determine if the client is the sharer or a viewer
            OutputStream out = client.getOutputStream();
            PrintWriter writer = new PrintWriter(out, true);
            writer.println("Are you the sharer? (yes/no)");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String response = reader.readLine();

            if ("yes".equalsIgnoreCase(response.trim())) {
                // Handle screen sharer
                System.out.println("Sharer connected.");
                while (true) {
                    int length = dataIn.readInt();
                    byte[] imageData = new byte[length];
                    dataIn.readFully(imageData);
                    System.out.println("Received image data of length: " + length);
                    System.out.println("Relaying image to viewers: " + viewers.size() + " connected viewers.");


                    // Broadcast screen to all viewers
                    for (Socket viewer : viewers) {
                        try {
                            OutputStream viewerOut = viewer.getOutputStream();
                            DataOutputStream viewerDataOut = new DataOutputStream(viewerOut);
                            viewerDataOut.writeInt(length);
                            viewerDataOut.write(imageData);
                            viewerDataOut.flush();
                        } catch (Exception e) {
                            System.out.println("Error sending to viewer: " + e);
                            viewers.remove(viewer); // Remove disconnected viewers
                        }
                    }
                }
            } else {
                // Handle viewer
                viewers.add(client);
                System.out.println("Viewer added. Total viewers: " + viewers.size());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
