package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDataRecord implements BinaryData {

    private int recordLength;
    private int recordNumber;
    private String sourceServiceOnDevice;
    private String recipientServiceOnDevice;
    private String group;
    private String recordProcessingPriority;
    private String timeFieldExists;
    private String eventIdFieldExists;
    private String objectIdFieldExists;
    private int objectIdentifier;
    private int eventIdentifier;
    private int time;
    private byte sourceServiceType;
    private byte recipientServiceType;
    private RecordDataSet recordDataSet;

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
