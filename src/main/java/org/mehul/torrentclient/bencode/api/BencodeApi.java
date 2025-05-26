package org.mehul.torrentclient.bencode.api;

import com.mehul.torrentclient.bencode.decoder.BencodeDecoder;
import com.mehul.torrentclient.bencode.encoder.BencodeEncoder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BencodeApi {
    private final BencodeDecoder decoder;
    private final BencodeEncoder encoder;

    public BencodeApi() {
        this.decoder = new BencodeDecoder();
        this.encoder = new BencodeEncoder();
    }

    public Object decode(byte[] bytes) {
        return decoder.decode(bytes);
    }

    public byte[] encode(Object obj) {
        return encoder.encode(obj);
    }

    public Object decodeFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("Unable to find file: " + path);
        }

        byte[] bytes = Files.readAllBytes(file.toPath());
        return decode(bytes);
    }

    public void encodeToFile(Object obj, String path) throws IOException {
        byte[] encoded = encode(obj);
        Path filePath = Paths.get(path);
        Files.write(filePath, encoded);
    }
}
