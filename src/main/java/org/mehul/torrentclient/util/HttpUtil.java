package org.mehul.torrentclient.util;

import org.mehul.torrentclient.bencode.decoder.BencodeDecoder;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.Bencode;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

// TODO: Make it a Singleton
public class HttpUtil {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public Bencode getRequest(String uri, Map<String, String> params) throws BencodeException {
        URI fullUri = makeFullUrl(uri, params);

        HttpRequest httpRequest = HttpRequest.newBuilder().uri(fullUri).GET().build();

        HttpResponse<byte[]> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException | InterruptedException ex) {
            throw new BencodeException("Error while sending request to get tracker: " + ex.getMessage());
        }

        int statusCode = response.statusCode();
        if (statusCode != 200) {
            throw new BencodeException("Status Code not 200 when getting tracker");
        }

        BencodeDecoder bencodeDecoder = new BencodeDecoder();
        return bencodeDecoder.decode(response.body());
    }

    private URI makeFullUrl(String uri, Map<String, String> params) throws BencodeException {
        StringJoiner joiner = new StringJoiner("&");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            joiner.add(URLEncoder.encode(entry.getKey(), StandardCharsets.ISO_8859_1) + "=" +
                    URLEncoder.encode(entry.getValue(), StandardCharsets.ISO_8859_1));
        }

        String finalUrl = uri + "?" + joiner.toString();
        try {
            return new URI(finalUrl);
        } catch (URISyntaxException ex) {
            throw new BencodeException("Cannot make URI object: " + ex.getMessage());
        }
    }
}
