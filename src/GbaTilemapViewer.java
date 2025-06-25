import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class GbaTilemapViewer extends JPanel {
    private final int tileWidth = 8;
    private final int tileHeight = 8;
    private final int mapWidth;
    private final int mapHeight;
    private int[][] tilemap;
    private byte[] tileset;
    private Color[] palette;

    // Offsets tirés du header (en hex)
    private final int blockmapAddr;   // Adresse dans la ROM du tilemap
    private final int blockdata1Addr; // Adresse tileset
    private final String gbaFilePath;

    public GbaTilemapViewer(String gbaFilePath, int mapWidth, int mapHeight,
                                   int blockmapAddr, int blockdata1Addr) throws IOException {
        this.gbaFilePath = gbaFilePath;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.blockmapAddr = blockmapAddr;
        this.blockdata1Addr = blockdata1Addr;

        setPreferredSize(new Dimension(mapWidth * tileWidth, mapHeight * tileHeight));

        loadRomData();
    }

    private void loadRomData() throws IOException {
        File gbaFile = new File(gbaFilePath);
        byte[] romData = Files.readAllBytes(gbaFile.toPath());

        // 1. Lire tilemap : mapWidth * mapHeight tiles, chaque tilemap entry = 2 bytes (short)
        tilemap = new int[mapHeight][mapWidth];
        int offset = blockmapAddr;
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                // 2 bytes little endian
                int low = romData[offset] & 0xFF;
                int high = romData[offset + 1] & 0xFF;
                int tileIndex = (high << 8) | low;
                tilemap[y][x] = tileIndex;
                offset += 2;
            }
        }

        // 2. Lire tileset : On charge un nombre arbitraire de tiles, ici 50 tiles (tu peux ajuster)
        int tilesCount = 50;
        int tilesetSize = 32 * tilesCount; // 32 bytes par tile (4bpp 8x8)
        tileset = new byte[tilesetSize];
        System.arraycopy(romData, blockdata1Addr, tileset, 0, tilesetSize);

        // 3. Charger palette (GBA palette 16 couleurs = 32 bytes) - souvent à une adresse fixe
        // Exemple : adresse palette standard à 0x00000120 (à ajuster si tu as palette spécifique)
        // Pour l'exemple on prend une palette fixe (ex: dégradé simple)
        palette = new Color[16];
        // Si tu veux lire la palette depuis la ROM, il faudrait connaître l'adresse exacte.
        // Ici on fait une palette simple en dégradé de gris :
        for (int i = 0; i < 16; i++) {
            int v = 255 * i / 15;
            palette[i] = new Color(v, v, v);
        }
    }

    private Color getTilePixelColor(int tileIndex, int x, int y) {
        int tileOffset = tileIndex * 32;
        int byteIndex = tileOffset + (y * 4) + (x / 2);
        int data = tileset[byteIndex] & 0xFF;
        int colorIndex;
        if (x % 2 == 0) {
            colorIndex = data & 0x0F;
        } else {
            colorIndex = (data >> 4) & 0x0F;
        }
        return palette[colorIndex];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Fond blanc
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Dessiner chaque tile
        for (int ty = 0; ty < mapHeight; ty++) {
            for (int tx = 0; tx < mapWidth; tx++) {
                int tileIndex = tilemap[ty][tx];
                for (int py = 0; py < tileHeight; py++) {
                    for (int px = 0; px < tileWidth; px++) {
                        Color c = getTilePixelColor(tileIndex, px, py);
                        g.setColor(c);
                        g.fillRect(tx * tileWidth + px, ty * tileHeight + py, 1, 1);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        // Données extraites du header que tu as fourni
        int mapWidth = 13;
        int mapHeight = 10;
        int blockmapAddr = 0xE92698;    // Adresse tilemap dans la ROM
        int blockdata1Addr = 0xE92754;  // Adresse tileset dans la ROM

        String gbaFilePath = "C:/Users/STRIX/Desktop/Tuto Mamy/pokemon_gaia_v3.2.gba";

        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("GBA Tilemap Viewer from ROM");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new GbaTilemapViewer(gbaFilePath, mapWidth, mapHeight, blockmapAddr, blockdata1Addr));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erreur lecture ROM: " + e.getMessage());
            }
        });
    }
}
