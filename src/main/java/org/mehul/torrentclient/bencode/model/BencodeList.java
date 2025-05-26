package org.mehul.torrentclient.bencode.model;

import java.util.List;

public class BencodeList implements Bencode {

    private final List<Bencode> value;

    public BencodeList(List<Bencode> value) {
        this.value = value;
    }

    @Override
    public BencodeType getType() {
        return BencodeType.LIST;
    }

    @Override
    public List<Bencode> getValue() {
        return value;
    }
}
