package org.mehul.torrentclient.bencode.decoder;

import com.mehul.torrentclient.bencode.model.BencodeType;
import com.mehul.torrentclient.bencode.util.ByteStreamReader;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BencodeDecoder implements DecoderStrategy {
    private static final byte DICTIONARY_START = (byte) 'd';
    private static final byte LIST_START = (byte) 'l';
    private static final byte NUMBER_START = (byte) 'i';
    private static final byte END_MARKER = (byte) 'e';
    private static final byte BYTE_ARRAY_DIVIDER = (byte) ':';

    @Override
    public Object decode(byte[] bytes) {
        ByteStreamReader reader = new ByteStreamReader(bytes);
        return decodeNextObject(reader);
    }

    public Object decodeNextObject(ByteStreamReader reader) {
        if (!reader.hasNext()) {
            return null;
        }

        BencodeType type = BencodeType.fromByte(reader.current());

        try {
            return switch (type) {
                case DICTIONARY -> decodeNextDictionary(reader);
                case LIST -> decodeNextList(reader);
                case NUMBER -> decodeNextNumber(reader);
                case BYTE_ARRAY -> decodeNextString(reader);
                default -> throw new IllegalStateException("Unknown bencode type");
            };
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Invalid bencode provided: " + ex.getMessage());
        }
    }

    public Long decodeNextNumber(ByteStreamReader reader) {
        if (!reader.hasNext() || reader.current() != NUMBER_START) {
            throw new IllegalArgumentException("Dictionary should start with " + (char) NUMBER_START);
        }
        reader.next();

        return decodeNumberWithoutStartMarker(reader);
    }

    private Long decodeNumberWithoutStartMarker(ByteStreamReader reader) {
        List<Byte> bytes = new ArrayList<>();

        // Keep reading bytes until we hit the end marker
        while (reader.hasNext()) {
            if (reader.current() == END_MARKER || reader.current() == BYTE_ARRAY_DIVIDER) {
                reader.next();
                break;
            }

            bytes.add(reader.next());
        }

        if (bytes.isEmpty()) {
            return 0L;
        }

        // Convert bytes to string and parse
        byte[] numberBytes = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            numberBytes[i] = bytes.get(i);
        }

        String numberString = new String(numberBytes, StandardCharsets.UTF_8);
        return Long.parseLong(numberString);
    }

    public String decodeNextString(ByteStreamReader reader) {
        long strLen = decodeNumberWithoutStartMarker(reader);

        // Read actual byte array
        byte[] result = new byte[(int) strLen];
        for (int i = 0; i < strLen; i++) {
            result[i] = reader.next();
        }

        return new String(result, StandardCharsets.UTF_8);
    }

    public List<Object> decodeNextList(ByteStreamReader reader) {
        if (!reader.hasNext() || reader.current() != LIST_START) {
            throw new IllegalArgumentException("Dictionary should start with " + (char) LIST_START);
        }
        reader.next();

        List<Object> list = new ArrayList<>();

        // Keep decoding until we hit the end marker
        while (reader.hasNext()) {
            if (reader.current() == END_MARKER) {
                reader.next();
                break;
            }

            list.add(decodeNextObject(reader));
        }

        return list;
    }

    public Map<String, Object> decodeNextDictionary(ByteStreamReader reader) {
        if (!reader.hasNext() || reader.current() != DICTIONARY_START) {
            throw new IllegalArgumentException("Dictionary should start with " + (char) DICTIONARY_START);
        }
        reader.next();

        Map<String, Object> dict = new HashMap<>();

        // Keep decoding until we hit the end marker
        while (reader.hasNext()) {
            if (reader.current() == END_MARKER) {
                reader.next();
                break;
            }

            String key = decodeNextString(reader);
            Object value = decodeNextObject(reader);

            dict.put(key, value);
        }

        return dict;
    }
}
