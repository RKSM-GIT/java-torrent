package org.mehul.torrentclient.bencode.model;

public interface Bencode {
    enum BencodeType {NUMBER, STRING, LIST, DICTIONARY};

    BencodeType getType();

    Object getValue();
}
