package org.mehul.torrentclient;

import lombok.extern.slf4j.Slf4j;
import org.mehul.torrentclient.bencode.api.BencodeApi;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.Bencode;
import org.mehul.torrentclient.handshake.HandshakeMessageUtil;
import org.mehul.torrentclient.handshake.TcpPeerConnection;
import org.mehul.torrentclient.peer.Peer;
import org.mehul.torrentclient.peer.PeerDownloader;
import org.mehul.torrentclient.torrent.MetaInfoFile;
import org.mehul.torrentclient.torrent.SingleFileTorrentInfo;
import org.mehul.torrentclient.tracker.TrackerInfo;
import org.mehul.torrentclient.util.PeerUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
public class TorrentApp {
    public static void main(String[] args) throws IOException, BencodeException {
        BencodeApi bencodeApi = new BencodeApi();

        // Parse torrent file
        String filePath = "sample.torrent";
        Bencode decoded = bencodeApi.decodeFile(filePath);
        MetaInfoFile metaInfoFile = MetaInfoFile.fromBencode(decoded);

        // Torrent File Information
        SingleFileTorrentInfo torrentInfo = (SingleFileTorrentInfo) metaInfoFile.getTorrentInfo();
        byte[] infoHash = metaInfoFile.getInfoHash();

        // My Peer ID & Tracker Info
        byte[] myPeerId = PeerUtil.generatePeerId();
        TrackerInfo trackerInfo = metaInfoFile.getTrackers(myPeerId);
        log.info("My Piece ID: {}", myPeerId);

        // Get First Peer
        List<Peer> peers = trackerInfo.getPeers();
        String ip = peers.getFirst().getHostIp();
        int port = peers.getFirst().getPort();

        // Download All Pieces
        log.info("Downloading all pieces from first peer. Host: {}, Port: {}", ip, port);
        try (
                final var connection = new TcpPeerConnection(ip, port);
                final var fileOutputStream = new FileOutputStream(new File("output.txt"));
        ) {

            for (int i = 0; i < torrentInfo.getPieceHashes().size(); ++i) {
                log.info("Downloading piece index: {}", i);

                // Do Handshake
                log.info("Performing Handshake");
                byte[] message = HandshakeMessageUtil.buildMessage(infoHash, myPeerId);
                connection.sendMessage(message);
                byte[] response = connection.receiveMessage(68);

                PeerDownloader peerDownloader = new PeerDownloader(connection);

                // BITSET -> INTERESTED -> UNCHOKE
                peerDownloader.setupForDownload();
                int pieceLen = torrentInfo.getPieceLength(i);

                // Send Requests
                peerDownloader.sendRequestsToDownloadPiece(i, pieceLen);

                // Download Pieces in Chunks
                byte[] downloadedPiece = peerDownloader.downloadingPieceAfterRequest(i, pieceLen);

                // Verify downloaded data
                boolean isValid = torrentInfo.verifyPiece(downloadedPiece, i);
                log.info("Verification Result -> isValid: {}", isValid);

                // Save to File
                fileOutputStream.write(downloadedPiece);
            }
        } catch (IOException e) {
            log.error("IO-Error while downloading piece index 0: {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error while downloading piece index 0: {}", e.getMessage());
        }
    }
}
