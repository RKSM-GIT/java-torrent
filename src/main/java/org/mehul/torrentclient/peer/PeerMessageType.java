package org.mehul.torrentclient.peer;

import lombok.Getter;

@Getter
public enum PeerMessageType {

    CHOKE(0),
    UNCHOKE(1),
    INTERESTED(2),
    NOT_INTERESTED(3),
    HAVE(4),
    BITFIELD(5),
    REQUEST(6),
    PIECE(7),
    CANCEL(8);

    private final int id;

    PeerMessageType(int id) {
        this.id = id;
    }

    public static PeerMessageType fromId(int id) throws IllegalArgumentException {
        for (PeerMessageType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }

        throw new IllegalArgumentException("Id must be between 0 and 8 (inclusive)");
    }
}
