package bencode.interfaces;

public interface BencodeString extends BencodeValue {
    byte[] bytes();

    String getString();
}
