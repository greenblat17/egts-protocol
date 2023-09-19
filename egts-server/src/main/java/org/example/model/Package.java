package org.example.model;

import lombok.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.example.util.CrcUtil.calculateCrc16;
import static org.example.util.CrcUtil.calculateCrc8;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Package implements BinaryData {

    private int protocolVersion;
    private int securityKeyId;
    private String prefix;
    private String route;
    private String encryptionAlg;
    private String compression;
    private String priority;
    private int headerLength;
    private int headerEncoding;
    private int frameDataLength;
    private int packageIdentifier;
    private int packetType;
    private int peerAddress;
    private int recipientAddress;
    private int timeToLive;
    private int headerCheckSum;
    private BinaryData servicesFrameData;
    private int servicesFrameDataCheckSum;

    // Decode разбирает набор байт в структуру пакета
    @Override
    public BinaryData decode(byte[] content) {
        var inputStream = new ByteArrayInputStream(content);
        var in = new BufferedInputStream(inputStream);
        try {
            protocolVersion = Byte.toUnsignedInt(in.readNBytes(1)[0]);
            securityKeyId = Byte.toUnsignedInt(in.readNBytes(1)[0]);

            var flag = Integer.toBinaryString(in.read());
            if (flag.length() < 8) {
                flag = "0".repeat(8 - flag.length()) +
                        flag;
            }

            prefix = String.valueOf(flag.charAt(0)) + flag.charAt(1);        // flags << 7, flags << 6
            route = String.valueOf(flag.charAt(2));                   // flags << 5
            encryptionAlg = String.valueOf(flag.charAt(3)) + flag.charAt(4); // flags << 4, flags << 3
            compression = String.valueOf(flag.charAt(5));             // flags << 2
            priority = String.valueOf(flag.charAt(6)) + flag.charAt(7);                // flags << 1, flags << 0

            headerLength = Byte.toUnsignedInt(in.readNBytes(1)[0]);
            headerEncoding = Byte.toUnsignedInt(in.readNBytes(1)[0]);


            frameDataLength = ByteBuffer.wrap(in.readNBytes(2))
                    .order(ByteOrder.LITTLE_ENDIAN).getShort();
            packageIdentifier = ByteBuffer.wrap(in.readNBytes(2))
                    .order(ByteOrder.LITTLE_ENDIAN).getShort();

            packetType = Byte.toUnsignedInt(in.readNBytes(1)[0]);

            if (route.equals("1")) {
                peerAddress = ByteBuffer.wrap(in.readNBytes(2))
                        .order(ByteOrder.LITTLE_ENDIAN).getShort();
                recipientAddress = ByteBuffer.wrap(in.readNBytes(2))
                        .order(ByteOrder.LITTLE_ENDIAN).getShort();
                timeToLive = Byte.toUnsignedInt(in.readNBytes(1)[0]);
            }

            headerCheckSum = Byte.toUnsignedInt(in.readNBytes(1)[0]);

            var dataFrameBytes = in.readNBytes(frameDataLength);
            BinaryData servicesFrameData;
            switch (packetType) {
                case 1 -> servicesFrameData = new ServiceDataSet();
                case 2 -> servicesFrameData = new PtResponse();
                default -> throw new RuntimeException("Unknown package type: " + packetType);
            }
            servicesFrameData.decode(dataFrameBytes);

            var crcBytes = in.readNBytes(2);
            servicesFrameDataCheckSum = ByteBuffer.wrap(crcBytes)
                    .order(ByteOrder.LITTLE_ENDIAN).getShort();

            byte[] data = new byte[frameDataLength];
            int idx = 0;
            for (int i = headerLength; i < headerLength + frameDataLength; i++) {
                data[idx++] = content[i];
            }
            if (servicesFrameDataCheckSum != calculateCrc16(data)) {
                throw new RuntimeException("Invalid part of package");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    // Encode кодирует струткуру в байтовую строку
    @Override
    public byte[] encode() {
        var bytesOut = new ByteArrayOutputStream();
        try {
            bytesOut.write(protocolVersion);
            bytesOut.write(securityKeyId);

            var flagsBits = prefix + route + encryptionAlg + compression + priority;
            var flags = Short.parseShort(flagsBits);
            bytesOut.write(flags);

            if (headerLength == 0) {
                headerLength = 1;
                if (route.equals("1")) {
                    headerLength += 5;
                }
            }
            bytesOut.write(headerLength);

            bytesOut.write(headerEncoding);

            byte[] sfrd = new byte[0];
            if (servicesFrameData != null) {
                sfrd = servicesFrameData.encode();
            }
            frameDataLength = (short) sfrd.length;

            bytesOut.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putInt(frameDataLength).array());
            bytesOut.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putInt(packageIdentifier).array());
            bytesOut.write(packetType);

            if (route.equals("1")) {
                bytesOut.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putInt(peerAddress).array());
                bytesOut.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putInt(recipientAddress).array());
                bytesOut.write(timeToLive);
            }

            bytesOut.write(calculateCrc8(bytesOut.toByteArray()));

            if (frameDataLength > 0) {
                bytesOut.write(sfrd);
                bytesOut.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putInt(calculateCrc16(sfrd)).array());
            }

            return bytesOut.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int length() {
        return 0;
    }
}
