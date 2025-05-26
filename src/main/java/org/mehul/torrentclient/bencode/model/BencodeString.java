package org.mehul.torrentclient.bencode.model;

public class BencodeString implements Bencode {

    private final String value;

    public BencodeString(String value) {
        this.value = value;
    }

    @Override
    public BencodeType getType() {
        return BencodeType.STRING;
    }

    @Override
    public String getValue() {
        return value;
    }
}
