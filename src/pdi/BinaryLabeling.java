/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdi;

import utils.Utils;

/**
 *
 * @author RodrigoPC
 */
public class BinaryLabeling {

    int total = 0;
    int[][] labelledMatrix;

    public int getTotal() {
        return total;
    }

    public int[][] getLabelledMatrix() {
        return labelledMatrix;
    }

    public int getTamanhoTotal(int label) {
        label++;
        int tamanhoTotal = 0;
        for (int y = 0; y < labelledMatrix[0].length; y++) {
            for (int x = 0; x < labelledMatrix.length; x++) {
                if (labelledMatrix[x][y] == label) {
                    tamanhoTotal++;
                }
            }
        }
        return tamanhoTotal;
    }
    
    public boolean[][] getMatrizBinLabel (int label) {
        boolean[][] matrizLabel = new boolean[labelledMatrix.length][labelledMatrix[0].length];
        label++;
        for (int y = 0; y < labelledMatrix[0].length; y++) {
            for (int x = 0; x < labelledMatrix.length; x++) {
                if (labelledMatrix[x][y] == label) {
                    matrizLabel[x][y] = true;
                } else {
                    matrizLabel[x][y] = false;                    
                }
            }
        }
        return matrizLabel;
    }
    
    public BinaryLabeling(int[][] mtzTrabalho) {

        labelledMatrix = Utils.copyArray(mtzTrabalho);

        int m = 2;

        for (int x = 1; x < (labelledMatrix.length - 1); x++) {
            for (int y = 1; y < (labelledMatrix[x].length - 1); y++) {
                if (labelledMatrix[x][y] == 1) {
                    compLabel(x, y, m++);
                }
            }
        }

        total = m - 2;
        
    }

    /*
        ATENÇÃO!!!
        Foi necessário acrescentar o parâmetro -Xss10m para aumentar o CallStack da JVM!
        Sem isso a recursividade abaixo vai causar erro na execução!
    */
    
    // ATENÇÃO 2: O trecho comentado abaixo faz com que o algoritmo trabalhe somente em 4 direções, e não em 8 (sem as diagonais)

    private void compLabel(int i, int j, int m) {
        if (labelledMatrix[i][j] == 1) {
            labelledMatrix[i][j] = m;
            //compLabel(i - 1, j - 1, m);
            compLabel(i - 1, j, m);
            //compLabel(i - 1, j + 1, m);
            compLabel(i, j - 1, m);
            compLabel(i, j + 1, m);
            //compLabel(i + 1, j - 1, m);
            compLabel(i + 1, j, m);
            //compLabel(i + 1, j + 1, m);
        }
    }

}
