package org.mehul.torrentclient.bencode.decoder.bencode.decoder;

public interface DecoderStrategy {
    Object decode(byte[] bytes);
}
