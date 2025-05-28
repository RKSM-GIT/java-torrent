package org.mehul.torrentclient;

import org.mehul.torrentclient.bencode.api.BencodeApi;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.Bencode;
import org.mehul.torrentclient.torrent.SingleFileTorrentInfo;
import org.mehul.torrentclient.torrent.TorrentFile;
import org.mehul.torrentclient.tracker.TrackerInfo;
import org.mehul.torrentclient.util.ByteUtil;

import java.io.IOException;

public class TorrentApp {
    public static void main(String[] args) throws IOException, BencodeException {
        BencodeApi bencodeApi = new BencodeApi();

        String filePath = "sample.torrent";
        Bencode decoded = bencodeApi.decodeFile(filePath);
        TorrentFile torrentFile = TorrentFile.fromBencode(decoded);

        System.out.println("Tracker URL: " + torrentFile.getAnnounce());
        System.out.println("Length: " + ((SingleFileTorrentInfo) torrentFile.getTorrentInfo()).getLength());
        System.out.println("Info Hash: " + torrentFile.getInfoHashAsString());
        System.out.println("Piece Length: " + torrentFile.getTorrentInfo().getPieceLength());
        System.out.println("Piece Hashes: ");
        SingleFileTorrentInfo torrentInfo = (SingleFileTorrentInfo) torrentFile.getTorrentInfo();
        torrentInfo.getPieceHashes().forEach((el) -> System.out.println(ByteUtil.bytesToHexString(el)));

        System.out.println();

        TrackerInfo trackerInfo = torrentFile.getTrackers();
        System.out.println("Tracker: {");
        for (var peer : trackerInfo.getPeers()) {
            System.out.println("\t" + peer);
        }
        System.out.println("}");

    }
}
