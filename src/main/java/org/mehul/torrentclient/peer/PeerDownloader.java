package org.mehul.torrentclient.peer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mehul.torrentclient.handshake.TcpPeerConnection;
import org.mehul.torrentclient.util.ByteUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

@RequiredArgsConstructor
@Slf4j
public class PeerDownloader {

    private static final int BLOCK_SIZE = 16 * 1024;
    private static final int INTEGER_BYTE_SIZE = 4;
    private final TcpPeerConnection peerConnection;

    private byte[] waitForMessageType(PeerMessageType messageType) throws IOException {
        log.info("Waiting for message of type: {}", messageType);

        while (true) {
            byte[] message;
            try {
                message = peerConnection.receivePeerMessage();
            } catch (IOException e) {
                throw new IOException("Error while receiving message: " + e.getMessage());
            }

            int id = message[0] & 0xFF;
            PeerMessageType receivedMessageType = PeerMessageType.fromId(id);

            if (!receivedMessageType.equals(messageType)) {
                log.info("Waiting for {} but got {}. Waiting some more...", messageType, receivedMessageType);
            } else {
                log.info("Message type: {} received", messageType);
                return ByteUtil.copyArray(message, 1, message.length - 1);
            }
        }
    }

    private void sendPeerMessage(PeerMessageType messageType, byte[] payload) throws IOException {
        byte[] message = createPeerMessage(messageType, payload);
        peerConnection.sendMessage(message);
    }

    private byte[] createPeerMessage(PeerMessageType messageType, byte[] payload) {
        int len = 1 + payload.length;
        ByteBuffer buffer = ByteBuffer.allocate(INTEGER_BYTE_SIZE + len);
        buffer.putInt(len);
        buffer.put((byte) messageType.getId());
        buffer.put(payload);
        return buffer.array();
    }

    public void setupForDownload() throws IOException {
        waitForMessageType(PeerMessageType.BITFIELD);
        sendPeerMessage(PeerMessageType.INTERESTED, new byte[0]);
        waitForMessageType(PeerMessageType.UNCHOKE);
    }

    private byte[] createRequestForPiece(int pieceIndex, int startIndex, int bytesNeeded) {
        ByteBuffer buffer = ByteBuffer.allocate(3 * INTEGER_BYTE_SIZE);
        buffer.putInt(pieceIndex);
        buffer.putInt(startIndex);
        buffer.putInt(bytesNeeded);
        return buffer.array();
    }

    /*
    Break the piece into blocks of 16 kiB (16 * 1024 bytes) and send a request message for each block

    The message id for request is 6.
    The payload for this message consists of:
        index: the zero-based piece index
        begin: the zero-based byte offset within the piece
            This will be 0 for the first block, 2^14 for the second block, 2*2^14 for the third block etc.
        length: the length of the block in bytes
            This will be 2^14 (16 * 1024) for all blocks except the last one.
            The last block will contain 2^14 bytes or less, you'll need calculate this value using the piece length.
     */
    public void sendRequestsToDownloadPiece(int pieceIndex, int pieceLength) throws IOException {
        log.info("Sending request to download piece index {} of length {}", pieceIndex, pieceLength);
        int blockCount = (pieceLength + BLOCK_SIZE - 1) / BLOCK_SIZE;

        for (int i = 0; i < blockCount; ++i) {
            int startIndex = i * BLOCK_SIZE;
            int bytesNeeded = Math.min(pieceLength - startIndex, BLOCK_SIZE);
            log.info("Sending request for {} bytes from index {}", bytesNeeded, startIndex);

            byte[] pieceRequestMessage = createRequestForPiece(pieceIndex, startIndex, bytesNeeded);
            sendPeerMessage(PeerMessageType.REQUEST, pieceRequestMessage);
        }
    }

    /*
    Wait for a piece message for each block you've requested

    The message id for piece is 7.
    The payload for this message consists of:
        index: the zero-based piece index
        begin: the zero-based byte offset within the piece
        block: the data for the piece, usually 2^14 bytes long
    */
    public byte[] downloadingPieceAfterRequest(int pieceIndex, int pieceLength) throws IOException {
        log.info("Downloading piece index {} of length {}", pieceIndex, pieceLength);
        int blockCount = (pieceLength + BLOCK_SIZE - 1) / BLOCK_SIZE;
        byte[] res = new byte[pieceLength];

        for (int i = 0; i < blockCount; ++i) {
            log.info("Downloading {} bytes from position {}", BLOCK_SIZE, i * BLOCK_SIZE);

            // Get information + payload
            byte[] chunkReceived = waitForMessageType(PeerMessageType.PIECE);
            ByteBuffer chunkBuffer = ByteBuffer.wrap(chunkReceived);

            // Get piece resPieceIndex and start resPieceIndex
            int resPieceIndex = chunkBuffer.getInt();
            if (resPieceIndex != pieceIndex) {
                throw new IOException("Expected resPieceIndex " + pieceIndex + " , resPieceIndex received " + resPieceIndex);
            }

            int begin = chunkBuffer.getInt();
            int block = chunkReceived.length - 2 * INTEGER_BYTE_SIZE;
            log.info("Piece Index in response: {}, Start Index in response: {}, Payload Size: {}"
                    , resPieceIndex, begin, block);

            // Get payload
            byte[] chunkPayload = new byte[block];
            chunkBuffer.get(chunkPayload);

            System.arraycopy(chunkPayload, 0, res, begin, block);
        }

        return res;
    }
}
