import java.io.*;
import java.nio.file.*;

public class GbaLZ77Extractor {

    public static byte[] decompressLZ77(byte[] data, int offset) throws IOException {
        if (data[offset] != 0x10)
            throw new IOException("Invalid magic number at offset " + String.format("0x%X", offset));

        int decompressedLength = (data[offset + 1] & 0xFF) |
                ((data[offset + 2] & 0xFF) << 8) |
                ((data[offset + 3] & 0xFF) << 16);
        byte[] output = new byte[decompressedLength];

        int src = offset + 4;
        int dst = 0;

        while (dst < decompressedLength) {
            int flags = data[src++] & 0xFF;
            for (int i = 0; i < 8 && dst < decompressedLength; i++) {
                if ((flags & (0x80 >> i)) == 0) {
                    // Uncompressed byte
                    output[dst++] = data[src++];
                } else {
                    // Compressed block
                    int byte1 = data[src++] & 0xFF;
                    int byte2 = data[src++] & 0xFF;

                    int disp = ((byte1 & 0x0F) << 8) | byte2;
                    int length = (byte1 >> 4) + 3;

                    for (int j = 0; j < length; j++) {
                        output[dst] = output[dst - disp - 1];
                        dst++;
                    }
                }
            }
        }

        return output;
    }

    public static int getDecompressLength(byte[] data, int offset)
    {
        return (data[offset + 1] & 0xFF) |
                ((data[offset + 2] & 0xFF) << 8) |
                ((data[offset + 3] & 0xFF) << 16);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: java GbaLZ77Extractor <rom.gba> <offsetHex> <output.bin>");
            return;
        }

        String romPath = args[0];
        int offset = Integer.parseInt(args[1], 16);
        String outputPath = args[2];

        byte[] romData = Files.readAllBytes(Paths.get(romPath));
        byte[] decompressed = decompressLZ77(romData, offset);

        Files.write(Paths.get(outputPath), decompressed);
        System.out.println("Décompression terminée. Taille : " + decompressed.length + " octets");
    }
}
