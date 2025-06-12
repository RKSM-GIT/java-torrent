package org.mehul.torrentclient.bencode.api;

import lombok.extern.slf4j.Slf4j;
import org.mehul.torrentclient.bencode.decoder.BencodeDecoder;
import org.mehul.torrentclient.bencode.encoder.BencodeEncoder;
import org.mehul.torrentclient.bencode.model.Bencode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class BencodeApi {
    private final BencodeDecoder decoder;
    private final BencodeEncoder encoder;

    public BencodeApi() {
        this.decoder = new BencodeDecoder();
        this.encoder = new BencodeEncoder();
    }

    public Bencode decode(byte[] bytes) {
        return decoder.decode(bytes);
    }

    public byte[] encode(Bencode bencode) {
        return encoder.encode(bencode);
    }

    public Bencode decodeFile(String path) throws IOException {
        log.info("Decoding bencode data of {}", path);
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("Unable to find file: " + path);
        }

        byte[] bytes = Files.readAllBytes(file.toPath());
        return decode(bytes);
    }

    public void encodeToFile(Bencode bencode, String path) throws IOException {
        byte[] encoded = encode(bencode);
        Path filePath = Paths.get(path);
        Files.write(filePath, encoded);
    }
}
