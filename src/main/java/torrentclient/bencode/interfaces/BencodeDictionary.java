package bencode.interfaces;

import java.util.Map;

public interface BencodeDictionary extends BencodeValue {
    Map<String, Object> getValue();
}
