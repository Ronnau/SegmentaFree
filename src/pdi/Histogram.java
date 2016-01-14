/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdi;

/**
 *
 * @author RodrigoPC
 */
public class Histogram {

    private int offset;
    private int[] histograma;

    public Histogram(int[][] mtzTrabalho, int limiteInferior, int limiteSuperior) {
        histograma = new int[limiteSuperior - limiteInferior + 1];

        if (limiteInferior < 0){
            offset = limiteInferior * -1;
        } else {
            offset = limiteInferior;
        }

        for (int x = 0; x < mtzTrabalho.length; x++) {
            for (int y = 0; y < mtzTrabalho[x].length; y++) {
                if( mtzTrabalho[x][y] < limiteInferior || mtzTrabalho[x][y] > limiteSuperior){
                    continue;
                }
                histograma[mtzTrabalho[x][y] + offset]++;
            }
        }
    }

    public int getMaxOcorrencias(int limiteInferior, int limiteSuperior) {
        int maxOcorrencias = limiteInferior;
        for (int i = 0; i < histograma.length; i++) {
            if( i - offset < limiteInferior || i - offset > limiteSuperior || i == 0){
                continue;
            }
            if (histograma[i] > histograma[maxOcorrencias + offset]){
                maxOcorrencias = i - offset;
            }
        }        
        return maxOcorrencias;
    }
    
    public int getLocalMinima(int limiteInferior, int limiteSuperior) {
        int localMinima = limiteInferior;
        for (int i = 0; i < histograma.length; i++) {
            if( i - offset < limiteInferior || i - offset > limiteSuperior || i == 0){
                continue;
            }
            if (histograma[i] < histograma[localMinima + offset]){
                localMinima = i - offset;
            }
        }        
        return localMinima;
    }
    
}
