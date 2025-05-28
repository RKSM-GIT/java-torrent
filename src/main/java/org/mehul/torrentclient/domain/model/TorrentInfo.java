package org.mehul.torrentclient.domain.model;

import lombok.Getter;

@Getter
public abstract class TorrentInfo {
    protected String name;
    protected int pieceLength;
    protected TorrentFileType torrentType;

}
