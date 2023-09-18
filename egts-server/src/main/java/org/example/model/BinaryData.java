package org.example.model;

public interface BinaryData {
    BinaryData decode(byte[] content);
    byte[] encode();
    int length();
}
