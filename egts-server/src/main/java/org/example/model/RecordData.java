package org.example.model;

import lombok.Data;

@Data
public class RecordData implements BinaryData {
    private SubrecordType subrecordType;
    private short subrecordLength;
    private BinaryData subrecordData;

    @Override
    public BinaryData decode(byte[] content) {
        return null;
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    @Override
    public int length() {
        return 0;
    }
}
