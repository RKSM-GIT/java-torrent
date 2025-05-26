package org.mehul.torrentclient.bencode.model;

import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        return value.entrySet().stream()
                .map(e -> "\"" + e.getKey().replace("\"", "\\\"") + "\": " + e.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
