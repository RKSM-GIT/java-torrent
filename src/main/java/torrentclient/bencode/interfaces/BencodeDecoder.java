package bencode.interfaces;

import java.io.IOException;
import java.io.InputStream;

public interface BencodeDecoder {
    Object decode(InputStream inputStream) throws IOException;
}
