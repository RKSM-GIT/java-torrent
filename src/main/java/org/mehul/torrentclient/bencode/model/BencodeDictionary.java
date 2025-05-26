package org.mehul.torrentclient.bencode.model;

import java.util.Map;

public class BencodeDictionary implements Bencode {

    private final Map<String, Bencode> value;

    public BencodeDictionary(Map<String, Bencode> value) {
        this.value = value;
    }

    @Override
    public BencodeType getType() {
        return BencodeType.DICTIONARY;
    }

    @Override
    public Map<String, Bencode> getValue() {
        return value;
    }
}
