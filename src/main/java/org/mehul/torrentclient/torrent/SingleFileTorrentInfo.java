package org.mehul.torrentclient.torrent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.Bencode;
import org.mehul.torrentclient.bencode.model.BencodeDictionary;
import org.mehul.torrentclient.bencode.model.BencodeNumber;
import org.mehul.torrentclient.bencode.model.BencodeString;
import org.mehul.torrentclient.util.ByteUtil;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SingleFileTorrentInfo extends TorrentInfo {
    private static final String NAME_KEY = "name";
    private static final String LENGTH_KEY = "length";
    private static final String PIECE_LENGTH_KEY = "piece length";
    private static final String PIECES_KEY = "pieces";
    private static final int PIECE_HASH_LENGTH = 20;

    private int length;
    private List<byte[]> pieceHashes;

    public static SingleFileTorrentInfo fromBencode(Bencode bencode) throws BencodeException {
        if (bencode.getType() != Bencode.BencodeType.DICTIONARY) {
            throw new BencodeException("Only dictionary type bencode can be transformed into TorrentInfo");
        }

        SingleFileTorrentInfo singleFileTorrentInfo = new SingleFileTorrentInfo();
        Map<String, Bencode> dict = ((BencodeDictionary) bencode).getValue();

        singleFileTorrentInfo.setName(dict);
        singleFileTorrentInfo.setLength(dict);
        singleFileTorrentInfo.setPieceLength(dict);
        singleFileTorrentInfo.setPieces(dict);
        singleFileTorrentInfo.torrentType = TorrentFileType.SINGLE;

        return singleFileTorrentInfo;
    }

    public void setName(Map<String, Bencode> dict) throws BencodeException {
        if (!dict.containsKey(NAME_KEY)) {
            throw new BencodeException("No " + NAME_KEY + " key in TorrentInfo dictionary");
        }

        Bencode nameBencode = dict.get(NAME_KEY);
        if (nameBencode.getType() != Bencode.BencodeType.STRING) {
            throw new BencodeException("Name in TorrentInfo dictionary should be of type String");
        }

        this.name = ((BencodeString) nameBencode).asString();
    }

    public void setLength(Map<String, Bencode> dict) throws BencodeException {
        if (!dict.containsKey(LENGTH_KEY)) {
            throw new BencodeException("No " + LENGTH_KEY + " key in TorrentInfo dictionary");
        }

        Bencode lengthBencode = dict.get(LENGTH_KEY);
        if (lengthBencode.getType() != Bencode.BencodeType.NUMBER) {
            throw new BencodeException("Length in TorrentInfo dictionary should be of type Number");
        }

        long length = ((BencodeNumber) lengthBencode).getValue();
        this.length = (int) length;
    }

    public void setPieceLength(Map<String, Bencode> dict) throws BencodeException {
        if (!dict.containsKey(PIECE_LENGTH_KEY)) {
            throw new BencodeException("No " + PIECE_LENGTH_KEY + " key in TorrentInfo dictionary");
        }

        Bencode pieceLengthBencode = dict.get(PIECE_LENGTH_KEY);
        if (pieceLengthBencode.getType() != Bencode.BencodeType.NUMBER) {
            throw new BencodeException("Piece Length in TorrentInfo dictionary should be of type Number");
        }

        long pieceLength = ((BencodeNumber) pieceLengthBencode).getValue();
        this.pieceLength = (int) pieceLength;
    }

    public void setPieces(Map<String, Bencode> dict) throws BencodeException {
        if (!dict.containsKey(PIECES_KEY)) {
            throw new BencodeException("No " + PIECES_KEY + " key in TorrentInfo dictionary");
        }

        Bencode piecesBencode = dict.get(PIECES_KEY);
        if (piecesBencode.getType() != Bencode.BencodeType.STRING) {
            throw new BencodeException("Pieces in TorrentInfo dictionary should be of type String");
        }

        byte[] concatenatedPieces = ((BencodeString) piecesBencode).getValue();
        this.pieceHashes = ByteUtil.splitBytesByLength(concatenatedPieces, PIECE_HASH_LENGTH);
    }

}
