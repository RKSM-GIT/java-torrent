package org.mehul.torrentclient.torrent;

import lombok.Getter;

@Getter
public abstract class TorrentInfo {
    protected String name;
    protected int maxPieceLength;
    protected TorrentInfoType torrentType;

}
