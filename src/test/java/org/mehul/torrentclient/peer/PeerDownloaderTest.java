package org.mehul.torrentclient.peer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mehul.torrentclient.handshake.TcpPeerConnection;

class PeerDownloaderTest {

    private TestTcpPeerConnection peerConnection;
    private PeerDownloader peerDownloader;

    @BeforeEach
    void setUp() throws IOException {
        peerConnection = new TestTcpPeerConnection();
        peerDownloader = new PeerDownloader(peerConnection);
    }

    @Test
    void setupForDownload_Success() throws IOException {
        // Setup test data
        byte[] bitfieldMessage = createPeerMessage(PeerMessageType.BITFIELD, new byte[]{1, 2, 3});
        byte[] unchokeMessage = createPeerMessage(PeerMessageType.UNCHOKE, new byte[0]);
        
        peerConnection.setNextResponse(bitfieldMessage);
        peerConnection.setNextResponse(unchokeMessage);

        // Execute
        peerDownloader.setupForDownload();

        // Verify
        assertTrue(peerConnection.getSentMessages().length > 0);
    }

    @Test
    void sendRequestsToDownloadPiece_Success() throws IOException {
        int pieceIndex = 0;
        int pieceLength = 16384; // 16KB

        // Execute
        peerDownloader.sendRequestsToDownloadPiece(pieceIndex, pieceLength);

        // Verify
        assertTrue(peerConnection.getSentMessages().length > 0);
    }

    @Test
    void downloadingPieceAfterRequest_Success() throws IOException {
        int pieceIndex = 0;
        int pieceLength = 16384;

        // Setup test data
        byte[] pieceData = new byte[pieceLength];
        byte[] pieceMessage = createPeerMessage(PeerMessageType.PIECE, pieceData);
        peerConnection.setNextResponse(pieceMessage);

        // Execute
        byte[] result = peerDownloader.downloadingPieceAfterRequest(pieceIndex, pieceLength);

        // Verify
        assertNotNull(result);
        assertEquals(pieceLength, result.length);
    }

    @Test
    void setupForDownload_IOException() throws IOException {
        peerConnection.setNextResponse(new IOException("Connection error"));

        assertThrows(IOException.class, () -> peerDownloader.setupForDownload());
    }

    private byte[] createPeerMessage(PeerMessageType type, byte[] payload) {
        int len = 1 + payload.length;
        ByteBuffer buffer = ByteBuffer.allocate(4 + len);
        buffer.putInt(len);
        buffer.put((byte) type.getId());
        buffer.put(payload);
        return buffer.array();
    }

    // Test-specific subclass of TcpPeerConnection
    private static class TestTcpPeerConnection extends TcpPeerConnection {
        private final ByteArrayOutputStream outputStream;
        private final ByteArrayInputStream inputStream;
        private byte[] nextResponse;
        private IOException nextException;

        public TestTcpPeerConnection() throws IOException {
            super("localhost", 6881);
            this.outputStream = new ByteArrayOutputStream();
            this.inputStream = new ByteArrayInputStream(new byte[0]);
        }

        public void setNextResponse(byte[] response) {
            this.nextResponse = response;
            this.nextException = null;
        }

        public void setNextResponse(IOException exception) {
            this.nextException = exception;
            this.nextResponse = null;
        }

        @Override
        public void sendMessage(byte[] message) throws IOException {
            if (nextException != null) {
                throw nextException;
            }
            outputStream.write(message);
        }

        @Override
        public byte[] receiveMessage(int length) throws IOException {
            if (nextException != null) {
                throw nextException;
            }
            if (nextResponse == null) {
                throw new IOException("No response set");
            }
            return nextResponse;
        }

        public byte[] getSentMessages() {
            return outputStream.toByteArray();
        }
    }
} 