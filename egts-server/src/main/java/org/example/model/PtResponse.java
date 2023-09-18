package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PtResponse implements BinaryData {

    private int responsePacketId;
    private int processingResult;
    private BinaryData sdr;

    @Override
    public BinaryData decode(byte[] content) {
        var inputStream = new ByteArrayInputStream(content);
        var in = new BufferedInputStream(inputStream);
        try {
            responsePacketId = ByteBuffer.wrap(in.readNBytes(2)).order(ByteOrder.LITTLE_ENDIAN).getInt();
            processingResult = in.readNBytes(1)[0];
            if (in.available() > 0) {
                sdr = new ServiceDataSet();
                sdr.decode(in.readNBytes(in.available()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public byte[] encode() {
        var bytesOut = new ByteArrayOutputStream();
        try {
            bytesOut.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putInt(responsePacketId).array());
            bytesOut.write(processingResult);
            if (sdr != null) {
                var sdrBytes = sdr.encode();
                bytesOut.write(sdrBytes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytesOut.toByteArray();
    }

    @Override
    public int length() {
        var recBytes = this.encode();
        return recBytes.length;
    }
}
