package org.mehul.torrentclient;

import org.mehul.torrentclient.bencode.api.BencodeApi;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.Bencode;
import org.mehul.torrentclient.domain.model.TorrentFile;

import java.io.IOException;

public class TorrentApp {
    public static void main(String[] args) throws IOException, BencodeException {
        BencodeApi bencodeApi = new BencodeApi();

        String filePath = "puppy.torrent";
        Bencode decoded = bencodeApi.decodeFile(filePath);
        TorrentFile torrentFile = TorrentFile.fromBencode(decoded);

        System.out.println(torrentFile);
    }
}
