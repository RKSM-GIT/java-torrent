package org.mehul.torrentclient.handshake;

import org.mehul.torrentclient.util.ByteUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

public class TcpPeerConnection implements AutoCloseable {
    private static final int TIMEOUT = 10_000; // 10 seconds

    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public TcpPeerConnection(String host, int port) throws IOException {
        try {
            this.socket = new Socket(host, port);
            this.socket.setSoTimeout(TIMEOUT);
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new IOException("Failed to connect to peer at " + host + ":" + port, e);
        }
    }

    public void sendMessage(byte[] message) throws IOException {
        try {
            outputStream.write(message);
            outputStream.flush();
        } catch (IOException e) {
            throw new IOException("Unable to send message " + ByteUtil.bytesToHexString(message), e);
        }
    }

    public byte[] receiveMessage(int length) throws IOException {
        int lenReceived = 0;
        byte[] buffer = new byte[length];

        try {
            while (lenReceived < length) {
                int bytesRead = inputStream.read(buffer, lenReceived, length - lenReceived);
                if (bytesRead == -1) {
                    throw new IOException("Connection closed prematurely while reading");
                }

                lenReceived += bytesRead;
            }
        } catch (SocketTimeoutException e) {
            throw new IOException("Socket timeout occurred while waiting for peer response", e);
        } catch (IOException e) {
            throw new IOException("Error while reading data from peer", e);
        }

        return buffer;
    }

    // [ 4-byte length ][ 1-byte message ID ][ payload of (length-1) bytes ]
    public byte[] receivePeerMessage() throws IOException {
        byte[] lengthBytes = receiveMessage(4);
        int length = ByteBuffer.wrap(lengthBytes).getInt();

        if (length == 0) {
            // if length is 0, then this is a keep alive message
            return new byte[0];
        }

        return receiveMessage(length);
    }

    @Override
    public void close() throws IOException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new IOException("Unable to close the socket", e);
        }
    }
}
