package org.mehul.torrentclient.tracker;

import lombok.*;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.util.ByteUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Peer {
    private String ipv4Address;
    private int port;

    public static List<Peer> peerListFromBytes(byte[] peerBytes) throws BencodeException {
        int n = peerBytes.length;
        if (n % 6 != 0) {
            throw new BencodeException("Each peer must be 6 bytes long");
        }

        List<Peer> res = new ArrayList<>(n / 6);

        for (int i = 0; i < n; i += 6) {
            byte[] currPeerBytes = ByteUtil.copyArray(peerBytes, i, 6);
            res.add(Peer.fromBytes(currPeerBytes));
        }

        return res;
    }

    public static Peer fromBytes(byte[] peerBytes) throws BencodeException {
        if (peerBytes.length != 6) {
            throw new BencodeException("Peer must be of 6 bytes");
        }

        String ip = String.format("%d.%d.%d.%d",
                peerBytes[0] & 0xff,
                peerBytes[1] & 0xff,
                peerBytes[2] & 0xff,
                peerBytes[3] & 0xff
        );
        int port = ((peerBytes[4] & 0xff) << 8) | (peerBytes[5] & 0xff);

        return new Peer(ip, port);
    }
}
