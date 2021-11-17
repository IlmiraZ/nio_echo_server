import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoSever {
    private static final ByteBuffer echoBuffer = ByteBuffer.allocate(1024);

    public void start() throws IOException {
        int port = 9000;

        Selector selector = Selector.open();

        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.configureBlocking(false);

        ServerSocket serverSocket = socketChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        serverSocket.bind(address);

        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started...");

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectedKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                    SocketChannel client = ssChannel.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("Client is connected " + client);
                } else if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();

                    while (true) {
                        try {
                            echoBuffer.clear();
                            int i = channel.read(echoBuffer);
                            if (i < 0) {
                                System.out.println("End-of-stream");
                                channel.close();
                            } else if (i == 0) {
                                break;
                            }
                            echoBuffer.flip();
                            channel.write(echoBuffer);
                        } catch (IOException e) {
                            System.out.println("Error: " + e.getMessage());
                            channel.close();
                            break;
                        }
                    }
                }
            }
        }
    }
}
