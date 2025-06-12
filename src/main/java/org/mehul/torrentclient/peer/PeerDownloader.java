package org.mehul.torrentclient.peer;

import lombok.RequiredArgsConstructor;
import org.mehul.torrentclient.handshake.TcpPeerConnection;

import java.io.IOException;
import java.nio.ByteBuffer;

@RequiredArgsConstructor
public class PeerDownloader {

    private final TcpPeerConnection peerConnection;

    public void waitForMessage(String expectedMessageId) throws IOException {

    }

    public void waitForBitfieldMessage() throws IOException {
        try {
            byte[] message = peerConnection.receivePeerMessage();
            int id = message[0] & 0xFF;
            PeerMessageType messageType = PeerMessageType.fromId(id);

            if (!messageType.equals(PeerMessageType.BITFIELD)) {
                throw new IOException("Waiting for " + PeerMessageType.BITFIELD + " but got " + messageType);
            }
        } catch (IOException e) {
            throw new IOException("Error while waiting for " + PeerMessageType.BITFIELD + " message", e);
        }
    }

    public void sendInterestedMessage() throws IOException {
        int messageLength = 1;
        int bytesInAnInteger = 4;
        ByteBuffer buffer = ByteBuffer.allocate(messageLength + bytesInAnInteger);
        buffer.putInt(messageLength);
        buffer.put((byte) PeerMessageType.INTERESTED.getId());

        try {
            peerConnection.sendMessage(buffer.array());
        } catch (IOException e) {
            throw new IOException("Error while sending " + PeerMessageType.INTERESTED + " message", e);
        }
    }

    public void waitForUnchokeMessage() throws IOException {
        try {
            byte[] message = peerConnection.receivePeerMessage();
            int id = message[0] & 0xFF;
            PeerMessageType messageType = PeerMessageType.fromId(id);

            if (!messageType.equals(PeerMessageType.UNCHOKE)) {
                throw new IOException("Waiting for " + PeerMessageType.UNCHOKE + " but got " + messageType);
            }
        } catch (IOException e) {
            throw new IOException("Error while waiting for " + PeerMessageType.UNCHOKE + " message", e);
        }
    }
}
