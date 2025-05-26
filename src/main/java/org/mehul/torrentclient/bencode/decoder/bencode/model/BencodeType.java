package org.mehul.torrentclient.bencode.decoder.bencode.model;

import java.util.ArrayList;
import java.util.List;

public enum BencodeType {
    DICTIONARY('d'),
    LIST('l'),
    NUMBER('i'),
    BYTE_ARRAY('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    private final List<Character> startMarkers;

    BencodeType(char... startMarkers) {
        this.startMarkers = new ArrayList<>(startMarkers.length);
        for (char c : startMarkers) {
            this.startMarkers.add(c);
        }
    }

    public boolean matchesMarker(byte b) {
        return startMarkers.contains((char) b);
    }

    public static BencodeType fromByte(byte b) {
        for (BencodeType type : values()) {
            if (type.matchesMarker(b)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown bencode start marker: " + b);
    }
}
