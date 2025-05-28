package org.mehul.torrentclient.torrent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.Bencode;
import org.mehul.torrentclient.bencode.model.BencodeDictionary;
import org.mehul.torrentclient.bencode.model.BencodeNumber;
import org.mehul.torrentclient.bencode.model.BencodeString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrackerInfo {
    private static final String INTERVAL_KEY = "interval";
    private static final String PEERS_KEY = "peers";
    private static final int PEERS_HASH_LENGTH = 6;

    private int interval;
    private List<Peer> peers;

    public static TrackerInfo fromBencode(Bencode bencode) {
        if (bencode.getType() != Bencode.BencodeType.DICTIONARY) {
            throw new BencodeException("Only dictionary type bencode can be transformed into TrackerResponse");
        }

        TrackerInfo trackerInfo = new TrackerInfo();
        Map<String, Bencode> dict = ((BencodeDictionary) bencode).getValue();

        trackerInfo.setInterval(dict);
        trackerInfo.setPeers(dict);

        return trackerInfo;
    }

    public void setInterval(Map<String, Bencode> dict) throws BencodeException {
        if (!dict.containsKey(INTERVAL_KEY)) {
            throw new BencodeException("No " + INTERVAL_KEY + " key in TrackerResponse dictionary");
        }

        Bencode intervalBencode = dict.get(INTERVAL_KEY);
        if (intervalBencode.getType() != Bencode.BencodeType.NUMBER) {
            throw new BencodeException("Interval in TrackerResponse dictionary should be of type Number");
        }

        long interval = ((BencodeNumber) intervalBencode).getValue();
        this.interval = (int) interval;
    }

    public void setPeers(Map<String, Bencode> dict) throws BencodeException {
        if (!dict.containsKey(PEERS_KEY)) {
            throw new BencodeException("No " + PEERS_KEY + " key in TrackerResponse dictionary");
        }

        Bencode peersBencode = dict.get(PEERS_KEY);
        if (peersBencode.getType() != Bencode.BencodeType.STRING) {
            throw new BencodeException("Peers in TrackerResponse dictionary should be of type String");
        }

        byte[] concatenatedPeers = ((BencodeString) peersBencode).getValue();
        this.peers = Peer.peerListFromBytes(concatenatedPeers);
    }
}
