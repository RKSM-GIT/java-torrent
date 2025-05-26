package org.mehul.torrentclient.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ByteUtil {
    public static String bytesToHexString(byte[] source) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : source) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static String bytesToString(byte[] source) {
        return new String(source, StandardCharsets.ISO_8859_1);
    }

    public static List<byte[]> splitBytesByLength(byte[] source, int length) {
        List<byte[]> res = new ArrayList<>();
        int n = source.length;
        int ind = 0;

        while (ind < n) {
            res.add(copyArray(source, ind, length));
            ind += length;
        }

        return res;
    }

    public static byte[] copyArray(byte[] source, int sourceOffset, int length) {
        byte[] dest = new byte[length];
        System.arraycopy(source, sourceOffset, dest, 0, length);
        return dest;
    }

    public static String bytesToIpAndPort(byte[] peersBytes) {
        String ip = String.format("%d.%d.%d.%d", peersBytes[0] & 0xff, peersBytes[1] & 0xff,
                peersBytes[2] & 0xff, peersBytes[3] & 0xff);
        int port = ((peersBytes[4] & 0xff) << 8) | (peersBytes[5] & 0xff);

        return ip + ":" + port;
    }
}
