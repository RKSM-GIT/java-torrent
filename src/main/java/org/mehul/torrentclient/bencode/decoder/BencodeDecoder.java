package org.mehul.torrentclient.bencode.decoder;

import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.*;
import org.mehul.torrentclient.util.ByteIterator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BencodeDecoder {

    public Bencode decode(byte[] bytes) {
        ByteIterator it = new ByteIterator(bytes);
        Bencode result = decodeNext(it);

        if (it.hasNext()) {
            throw new BencodeException("Trailing data after valid bencode at position " + it.getIndex());
        }

        return result;
    }

    private Bencode decodeNext(ByteIterator it) {
        byte b = it.peek();

        return switch (b) {
            case 'i' -> decodeNumber(it);
            case 'l' -> decodeList(it);
            case 'd' -> decodeDictionary(it);
            default -> {
                if (b >= '0' && b <= '9') {
                    yield decodeString(it);
                }
                throw new BencodeException("Unexpected byte '" + (char) b + "' at position " + it.getIndex());
            }
        };
    }

    private BencodeNumber decodeNumber(ByteIterator it) {
        int start = it.getIndex();
        it.next(); // skip 'i'

        StringBuilder sb = new StringBuilder();

        if (it.peek() == '-') {
            byte b = it.next();
            sb.append((char) b);
        }

        boolean leadingZero = it.peek() == '0';
        while (true) {
            byte b = it.next();
            if (b == 'e') {
                break;
            }

            if (!Character.isDigit(b)) {
                throw new BencodeException("Invalid digit in integer at position " + it.getIndex());
            }

            sb.append((char) b);
        }

        if (sb.toString().equals("-0") || (leadingZero && sb.length() > 1)) {
            throw new BencodeException("Invalid integer format at position " + start);
        }

        try {
            return new BencodeNumber(Long.parseLong(sb.toString()));
        } catch (NumberFormatException ex) {
            throw new BencodeException("Malformed number at position " + start, ex);
        }
    }

    private BencodeString decodeString(ByteIterator it) {
        int start = it.getIndex();
        StringBuilder lengthStr = new StringBuilder();

        while (true) {
            byte b = it.next();
            if (b == ':') break;
            if (!Character.isDigit(b)) {
                throw new BencodeException("Invalid string length at position " + it.getIndex());
            }
            lengthStr.append((char) b);
        }

        int length;
        try {
            length = Integer.parseInt(lengthStr.toString());
        } catch (NumberFormatException ex) {
            throw new BencodeException("Invalid string length at position " + start, ex);
        }

        if (length < 0) {
            throw new BencodeException("Negative string length at position " + start);
        }

        byte[] strBytes = new byte[length];
        for (int i = 0; i < length; i++) {
            if (!it.hasNext()) {
                throw new BencodeException("Unexpected end of input in string value at position " + it.getIndex());
            }
            byte x = it.peek();
            char c = (char) x;
            strBytes[i] = it.next();
        }

        return new BencodeString(strBytes);
    }

    private BencodeList decodeList(ByteIterator it) {
        it.next(); // skip 'l'
        List<Bencode> items = new ArrayList<>();

        while (true) {
            if (!it.hasNext()) {
                throw new BencodeException("Unexpected end of list at position " + it.getIndex());
            }

            if (it.peek() == 'e') {
                it.next(); // skip 'e'
                break;
            }

            items.add(decodeNext(it));
        }

        return new BencodeList(items);
    }

    private BencodeDictionary decodeDictionary(ByteIterator it) {
        it.next(); // skip 'd'
        Map<String, Bencode> map = new LinkedHashMap<>();
        List<String> keys = new ArrayList<>();

        while (true) {
            if (!it.hasNext()) {
                throw new BencodeException("Unexpected end of dictionary at position " + it.getIndex());
            }

            if (it.peek() == 'e') {
                it.next(); // skip 'e'
                break;
            }

            BencodeString keyObj = decodeString(it);
            String key = keyObj.asString();
            keys.add(key);

            Bencode value = decodeNext(it);
            map.put(key, value);
        }

        return new BencodeDictionary(map);
    }
}
