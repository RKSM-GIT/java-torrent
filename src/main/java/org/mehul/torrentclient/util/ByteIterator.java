package org.mehul.torrentclient.util;

import org.mehul.torrentclient.bencode.exception.BencodeException;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ByteIterator implements Iterator<Byte> {
    private final byte[] bytes;
    private int index;

    public ByteIterator(byte[] bytes) {
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
            throw new BencodeException("Unexpected end of input at position " + index);
        }
        return bytes[index++];
    }

    public Byte peek() {
        if (!hasNext()) {
            throw new NoSuchElementException("Unexpected end of input at position " + index);
        }
        return bytes[index];
    }

    public int getIndex() {
        return index;
    }
}
