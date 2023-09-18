package org.example.model;

import lombok.Data;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@Data
public class RecordDataSet implements BinaryData {

    private List<RecordData> recordDataList = new ArrayList<>();

    @Override
    public BinaryData decode(byte[] recDS) {
        var inputStream = new ByteArrayInputStream(recDS);
        var in = new BufferedInputStream(inputStream);
        while (true) {
            try {
                if (!(in.available() > 0)) break;

                var rd = new RecordData();
                var subrecordType = SubrecordType.fromId(in.read());
                var subrecordLength = ByteBuffer.wrap(inputStream.readNBytes(2))
                        .order(ByteOrder.LITTLE_ENDIAN).getShort();
                var subrecordBytes = in.readNBytes(subrecordLength);
                var subRecordData = new SrResponse();
                subRecordData.decode(subrecordBytes);

                subrecordType.ifPresent(rd::setSubrecordType);
                rd.setSubrecordLength(subrecordLength);
                rd.setSubrecordData(subRecordData);
                recordDataList.add(rd);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    @Override
    public byte[] encode() {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        try {
            for (RecordData rd : recordDataList) {
                if (rd.getSubrecordType() == null) {
                    rd.setSubrecordType(SubrecordType.POS_Data);
                }
                bytesOut.write(rd.getSubrecordType().getId());

                if (rd.getSubrecordLength() == 0) {
                    rd.setSubrecordLength((short) rd.getSubrecordData().length());
                }

                bytesOut.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(rd.getSubrecordLength()).array());
                bytesOut.write(rd.getSubrecordData().encode());
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
