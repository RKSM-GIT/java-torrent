package org.mehul.torrentclient.torrent;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.Bencode;
import org.mehul.torrentclient.bencode.model.BencodeDictionary;
import org.mehul.torrentclient.bencode.model.BencodeNumber;
import org.mehul.torrentclient.bencode.model.BencodeString;
import org.mehul.torrentclient.tracker.TrackerInfo;

class MetaInfoFileTest {

    @Test
    void fromBencode_ValidDictionary() throws BencodeException {
        // Create a valid bencode dictionary
        Map<String, Bencode> dict = new HashMap<>();
        dict.put("announce", new BencodeString("http://tracker.example.com".getBytes()));

        Map<String, Bencode> infoDict = new HashMap<>();
        infoDict.put("name", new BencodeString("test.torrent".getBytes()));
        infoDict.put("length", new BencodeNumber(1000L));
        infoDict.put("piece length", new BencodeNumber(16384L));
        infoDict.put("pieces", new BencodeString("0123456789abcdef0123456789abcdef01234567".getBytes()));

        dict.put("info", new BencodeDictionary(infoDict));

        BencodeDictionary bencodeDict = new BencodeDictionary(dict);

        // Execute
        MetaInfoFile metaInfoFile = MetaInfoFile.fromBencode(bencodeDict);

        // Verify
        assertNotNull(metaInfoFile);
        assertEquals("http://tracker.example.com", metaInfoFile.getAnnounce());
        assertNotNull(metaInfoFile.getInfoHash());
        assertInstanceOf(SingleFileTorrentInfo.class, metaInfoFile.getTorrentInfo());
    }

    @Test
    void fromBencode_MissingAnnounce() {
        Map<String, Bencode> dict = new HashMap<>();
        BencodeDictionary bencodeDict = new BencodeDictionary(dict);

        assertThrows(BencodeException.class, () -> MetaInfoFile.fromBencode(bencodeDict));
    }

    @Test
    void fromBencode_MissingInfo() {
        Map<String, Bencode> dict = new HashMap<>();
        dict.put("announce", new BencodeString("http://tracker.example.com".getBytes()));
        BencodeDictionary bencodeDict = new BencodeDictionary(dict);

        assertThrows(BencodeException.class, () -> MetaInfoFile.fromBencode(bencodeDict));
    }

    @Test
    void fromBencode_InvalidAnnounceType() {
        Map<String, Bencode> dict = new HashMap<>();
        dict.put("announce", new BencodeNumber(123L));
        BencodeDictionary bencodeDict = new BencodeDictionary(dict);

        assertThrows(BencodeException.class, () -> MetaInfoFile.fromBencode(bencodeDict));
    }

    @Test
    void getTrackers_Success() throws BencodeException {
        // Create a valid bencode dictionary
        Map<String, Bencode> dict = new HashMap<>();
        dict.put("announce", new BencodeString("http://tracker.example.com".getBytes()));

        Map<String, Bencode> infoDict = new HashMap<>();
        infoDict.put("name", new BencodeString("test.torrent".getBytes()));
        infoDict.put("length", new BencodeNumber(1000L));
        infoDict.put("piece length", new BencodeNumber(16384L));
        infoDict.put("pieces", new BencodeString("0123456789abcdef0123456789abcdef01234567".getBytes()));

        dict.put("info", new BencodeDictionary(infoDict));

        BencodeDictionary bencodeDict = new BencodeDictionary(dict);
        MetaInfoFile metaInfoFile = MetaInfoFile.fromBencode(bencodeDict);

        // Execute
        byte[] peerId = new byte[20];
        TrackerInfo trackerInfo = metaInfoFile.getTrackers(peerId);

        // Verify
        assertNotNull(trackerInfo);
    }

    @Test
    void getTrackers_InvalidTrackerResponse() {
        // Create a valid bencode dictionary with invalid tracker URL
        Map<String, Bencode> dict = new HashMap<>();
        dict.put("announce", new BencodeString("http://invalid-tracker.example.com".getBytes()));

        Map<String, Bencode> infoDict = new HashMap<>();
        infoDict.put("name", new BencodeString("test.torrent".getBytes()));
        infoDict.put("length", new BencodeNumber(1000L));
        infoDict.put("piece length", new BencodeNumber(16384L));
        infoDict.put("pieces", new BencodeString("0123456789abcdef0123456789abcdef01234567".getBytes()));

        dict.put("info", new BencodeDictionary(infoDict));

        BencodeDictionary bencodeDict = new BencodeDictionary(dict);
        MetaInfoFile metaInfoFile = MetaInfoFile.fromBencode(bencodeDict);

        // Execute and verify
        byte[] peerId = new byte[20];
        assertThrows(BencodeException.class, () -> metaInfoFile.getTrackers(peerId));
    }
} 