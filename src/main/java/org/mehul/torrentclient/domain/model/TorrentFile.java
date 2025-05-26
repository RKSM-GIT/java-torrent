package org.mehul.torrentclient.domain.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.mehul.torrentclient.bencode.encoder.BencodeEncoder;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.Bencode;
import org.mehul.torrentclient.bencode.model.BencodeDictionary;
import org.mehul.torrentclient.bencode.model.BencodeString;
import org.mehul.torrentclient.util.ByteUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TorrentFile {
    private static final String ANNOUNCE_KEY = "announce";
    private static final String INFO_KEY = "info";

    private String announce;
    private TorrentInfo torrentInfo;
    private byte[] infoHash;

    public static TorrentFile fromBencode(Bencode bencode) throws BencodeException {
        if (bencode.getType() != Bencode.BencodeType.DICTIONARY) {
            throw new BencodeException("Only dictionary type bencode can be transformed into TorrentFile");
        }

        TorrentFile torrentFile = new TorrentFile();
        Map<String, Bencode> dict = ((BencodeDictionary) bencode).getValue();

        torrentFile.setAnnounce(dict);
        torrentFile.setInfo(dict);

        return torrentFile;
    }

    public void setAnnounce(Map<String, Bencode> dict) throws BencodeException {
        if (!dict.containsKey(ANNOUNCE_KEY)) {
            throw new BencodeException("No " + ANNOUNCE_KEY + " key in TorrentInfo dictionary");
        }

        Bencode announceBencode = dict.get(ANNOUNCE_KEY);
        if (announceBencode.getType() != Bencode.BencodeType.STRING) {
            throw new BencodeException("Announce in TorrentFile dictionary should be of type String");
        }

        this.announce = ((BencodeString) dict.get(ANNOUNCE_KEY)).asString();
    }

    public void setInfo(Map<String, Bencode> dict) throws BencodeException {
        if (!dict.containsKey(INFO_KEY)) {
            throw new BencodeException("No " + INFO_KEY + " key in TorrentInfo dictionary");
        }

        Bencode infoBencode = dict.get(INFO_KEY);
        this.torrentInfo = (SingleFileTorrentInfo.fromBencode(infoBencode));

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            BencodeEncoder bencodeEncoder = new BencodeEncoder();
            this.infoHash = md.digest(bencodeEncoder.encode(infoBencode));
        } catch (NoSuchAlgorithmException ex) {
            throw new BencodeException("SHA-1 algorithm not available: " + ex.getMessage());
        } catch (RuntimeException ex) {
            throw new BencodeException("Error while calculating info hash: " + ex.getMessage());
        }
    }

    public String getAnnounce() {
        return announce;
    }

    public TorrentInfo getTorrentInfo() {
        return torrentInfo;
    }

    public byte[] getInfoHash() {
        return infoHash;
    }

    public String getInfoHashAsString() {
        return ByteUtil.bytesToString(infoHash);
    }
}
