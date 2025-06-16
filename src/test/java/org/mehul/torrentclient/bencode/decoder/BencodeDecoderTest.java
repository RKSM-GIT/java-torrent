package org.mehul.torrentclient.bencode.decoder;

import org.junit.jupiter.api.Test;
import org.mehul.torrentclient.bencode.exception.BencodeException;
import org.mehul.torrentclient.bencode.model.*;

import static org.junit.jupiter.api.Assertions.*;

class BencodeDecoderTest {
    private final BencodeDecoder decoder = new BencodeDecoder();

    @Test
    void decodeNumber_ValidPositiveNumber() {
        byte[] input = "i42e".getBytes();
        Bencode result = decoder.decode(input);
        assertInstanceOf(BencodeNumber.class, result);
        assertEquals(42L, ((BencodeNumber) result).getValue());
    }

    @Test
    void decodeNumber_ValidNegativeNumber() {
        byte[] input = "i-42e".getBytes();
        Bencode result = decoder.decode(input);
        assertInstanceOf(BencodeNumber.class, result);
        assertEquals(-42L, ((BencodeNumber) result).getValue());
    }

    @Test
    void decodeNumber_InvalidFormat() {
        byte[] input = "i42".getBytes();
        assertThrows(BencodeException.class, () -> decoder.decode(input));
    }

    @Test
    void decodeString_ValidString() {
        byte[] input = "4:spam".getBytes();
        Bencode result = decoder.decode(input);
        assertInstanceOf(BencodeString.class, result);
        assertEquals("spam", ((BencodeString) result).asString());
    }

    @Test
    void decodeString_InvalidLength() {
        byte[] input = "5:spam".getBytes();
        assertThrows(BencodeException.class, () -> decoder.decode(input));
    }

    @Test
    void decodeList_ValidList() {
        byte[] input = "l4:spam4:eggse".getBytes();
        Bencode result = decoder.decode(input);
        assertInstanceOf(BencodeList.class, result);
        BencodeList list = (BencodeList) result;
        assertEquals(2, list.getValue().size());
        assertEquals("spam", ((BencodeString) list.getValue().get(0)).asString());
        assertEquals("eggs", ((BencodeString) list.getValue().get(1)).asString());
    }

    @Test
    void decodeList_EmptyList() {
        byte[] input = "le".getBytes();
        Bencode result = decoder.decode(input);
        assertInstanceOf(BencodeList.class, result);
        assertEquals(0, ((BencodeList) result).getValue().size());
    }

    @Test
    void decodeDictionary_ValidDictionary() {
        byte[] input = "d3:cow3:moo4:spam4:eggse".getBytes();
        Bencode result = decoder.decode(input);
        assertInstanceOf(BencodeDictionary.class, result);
        BencodeDictionary dict = (BencodeDictionary) result;
        assertEquals("moo", ((BencodeString) dict.getValue().get("cow")).asString());
        assertEquals("eggs", ((BencodeString) dict.getValue().get("spam")).asString());
    }

    @Test
    void decodeDictionary_EmptyDictionary() {
        byte[] input = "de".getBytes();
        Bencode result = decoder.decode(input);
        assertInstanceOf(BencodeDictionary.class, result);
        assertEquals(0, ((BencodeDictionary) result).getValue().size());
    }

    @Test
    void decode_ComplexNestedStructure() {
        byte[] input = "d4:listl4:spam4:eggse3:numi42e4:str4:teste".getBytes();
        Bencode result = decoder.decode(input);
        assertInstanceOf(BencodeDictionary.class, result);
        BencodeDictionary dict = (BencodeDictionary) result;

        BencodeList list = (BencodeList) dict.getValue().get("list");
        assertEquals(2, list.getValue().size());
        assertEquals("spam", ((BencodeString) list.getValue().get(0)).asString());
        assertEquals("eggs", ((BencodeString) list.getValue().get(1)).asString());

        assertEquals(42L, ((BencodeNumber) dict.getValue().get("num")).getValue());
        assertEquals("test", ((BencodeString) dict.getValue().get("str")).asString());
    }

    @Test
    void decode_InvalidInput() {
        byte[] input = "invalid".getBytes();
        assertThrows(BencodeException.class, () -> decoder.decode(input));
    }

    @Test
    void decode_TrailingData() {
        byte[] input = "i42eextra".getBytes();
        assertThrows(BencodeException.class, () -> decoder.decode(input));
    }
} 