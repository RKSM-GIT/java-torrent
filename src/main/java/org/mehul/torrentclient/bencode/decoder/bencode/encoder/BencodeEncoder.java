package org.mehul.torrentclient.bencode.decoder.bencode.encoder;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class BencodeEncoder implements EncoderStrategy {
    private static final byte DICTIONARY_START = (byte) 'd';
    private static final byte LIST_START = (byte) 'l';
    private static final byte NUMBER_START = (byte) 'i';
    private static final byte END_MARKER = (byte) 'e';
    private static final byte BYTE_ARRAY_DIVIDER = (byte) ':';

    @Override
    public byte[] encode(Object obj) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encodeNextObject(outputStream, obj);
        return outputStream.toByteArray();
    }

    public void encodeNextObject(ByteArrayOutputStream outputStream, Object obj) {
        if (obj instanceof byte[]) {
            encodeByteArray(outputStream, (byte[]) obj);
        } else if (obj instanceof String) {
            encodeString(outputStream, (String) obj);
        } else if (obj instanceof Long || obj instanceof Integer) {
            encodeNumber(outputStream, ((Number) obj).longValue());
        } else if (obj instanceof List) {
            encodeList(outputStream, (List<Object>) obj);
        } else if (obj instanceof Map) {
            encodeDictionary(outputStream, (Map<String, Object>) obj);
        } else {
            throw new IllegalArgumentException("Unable to encode type: " + obj.getClass().getName());
        }
    }

    public void encodeNumber(ByteArrayOutputStream outputStream, long number) {
        outputStream.write(NUMBER_START);
        byte[] numberBytes = String.valueOf(number).getBytes(StandardCharsets.UTF_8);
        outputStream.write(numberBytes, 0, numberBytes.length);
        outputStream.write(END_MARKER);
    }

    public void encodeString(ByteArrayOutputStream outputStream, String str) {
        encodeByteArray(outputStream, str.getBytes(StandardCharsets.UTF_8));
    }

    private void encodeByteArray(ByteArrayOutputStream outputStream, byte[] bytes) {
        byte[] lengthBytes = String.valueOf(bytes.length).getBytes(StandardCharsets.UTF_8);
        outputStream.write(lengthBytes, 0, lengthBytes.length);
        outputStream.write(BYTE_ARRAY_DIVIDER);
        outputStream.write(bytes, 0, bytes.length);
    }

    public void encodeList(ByteArrayOutputStream outputStream, List<Object> list) {
        outputStream.write(LIST_START);
        for (Object item : list) {
            encodeNextObject(outputStream, item);
        }
        outputStream.write(END_MARKER);
    }

    public void encodeDictionary(ByteArrayOutputStream outputStream, Map<String, Object> dict) {
        outputStream.write(DICTIONARY_START);

        // Sort keys by their UTF-8 byte representation
        List<String> sortedKeys = dict.keySet().stream()
                .sorted((a, b) -> {
                    byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
                    byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);

                    for (int i = 0; i < Math.min(aBytes.length, bBytes.length); i++) {
                        int comparison = Byte.compare(aBytes[i], bBytes[i]);
                        if (comparison != 0) {
                            return comparison;
                        }
                    }
                    return Integer.compare(aBytes.length, bBytes.length);
                })
                .toList();

        for (String key : sortedKeys) {
            encodeString(outputStream, key);
            encodeNextObject(outputStream, dict.get(key));
        }

        outputStream.write(END_MARKER);
    }
}
