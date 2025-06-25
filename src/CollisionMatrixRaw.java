import java.io.FileInputStream;
import java.io.IOException;

public class CollisionMatrixRaw {

    // Lecture brute des permissions (1er octet) depuis le fichier de collision
    public static byte[] readAndReturnPermissions(String path) throws IOException {
        FileInputStream input = new FileInputStream(path);
        int fileSize = input.available();
        int totalBlocks = fileSize / 2;

        byte[] collisions = new byte[totalBlocks];

        for (int i = 0; i < totalBlocks; i++) {
            int movement = input.read(); // 1er octet = permission
            input.read(); // 2e octet = terrain (on l'ignore)
            if (movement == -1) throw new IOException("Fin de fichier inattendue.");
            collisions[i] = (byte) movement;
        }

        input.close();
        return collisions;
    }

    // Affichage formaté de la matrice des collisions
    public static void printMatrixFromFlatArray(byte[] flatArray, int width) {
        int height = (int) Math.ceil(flatArray.length / (double) width);
        System.out.print("[");
        for (int y = 0; y < height; y++) {
            System.out.print("[");
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                if (index < flatArray.length) {
                    System.out.print(flatArray[index] & 0xFF); // afficher en int non signé
                } else {
                    System.out.print("0"); // remplir avec 0 si on dépasse
                }

                if (x < width - 1) System.out.print(", ");
            }
            System.out.print("]");
            if (y < height - 1) System.out.println(",");
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
        String collisionPath = "D:/telechargement/metatile_attributes.bin"; // 🔁 Remplace par ton vrai chemin
        int width = 24; // largeur de la matrice souhaitée

        try {
            byte[] collisions = readAndReturnPermissions(collisionPath);
            printMatrixFromFlatArray(collisions, width);
        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
