package org.mehul.torrentclient;

import org.mehul.torrentclient.bencode.api.BencodeApi;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.Bencode;
import org.mehul.torrentclient.domain.model.SingleFileTorrentInfo;
import org.mehul.torrentclient.domain.model.TorrentFile;
import org.mehul.torrentclient.util.ByteUtil;

import java.io.IOException;

public class TorrentApp {
    public static void main(String[] args) throws IOException, BencodeException {
        BencodeApi bencodeApi = new BencodeApi();

        String filePath = "puppy.torrent";
        Bencode decoded = bencodeApi.decodeFile(filePath);
        TorrentFile torrentFile = TorrentFile.fromBencode(decoded);

        System.out.println("Tracker URL: " + torrentFile.getAnnounce());
        System.out.println("Length: " + ((SingleFileTorrentInfo) torrentFile.getTorrentInfo()).getLength());
        System.out.println("Info Hash: " + torrentFile.getInfoHashAsString());
        System.out.println("Piece Length: " + torrentFile.getTorrentInfo().getPieceLength());

        System.out.println("Piece Hashes: ");
        torrentFile.getTorrentInfo().getPieceHashes().forEach((el) -> System.out.println(ByteUtil.bytesToString(el)));

    }
}
