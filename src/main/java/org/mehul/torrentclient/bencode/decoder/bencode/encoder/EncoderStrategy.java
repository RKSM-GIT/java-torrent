package org.mehul.torrentclient.bencode.decoder.bencode.encoder;

public interface EncoderStrategy {
    byte[] encode(Object obj);
}
