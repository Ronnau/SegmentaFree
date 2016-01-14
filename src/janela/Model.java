package janela;

import dados.Exame;
import java.awt.image.BufferedImage;

/**
 *
 * @author Rodrigo
 */
public class Model {

    public static final int DIRETORIO = 1;
    public static final int ARQUIVO = 2;

    Exame exame;

    public Model(int tipo, String localizacao) throws Exception {

        System.out.println("Diretório/arquivo: " + localizacao);

        if (tipo == DIRETORIO) {
            exame = new Exame(Exame.DIRETORIO, localizacao);
        } else {
            exame = new Exame(Exame.ARQUIVO, localizacao);
        }
    }

    public String getNomeArquivo(final int fatia) {
        return exame.getNomeArquivo(fatia);
    }

    BufferedImage getImagemFatia(int indice, int WL, int WW) {
        return exame.getImagemFatia(indice, WL, WW);
    }

    int getNumeroFatias() {
        return exame.getNumeroFatias();
    }

    boolean[][] getMatrizPulmaoEsq(int indice){
        return exame.getFatia(indice).getPulmaoEsq();
    }

    int getTamanhoPulmaoEsq(int indice) {
        return exame.getFatia(indice).getTamanhoPulmaoEsq();
    }
    
    boolean[][] getMatrizPulmaoDir(int indice){
        return exame.getFatia(indice).getPulmaoDir();
    }

    int getTamanhoPulmaoDir(int indice) {
        return exame.getFatia(indice).getTamanhoPulmaoDir();
    }
    
    int[][] getMatrizOriginal(int indice){
        return exame.getFatia(indice).getMatrizCoeficientes();
    }

    float getEspessuraFatia(int indice) {
        return exame.getFatia(indice).getSliceThickness();
    }

}
