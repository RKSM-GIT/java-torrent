package org.mehul.torrentclient.handshake;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class TcpPeerConnectionTest {

    private TestSocket socket;
    private TcpPeerConnection peerConnection;

    @BeforeEach
    void setUp() throws IOException {
        socket = new TestSocket();
        // Create a test-specific TcpPeerConnection that uses our TestSocket
        peerConnection = new TestTcpPeerConnection("localhost", 6881, socket);
    }

    @Test
    void sendMessage_Success() throws IOException {
        byte[] message = "test message".getBytes();
        peerConnection.sendMessage(message);
        assertArrayEquals(message, socket.getWrittenData());
    }

    @Test
    void sendMessage_IOException() throws IOException {
        byte[] message = "test message".getBytes();
        socket.setNextWriteException(new IOException("Write error"));
        assertThrows(IOException.class, () -> peerConnection.sendMessage(message));
    }

    @Test
    void receiveMessage_Success() throws IOException {
        byte[] expectedData = "test data".getBytes();
        socket.setNextReadData(expectedData);
        byte[] result = peerConnection.receiveMessage(expectedData.length);
        assertArrayEquals(expectedData, result);
    }

    @Test
    void receiveMessage_ConnectionClosed() throws IOException {
        socket.setNextReadData(new byte[0]);
        assertThrows(IOException.class, () -> peerConnection.receiveMessage(10));
    }

    @Test
    void receiveMessage_IOException() throws IOException {
        socket.setNextReadException(new IOException("Read error"));
        assertThrows(IOException.class, () -> peerConnection.receiveMessage(10));
    }

    @Test
    void receivePeerMessage_Success() throws IOException {
        byte[] messageData = "test".getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(4 + messageData.length);
        buffer.putInt(messageData.length);
        buffer.put(messageData);
        socket.setNextReadData(buffer.array());

        byte[] result = peerConnection.receivePeerMessage();
        assertArrayEquals(messageData, result);
    }

    @Test
    void receivePeerMessage_KeepAlive() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(0);
        socket.setNextReadData(buffer.array());

        byte[] result = peerConnection.receivePeerMessage();
        assertEquals(0, result.length);
    }

    @Test
    void close_Success() throws IOException {
        peerConnection.close();
        assertTrue(socket.isClosed());
    }

    @Test
    void close_IOException() throws IOException {
        socket.setCloseException(new IOException("Close error"));
        assertThrows(IOException.class, () -> peerConnection.close());
    }

    private static class TestTcpPeerConnection extends TcpPeerConnection {

        public TestTcpPeerConnection(String host, int port, Socket testSocket) throws IOException {
            super(host, port);
            // Use reflection to replace the socket
            try {
                java.lang.reflect.Field socketField = TcpPeerConnection.class.getDeclaredField("socket");
                socketField.setAccessible(true);
                socketField.set(this, testSocket);

                // Also replace the streams
                java.lang.reflect.Field inputStreamField = TcpPeerConnection.class.getDeclaredField("inputStream");
                inputStreamField.setAccessible(true);
                inputStreamField.set(this, testSocket.getInputStream());

                java.lang.reflect.Field outputStreamField = TcpPeerConnection.class.getDeclaredField("outputStream");
                outputStreamField.setAccessible(true);
                outputStreamField.set(this, testSocket.getOutputStream());
            } catch (Exception e) {
                throw new RuntimeException("Failed to set test socket", e);
            }
        }
    }

    private static class TestSocket extends Socket {
        private final ByteArrayOutputStream outputStream;
        @Getter
        private boolean closed;
        @Setter
        private IOException closeException;
        private IOException readException;
        private byte[] nextReadData;

        public TestSocket() {
            this.outputStream = new ByteArrayOutputStream();
            this.closed = false;
        }

        public void setNextReadData(byte[] data) {
            this.nextReadData = data;
            this.readException = null;
        }

        public void setNextReadException(IOException exception) {
            this.readException = exception;
            this.nextReadData = null;
        }

        public void setNextWriteException(IOException exception) {
        }

        @Override
        public java.io.InputStream getInputStream() {
            return new ByteArrayInputStream(nextReadData != null ? nextReadData : new byte[0]);
        }

        @Override
        public java.io.OutputStream getOutputStream() {
            return outputStream;
        }

        @Override
        public void close() throws IOException {
            if (closeException != null) {
                throw closeException;
            }
            closed = true;
        }

        public byte[] getWrittenData() {
            return outputStream.toByteArray();
        }
    }
} 