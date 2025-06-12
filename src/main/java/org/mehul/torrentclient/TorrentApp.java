package org.mehul.torrentclient;

import org.mehul.torrentclient.bencode.api.BencodeApi;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.Bencode;
import org.mehul.torrentclient.handshake.HandshakeMessageUtil;
import org.mehul.torrentclient.handshake.TcpPeerConnection;
import org.mehul.torrentclient.torrent.MetaInfoFile;
import org.mehul.torrentclient.torrent.SingleFileTorrentInfo;
import org.mehul.torrentclient.tracker.TrackerInfo;
import org.mehul.torrentclient.util.ByteUtil;
import org.mehul.torrentclient.util.PeerUtil;

import java.io.IOException;

public class TorrentApp {
    public static void main(String[] args) throws IOException, BencodeException {
        BencodeApi bencodeApi = new BencodeApi();

        // Parse torrent file
        String filePath = "sample.torrent";
        System.out.println("Parsing Torrent File - " + filePath);
        Bencode decoded = bencodeApi.decodeFile(filePath);
        MetaInfoFile metaInfoFile = MetaInfoFile.fromBencode(decoded);

        // Display torrent file
        System.out.println("\nTorrent Info: ");
        SingleFileTorrentInfo torrentInfo = (SingleFileTorrentInfo) metaInfoFile.getTorrentInfo();
        byte[] infoHash = metaInfoFile.getInfoHash();

        System.out.println("Tracker URL: " + metaInfoFile.getAnnounce());
        System.out.println("Length: " + torrentInfo.getLength());
        System.out.println("Info Hash: " + ByteUtil.bytesToHexString(infoHash));
        System.out.println("Piece Length: " + torrentInfo.getMaxPieceLength());

        System.out.println("Piece Hashes: ");
        torrentInfo.getPieceHashes().forEach(System.out::println);

        // Display Tracker Information
        System.out.println("\nTracker Information: ");
        byte[] myPeerId = PeerUtil.generatePeerId();
        System.out.println("My random Peer ID for Tracker Info: " + ByteUtil.bytesToHexString(myPeerId));

        TrackerInfo trackerInfo = metaInfoFile.getTrackers(myPeerId);
        System.out.println("Tracker: {");
        for (var peer : trackerInfo.getPeers()) {
            System.out.println("\t" + peer);
        }
        System.out.println("}");

        // Do handshakes
        System.out.println("\nDoing a handshake using my Peer ID: " + ByteUtil.bytesToHexString(myPeerId));
        byte[] message = HandshakeMessageUtil.buildMessage(infoHash, myPeerId);
        for (var peer : trackerInfo.getPeers()) {
            try (TcpPeerConnection connection = new TcpPeerConnection(peer.getHostIp(), peer.getPort())) {
                connection.sendMessage(message);
                byte[] response = connection.receiveMessage(68);
                byte[] peerId = HandshakeMessageUtil.extractPeerId(response);

                System.out.println("Peer: " + peer + ", Peer ID: " + ByteUtil.bytesToHexString(peerId));
            } catch (IOException e) {
                System.out.println("Unable to get peer id for: " + peer);
            }
        }
    }
}
