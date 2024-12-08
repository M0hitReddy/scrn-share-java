import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Viewer {

    public static void main(String[] args) throws Exception {
        String serverIp = "192.168.29.206"; // Replace with your server's public IP
        int port = 9090;

        try {
            Socket client = new Socket(serverIp, port);
            System.out.println("Connected to server");

            // Notify server that this is a viewer
            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
            writer.println("no");

            // Create a JFrame to display the shared screen
            JFrame frame = new JFrame("Live Screen Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            JLabel label = new JLabel();
            frame.getContentPane().add(label, BorderLayout.CENTER);
            frame.setVisible(true);

            // Receive screen data
            InputStream in = client.getInputStream();
            DataInputStream dataIn = new DataInputStream(in);

            while (true) {
                int length = dataIn.readInt();
                byte[] imageData = new byte[length];
                dataIn.readFully(imageData);

                // Convert image data to BufferedImage
                ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                BufferedImage image = ImageIO.read(bais);

                // Display the image in the JFrame
                label.setIcon(new ImageIcon(image));
                frame.repaint();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
