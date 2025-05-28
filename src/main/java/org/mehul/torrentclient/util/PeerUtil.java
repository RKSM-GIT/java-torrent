package org.mehul.torrentclient.util;

import java.util.Random;

public class PeerUtil {
    public static byte[] generatePeerId() {
        Random random = new Random();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);

        return bytes;
    }
}
