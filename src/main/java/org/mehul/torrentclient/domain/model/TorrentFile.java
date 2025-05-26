package org.mehul.torrentclient.domain.model;

import lombok.*;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.Bencode;
import org.mehul.torrentclient.bencode.model.BencodeDictionary;
import org.mehul.torrentclient.bencode.model.BencodeString;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TorrentFile {
    private static final String ANNOUNCE_KEY = "announce";
    private static final String INFO_KEY = "info";

    private String announce;
    private TorrentInfo torrentInfo;

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
    }
}
