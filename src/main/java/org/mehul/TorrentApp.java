package org.mehul;

import org.mehul.torrentclient.bencode.api.BencodeApi;
import org.mehul.torrentclient.bencode.model.Bencode;
import org.mehul.torrentclient.bencode.model.BencodeDictionary;

import java.io.IOException;

public class TorrentApp {
    public static void main(String[] args) throws IOException {
        BencodeApi bencodeApi = new BencodeApi();

        String filePath = "puppy.torrent";
        Bencode decoded = bencodeApi.decodeFile(filePath);
        BencodeDictionary bencodeDictionary = (BencodeDictionary) decoded;

        System.out.println(bencodeDictionary.getValue());
    }
}
