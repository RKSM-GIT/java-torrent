# Java Torrent Client

A robust and efficient BitTorrent client implementation in Java that demonstrates advanced networking concepts and protocol implementation.

## 🚀 Features

- **Bencode Protocol Implementation**: Custom implementation of the Bencode encoding/decoding protocol used in BitTorrent
- **Peer-to-Peer Communication**: Direct peer connections with proper handshake protocol
- **Piece Management**: Efficient downloading and verification of torrent pieces
- **Tracker Communication**: HTTP-based tracker communication for peer discovery
- **SHA-1 Verification**: Built-in piece verification using SHA-1 hashing
- **Robust Error Handling**: Comprehensive error handling and logging throughout the application

## 🛠️ Technical Implementation

- **Protocol Implementation**:
  - BitTorrent handshake protocol
  - Peer wire protocol
  - Tracker HTTP protocol
  - Bencode encoding/decoding

- **Core Components**:
  - `BencodeApi`: Handles encoding/decoding of torrent files
  - `PeerDownloader`: Manages peer connections and piece downloads
  - `TcpPeerConnection`: Handles TCP socket communication
  - `MetaInfoFile`: Processes torrent metadata
  - `SingleFileTorrentInfo`: Manages single-file torrent information

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## 🚀 Getting Started

1. Clone the repository:
```bash
git clone https://github.com/yourusername/java-torrent.git
cd java-torrent
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn exec:java -Dexec.mainClass="org.mehul.torrentclient.TorrentApp" -Dexec.args="path/to/your/torrent.torrent"
```

## 🔧 Project Structure

```
src/main/java/org/mehul/torrentclient/
├── bencode/           # Bencode protocol implementation
├── handshake/         # BitTorrent handshake protocol
├── peer/             # Peer communication and piece management
├── torrent/          # Torrent file processing
├── tracker/          # Tracker communication
└── util/             # Utility classes
```

## 🛠️ Dependencies

- Lombok: Reduces boilerplate code
- SLF4J: Logging framework
- JUnit: Testing framework

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the issues page.

## 👨‍💻 Author

**RKS Mehul**
- GitHub: [@RKSM-GIT](https://github.com/RKSM-GIT)

## 🙏 Acknowledgments

- BitTorrent Protocol Specification
- Java Networking APIs
- Maven Build System
