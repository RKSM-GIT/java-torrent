package bencode.interfaces;

public interface BencodeValue {
    enum Type {
        STRING, INTEGER, LIST, DICTIONARY
    }

    Type getType();
}
