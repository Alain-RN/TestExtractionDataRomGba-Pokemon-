import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageFromUnicode {

    public static void main(String[] args) {
        try {
            // La chaîne avec les codes hexadécimaux Unicode que tu as fournis,
            // ici on va la convertir en caractères Unicode.
            String unicodeString = "0408 0420 0420 042E 042F 0420 042D 0421 0422 0420 0420 0420 0420 0408 0431 0432 0436 0437 0428 0435 0429 042A 0428 0428 0416 0417 0408 3039 303A 303E 303F 3009 303D 3009 3009 3009 301D 041E 041F 0408 3009 3001 3001 3043 3044 3044 3044 3044 3046 3025 3026 3027 0408 3009 3001 3001 3053 304B 044C 044D 304E 3056 3001 3001 3001 0408 3009 3001 3001 3053 304B 0454 0455 304E 3056 3001 3001 3001 0408 3057 3001 3001 305B 305C 305C 305C 305C 305E 3001 3001 3047 0408 045F 3001 3001 3001 3001 3001 3001 3001 3001 3001 3001 044F 0408 3009 3001 3012 3013 3014 3001 3001 3001 3001 3001 3001 3001 0408 0408 0408 041A 041B 041C 0408 0408 0408 0408 0408 0408 0408";

            // On va essayer de transformer les codes hexadécimaux en caractères réels.
            // Pour cela on extrait uniquement les codes hex et on convertit.
            StringBuilder text = new StringBuilder();
            String[] parts = unicodeString.split("\\s+");
            for (String part : parts) {
                // On ne traite que les codes hex (longueur 4 caractères)
                if (part.matches("[0-9A-Fa-f]{4}")) {
                    int codePoint = Integer.parseInt(part, 16);
                    text.append(Character.toChars(codePoint));
                } else {
                    // Ajoute les autres mots ou caractères tels quels (ex: ^misc.temp.BPRE_2D50FC`blm`)
                    text.append(part).append(" ");
                }
            }

            // Préparation de l'image
            int width = 1200;
            int height = 400;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();

            // Fond blanc
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            // Texte noir
            g.setColor(Color.BLACK);
            g.setFont(new Font("Serif", Font.PLAIN, 24));
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Affichage du texte dans l'image
            int x = 20;
            int y = 40;
            for (String line : text.toString().split(" ")) {
                g.drawString(line, x, y);
                x += g.getFontMetrics().stringWidth(line) + 10;
                if (x > width - 200) { // Retour à la ligne
                    x = 20;
                    y += 30;
                }
            }

            g.dispose();

            // Sauvegarder l'image en PNG
            ImageIO.write(image, "png", new File("C:/Users/STRIX/Desktop/output_unicode.png"));
            System.out.println("Image créée : output_unicode.png");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
