package org.mehul.torrentclient.bencode.model;

import java.nio.charset.StandardCharsets;

public class BencodeString implements Bencode {

    private final byte[] value;

    public BencodeString(byte[] value) {
        this.value = value;
    }

    public String asString() {
        return new String(value, StandardCharsets.UTF_8);
    }

    @Override
    public BencodeType getType() {
        return BencodeType.STRING;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "\"" + asString().replace("\"", "\\\"")
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }
}
