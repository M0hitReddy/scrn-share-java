
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;

public class Client {

    public static void main(String[] args) throws Exception {
        String serverIp = "192.168.29.206";// Update with your server IP
        int port = 9090;

        try {
            Socket client = new Socket(serverIp, port);
            OutputStream out = client.getOutputStream();
            DataOutputStream dataOut = new DataOutputStream(out);
            System.out.println("Connected to server");

            // Create a Robot object to capture the screen
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

            // Set frame rate (30 FPS)
            int fps = 30;
            long frameTime = 1000 / fps;

            while (true) {
                long startTime = System.currentTimeMillis();

                BufferedImage screenCapture = robot.createScreenCapture(screenRect);

                // Write the image to a ByteArrayOutputStream
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(screenCapture, "png", baos);
                byte[] imageData = baos.toByteArray();

                // Send the length of the image data and the image itself
                dataOut.writeInt(imageData.length);
                dataOut.write(imageData);

                // Flush the stream to ensure data is sent
                dataOut.flush();

                // Calculate time taken for this frame and adjust sleep time to maintain constant frame rate
                long timeTaken = System.currentTimeMillis() - startTime;
                long sleepTime = frameTime - timeTaken;
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}