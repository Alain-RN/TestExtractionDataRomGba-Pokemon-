import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;

public class GbaSpriteToPng {

    public static int[] readPalette(byte[] paletteData) {
        int[] colors = new int[16]; // 16 couleurs pour une palette 4bpp
        for (int i = 0; i < 16; i++) {
            int color = (paletteData[i * 2] & 0xFF) | ((paletteData[i * 2 + 1] & 0xFF) << 8);
            int r = (color & 0x1F) << 3;
            int g = ((color >> 5) & 0x1F) << 3;
            int b = ((color >> 10) & 0x1F) << 3;
            colors[i] = (r << 16) | (g << 8) | b;
        }
        return colors;
    }


    public static BufferedImage createImageFromTileData(byte[] tileData, int width, int height, int[] palette) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int tileWidth = 8;
        int tilesPerRow = width / tileWidth;

        for (int tileIndex = 0; tileIndex < tileData.length / 32; tileIndex++) {
            int tileX = (tileIndex % tilesPerRow) * tileWidth;
            int tileY = (tileIndex / tilesPerRow) * tileWidth;

            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
                    int byteIndex = tileIndex * 32 + y * 4 + x / 2;
                    int value = tileData[byteIndex] & 0xFF;
                    int colorIndex = (x % 2 == 0) ? (value & 0xF) : (value >> 4);
                    int rgb = palette[colorIndex];
                    image.setRGB(tileX + x, tileY + y, rgb);
                }
            }
        }
        return image;
    }

    public static BufferedImage createTileImage(byte[] tileData, int[] palette) {
        int size = 8;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        int pixelIndex = 0;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x += 2) {
                int byteIndex = pixelIndex / 2;
                int data = tileData[byteIndex] & 0xFF;

                int colorLow = data & 0x0F;
                int colorHigh = (data >> 4) & 0x0F;

                image.setRGB(x, y, palette[colorLow]);
                image.setRGB(x + 1, y, palette[colorHigh]);

                pixelIndex += 2;
            }
        }

        return image;
    }


    public static void main(String[] args) throws IOException {
        // 📝 Chemin vers la ROM GBA
        String romPath = "C:/Users/STRIX/Desktop/Tuto Mamy/pokemon_gaia_v3.2.gba"; // <--- MODIFIE ICI

        // 📍 Offset de départ pour les données compressées (sprite)
        int spriteOffset = 0xAEB9F8;

        // 🎨 Offset pour la palette 16 couleurs (non compressée)
        int paletteOffset = 0xE69E94;

        // 🖼️ Nom de l’image de sortie
        String outputImage = "C:/Users/STRIX/Desktop/sprite_output.png";
        String outputImage2 = "C:/Users/STRIX/Desktop/sprite_output2.png";


        // 📦 Lire la ROM
        byte[] romData = Files.readAllBytes(Paths.get(romPath));
        byte[] decompressedData = GbaLZ77Extractor.decompressLZ77(romData, spriteOffset);

        System.out.print(GbaLZ77Extractor.getDecompressLength(romData, spriteOffset));




        byte[] decompressedPalette = GbaLZ77Extractor.decompressLZ77(romData, paletteOffset);
        int[] palette = readPalette(decompressedPalette); // Utiliser les données décompressées

        // 🧱 Dimensions (modifiables selon le sprite)
//        int tileCount = decompressedData.length / 32;
//        int tilesPerRow = 8; // nombre de tiles par ligne (modifiable)
//        int rows = (int) Math.ceil(tileCount / (double) tilesPerRow);
        byte[] tilesete = decompressedData;

        int tileCount = decompressedData.length / 32; // chaque tile fait 32 octets (8x8x4bpp)
        int tilesPerRow = (int) Math.ceil(Math.sqrt(tileCount)); // carré
        int rows = tilesPerRow;


        BufferedImage image = createImageFromTileData(decompressedData, tilesPerRow * 8, rows * 8, palette);


        ImageIO.write(image, "png", new File(outputImage));

        System.out.println("✅ Image générée : " + outputImage);

        int tileIndex = 0x257;
        byte[] a = new byte[32];
        System.arraycopy(tilesete, tileIndex * 32, a, 0, 32);

        BufferedImage image2 = createTileImage(a, palette);
        ImageIO.write(image2, "png", new File(outputImage2));

    }
}
