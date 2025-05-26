package org.mehul.torrentclient.bencode.decoder.bencode.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ByteStreamReader implements Iterator<Byte> {
    private final byte[] bytes;
    private int index;

    public ByteStreamReader(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes array cannot be null");
        }
        this.bytes = bytes;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < bytes.length;
    }

    @Override
    public Byte next() {
        if (!hasNext()) {
            throw new NoSuchElementException("End of byte stream reached");
        }
        return bytes[index++];
    }

    public Byte current() {
        if (!hasNext()) {
            throw new NoSuchElementException("End of byte stream reached");
        }
        return bytes[index];
    }

    public void reset() {
        index = 0;
    }
}
