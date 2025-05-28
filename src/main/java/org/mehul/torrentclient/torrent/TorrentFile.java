package org.mehul.torrentclient.torrent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.mehul.torrentclient.bencode.encoder.BencodeEncoder;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.Bencode;
import org.mehul.torrentclient.bencode.model.BencodeDictionary;
import org.mehul.torrentclient.bencode.model.BencodeString;
import org.mehul.torrentclient.tracker.TrackerInfo;
import org.mehul.torrentclient.util.ByteUtil;
import org.mehul.torrentclient.util.HttpUtil;
import org.mehul.torrentclient.util.PeerUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
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

    public String getInfoHashAsString() {
        return ByteUtil.bytesToHexString(infoHash);
    }

    public TrackerInfo getTrackers() {
        HttpUtil httpUtil = new HttpUtil();
        List<TrackerInfo> res = new ArrayList<>();


        Map<String, String> params = new HashMap<>();

        byte[] peerIdBytes = PeerUtil.generatePeerId();

        params.put("info_hash", ByteUtil.bytesToString(infoHash));
        params.put("peer_id", ByteUtil.bytesToString(peerIdBytes));
        params.put("port", "6881");
        params.put("uploaded", "0");
        params.put("downloaded", "0");
        params.put("left", Integer.toString(torrentInfo.getPieceLength()));
        params.put("compact", "1");
        String uri = announce;

        Bencode responseBencode = httpUtil.getRequest(uri, params);
        return TrackerInfo.fromBencode(responseBencode);
    }
}
