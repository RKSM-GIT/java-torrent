package org.mehul.torrentclient.bencode.model;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        return value.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
