package pdi;

/**
 *
 * @author RodrigoPC
 */
public class Convolution {
    public static int[][] aplicaConvolucao1D(final int[][] data, final double[] kernel) {
        final int width = data.length;
        final int height = data[0].length;
        final int ignoredBorderLength = kernel.length / 2;

        // percorre a matriz no eixo X, aplicando o kernel horizontalmente
        final int[][] xPass = new int[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < xPass.length; x++) {
                if ((x < ignoredBorderLength) || (x > width - ignoredBorderLength - 1)) {
                    xPass[x][y] = data[x][y];
                    continue;
                }

                xPass[x][y] = 0;

                for (int i = 0; i < kernel.length; i++) {
                    xPass[x][y] += data[(x - ignoredBorderLength) + i][y] * kernel[i];
                }
            }
        }
        // -->

        // percorre a matriz no eixo Y, aplicando o kernel verticalmente
        final int[][] yPass = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if ((y < ignoredBorderLength) || (y > height - ignoredBorderLength - 1)) {
                    xPass[x][y] = data[x][y];
                    continue;
                }

                yPass[x][y] = 0;

                for (int i = 0; i < kernel.length; i++) {
                    yPass[x][y] += data[x][(y - ignoredBorderLength) + i] * kernel[i];
                }
            }
        }
        // -->

        // define a matriz de saída
        final int[][] result = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                result[x][y] = (xPass[x][y] + yPass[x][y]) / 2;
            }
        }
        // -->

        return result;
    }

    public static int[][] aplicaConvolucao2D(int[][] mtzTrabalho, double[][] kernel) {
        int[][] mtzSaida = new int[mtzTrabalho.length][mtzTrabalho[0].length];
        
        
        return mtzSaida;
    }

}
