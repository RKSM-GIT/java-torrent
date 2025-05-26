package org.mehul.torrentclient.domain.model;

public abstract class TorrentInfo {
    protected String name;
    protected int pieceLength;
    protected String rawPieces;
    protected TorrentFileType torrentType;
}
