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
public class Threshold {

    public int[][] aplicaBinThreshold(int[][] mtzTrabalho, int limiar, int valorAbaixo, int valorAcima) {
        
        int[][] matriz = Utils.copyArray(mtzTrabalho);
                
        for (int x = 0; x < matriz.length; x++) {
            for (int y = 0; y < matriz[x].length; y++) {
                if (matriz[x][y] >= limiar){
                    matriz[x][y] = valorAcima;                    
                } else {
                    matriz[x][y] = valorAbaixo;
                }
            }
        }
        return matriz;
    }
}
