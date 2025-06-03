package org.mehul.torrentclient.handshake;

import org.mehul.torrentclient.util.ByteUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class HandshakeMessageUtil {
    private static final int PROTOCOL_LENGTH = 19;
    private static final byte[] PROTOCOL_VAL = "BitTorrent protocol".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] RESERVED = new byte[8];
    private static final int INFO_HASH_LENGTH = 20;
    private static final int PEER_ID_LENGTH = 20;
    private static final int MESSAGE_LEN = 68;

    public static byte[] buildMessage(byte[] infoHash, byte[] peerId) {
        if (infoHash == null || infoHash.length != INFO_HASH_LENGTH) {
            throw new IllegalArgumentException("Info Hash must be " + INFO_HASH_LENGTH + " bytes long");
        }
        if (peerId == null || peerId.length != PEER_ID_LENGTH) {
            throw new IllegalArgumentException("Peer ID must be " + PEER_ID_LENGTH + " bytes long");
        }

        ByteBuffer buffer = ByteBuffer.allocate(MESSAGE_LEN);

        buffer.put((byte) PROTOCOL_LENGTH);
        buffer.put(PROTOCOL_VAL);
        buffer.put(RESERVED);
        buffer.put(infoHash);
        buffer.put(peerId);

        return buffer.array();
    }

    public static byte[] extractPeerId(byte[] handshakeMessage) {
        if (handshakeMessage == null || handshakeMessage.length != MESSAGE_LEN) {
            throw new IllegalArgumentException("Hand Shake Message must be of length " + MESSAGE_LEN);
        }

        // from byte 48 to 68
        return ByteUtil.copyArray(handshakeMessage, MESSAGE_LEN - PEER_ID_LENGTH, PEER_ID_LENGTH);
    }
}
