package org.mehul.torrentclient.bencode.encoder;

public interface EncoderStrategy {
    byte[] encode(Object obj);
}
