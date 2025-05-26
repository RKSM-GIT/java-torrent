package org.mehul.torrentclient.bencode.exception;

public class BencodeException extends RuntimeException {
    public BencodeException(String message) {
        super(message);
    }

    public BencodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
