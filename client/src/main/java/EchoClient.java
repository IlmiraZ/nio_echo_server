import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class EchoClient {
    private static final ByteBuffer buffer = ByteBuffer.allocate(1024);

    public void start() {
        InetSocketAddress iAddress = new InetSocketAddress("localhost", 9000);
        try (SocketChannel client = SocketChannel.open(iAddress);
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))
        ) {
            System.out.println("Client connected to " + iAddress + ", blocking: " + client.isBlocking());
            while (client.isOpen()) {
                if (br.ready()) {
                    String str = br.readLine();
                    buffer.clear();
                    buffer.put(str.getBytes(StandardCharsets.UTF_8));
                    buffer.flip();
                    client.write(buffer);

                    buffer.clear();
                    client.read(buffer);
                    buffer.flip();
                    String response = StandardCharsets.UTF_8.decode(buffer).toString();
                    System.out.println("echo: " + response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
