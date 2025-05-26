package org.mehul.torrentclient.domain.model;

import java.util.List;

public abstract class TorrentInfo {
    protected String name;
    protected int pieceLength;
    protected List<byte[]> pieceHashes;
    protected TorrentFileType torrentType;

    public String getName() {
        return name;
    }

    public int getPieceLength() {
        return pieceLength;
    }

    public List<byte[]> getPieceHashes() {
        return pieceHashes;
    }

    public TorrentFileType getTorrentType() {
        return torrentType;
    }
}
