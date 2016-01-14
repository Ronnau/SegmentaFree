
package processamento;

import java.awt.image.BufferedImage;

/**
 *
 * @author Rodrigo
 */
public class Auxiliar {
    public static int[][] bufferedImageToArrayInt(BufferedImage imagem) {
        int x = imagem.getWidth();
        int y = imagem.getHeight();
        int[][] matriz = new int[x][y];
        
        for (int ix = 0; ix < x; ix++) {
            for (int iy = 0; iy < y; iy++) {
                matriz[ix][iy] = imagem.getRGB(ix, iy);
            }
        }
        return matriz;
    }
}
