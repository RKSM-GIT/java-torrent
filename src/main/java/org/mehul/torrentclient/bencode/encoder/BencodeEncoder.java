package org.mehul.torrentclient.bencode.encoder;

import org.mehul.torrentclient.bencode.model.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BencodeEncoder {

    public byte[] encode(Bencode value) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        encodeNext(out, value);
        return out.toByteArray();
    }

    private void encodeNext(ByteArrayOutputStream out, Bencode value) {
        switch (value) {
            case BencodeNumber bint -> encodeNumber(out, bint);
            case BencodeString bstr -> encodeString(out, bstr);
            case BencodeList blist -> encodeList(out, blist);
            case BencodeDictionary bdict -> encodeDictionary(out, bdict);
            default -> throw new IllegalArgumentException("Unknown Bencode type: " + value.getClass());
        }
    }

    private void encodeNumber(ByteArrayOutputStream out, BencodeNumber bint) {
        out.write('i');

        String numberString = Long.toString(bint.getValue());
        out.write(numberString.getBytes(StandardCharsets.UTF_8), 0, numberString.length());

        out.write('e');
    }

    private void encodeString(ByteArrayOutputStream out, BencodeString bstr) {
        byte[] bytes = bstr.getValue();
        out.write(Integer.toString(bytes.length).getBytes(StandardCharsets.UTF_8), 0,
                Integer.toString(bytes.length).length());
        out.write(':');
        out.write(bytes, 0, bytes.length);
    }

    private void encodeList(ByteArrayOutputStream out, BencodeList blist) {
        out.write('l');
        for (Bencode item : blist.getValue()) {
            encodeNext(out, item);
        }
        out.write('e');
    }

    private void encodeDictionary(ByteArrayOutputStream out, BencodeDictionary bdict) {
        out.write('d');
        Map<String, Bencode> map = bdict.getValue();

        // Sort keys based on UTF-8 byte order
        List<String> sortedKeys = new ArrayList<>(map.keySet());
        sortedKeys.sort(Comparator.comparing(k -> k.getBytes(StandardCharsets.UTF_8), Arrays::compare));

        for (String key : sortedKeys) {
            encodeString(out, new BencodeString(key.getBytes(StandardCharsets.UTF_8)));
            encodeNext(out, map.get(key));
        }
        out.write('e');
    }
}
