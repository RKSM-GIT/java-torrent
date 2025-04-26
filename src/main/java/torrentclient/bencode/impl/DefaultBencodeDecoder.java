package bencode.impl;

import bencode.interfaces.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultBencodeDecoder implements BencodeDecoder {
    @Override
    public Object decode(InputStream inputStream) throws IOException {
        int firstByte = inputStream.read();

        if (firstByte == -1) {
            throw new IOException("Empty Stream");
        }

        char c = (char) firstByte;

        if (Character.isDigit(c)) {
            inputStream.reset();
            return decodeString(inputStream);
        } else if (c == 'i') {
            return decodeInteger(inputStream);
        } else if (c == 'l') {
            return decodeList(inputStream);
        } else if (c == 'd') {
            return decodeDictionary(inputStream);
        } else {
            throw new IOException("Invalid bencode format");
        }
    }

    private BencodeString decodeString(InputStream inputStream) throws IOException {
        StringBuilder lengthStr = new StringBuilder();
        int b;

        // Read the length until ':' character
        while ((b = inputStream.read()) != -1) {
            char c = (char) b;
            if (c == ':') {
                break;
            }
            lengthStr.append(c);
        }

        int length = Integer.parseInt(lengthStr.toString());
        byte[] bytes = new byte[length];
        int bytesRead = inputStream.read(bytes);

        if (bytesRead != length) {
            throw new IOException("Failed to read expected number of bytes");
        }

        return new DefaultBencodeString(bytes);
    }

    private Object decodeInteger(InputStream inputStream) throws IOException {
        StringBuilder value = new StringBuilder();
        int b;

        // Read until 'e' character
        while ((b = inputStream.read()) != -1) {
            char c = (char) b;
            if (c == 'e') {
                break;
            }
            value.append(c);
        }

        return new DefaultBencodeInteger(Long.parseLong(value.toString()));
    }

    private BencodeList decodeList(InputStream inputStream) throws IOException {
        List<Object> list = new ArrayList<>();

        while (true) {
            inputStream.mark(1);
            int b = inputStream.read();

            if (b == -1) {
                throw new IOException("Unexpected end of stream");
            }

            if ((char) b == 'e') {
                break;
            }

            inputStream.reset();
            list.add(decode(inputStream));
        }

        return new DefaultBencodeList(list);
    }

    private BencodeDictionary decodeDictionary(InputStream inputStream) throws IOException {
        Map<String, Object> dict = new HashMap<>();

        while (true) {
            inputStream.mark(1);
            int b = inputStream.read();

            if (b == -1) {
                throw new IOException("Unexpected end of stream");
            }

            if ((char) b == 'e') {
                break;
            }

            inputStream.reset();

            BencodeString key = decodeString(inputStream);
            Object value = decode(inputStream);

            dict.put(key.getString(), value);
        }

        return new DefaultBencodeDictionary(dict);
    }

    record DefaultBencodeString(byte[] bytes) implements BencodeString {

        @Override
        public byte[] bytes() {
            return bytes;
        }

        @Override
        public String getString() {
            return new String(bytes, StandardCharsets.UTF_8);
        }

        @Override
        public Type getType() {
            return Type.STRING;
        }
    }

    record DefaultBencodeInteger(long value) implements BencodeInteger {

        @Override
        public long getValue() {
            return value;
        }

        @Override
        public Type getType() {
            return Type.INTEGER;
        }
    }

    record DefaultBencodeList(List<Object> value) implements BencodeList {

        @Override
        public List<Object> getValue() {
            return value;
        }

        @Override
        public Type getType() {
            return Type.LIST;
        }
    }

    record DefaultBencodeDictionary(Map<String, Object> value) implements BencodeDictionary {

        @Override
        public Map<String, Object> getValue() {
            return value;
        }

        @Override
        public Type getType() {
            return Type.DICTIONARY;
        }
    }
}
