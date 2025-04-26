package bencode.interfaces;

import java.util.List;

public interface BencodeList extends BencodeValue {
    List<Object> getValue();
}
