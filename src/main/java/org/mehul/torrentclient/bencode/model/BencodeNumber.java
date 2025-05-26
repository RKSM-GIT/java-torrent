package org.mehul.torrentclient.bencode.model;

public class BencodeNumber implements Bencode {

    private final Long value;

    public BencodeNumber(Long value) {
        this.value = value;
    }

    @Override
    public BencodeType getType() {
        return BencodeType.NUMBER;
    }

    @Override
    public Long getValue() {
        return value;
    }
}
