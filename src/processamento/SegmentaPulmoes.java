package processamento;

import dados.Exame;
import pdi.BinaryLabeling;
import pdi.GaussianLPF;
import pdi.Histogram;
import pdi.Threshold;

import static utils.Utils.copyArray;

/**
 *
 * @author Rodrigo
 */
class SegmentaPulmoes {

    private final Exame exame;

    int[][] mtzTrabalho;
    int labelMaior1, labelMaior2, tamanho, maior1, maior2;

    SegmentaPulmoes(Exame exame) {
        this.exame = exame;
    }

    void segmenta() {

        for (int indiceFatia = 0; indiceFatia < exame.getNumeroFatias(); indiceFatia++) {
            segmentaFatia(indiceFatia);
        }

    }

    private void segmentaFatia(int indiceFatia) {

        //deve copiar o conteudo da matriz pois NÃO deve alterar o matriz de coeficientes original
        mtzTrabalho = copyArray(exame.getFatia(indiceFatia).getMatrizCoeficientes());

        //JOptionPane.showMessageDialog(null,"Matriz de Coeficientes");
        //new VisualizaImagem(indiceFatia, exame, mtzTrabalho, -400, 1500);

        //converte qualquer valor fora da faixa de valores válidos para um valor conhecido de background
        for (int y = 0; y < mtzTrabalho[0].length; y++) {
            for (int x = 0; x < mtzTrabalho.length; x++) {
                if (mtzTrabalho[x][y] > 4000) {
                    mtzTrabalho[x][y] = -1000;
                }
            }
        }

        //JOptionPane.showMessageDialog(null,"Conversão de valores inválidos");
        //new VisualizaImagem(indiceFatia, exame, mtzTrabalho, -400, 1500);

        //se a espessura da fatia for menor que 5mm
         if (exame.getFatia(indiceFatia).getSliceThickness() <= 5){
             mtzTrabalho = aplicaGauss(mtzTrabalho);

            //JOptionPane.showMessageDialog(null,"Aplicação de Gauss");
            //new VisualizaImagem(indiceFatia, exame, mtzTrabalho, -400, 1500);
             
         }

        //faz o scan da esquerda para a direita
        for (int y = 0; y < mtzTrabalho[0].length; y++) {
            for (int x = 0; x < mtzTrabalho.length; x++) {
                if (mtzTrabalho[x][y] > -200) {
                    break;
                }
                mtzTrabalho[x][y] = -200;
            }
        }

        //JOptionPane.showMessageDialog(null,"Scan esquerda para esquerda");
        //new VisualizaImagem(indiceFatia, exame, mtzTrabalho, -400, 1500);

        //faz o scan da direita para a esquerda
        for (int y = 0; y < mtzTrabalho[0].length; y++) {
            for (int x = (mtzTrabalho.length - 1); x > -1; x--) {
                if (mtzTrabalho[x][y] > -200) {
                    break;
                }
                mtzTrabalho[x][y] = -200;
            }
        }

        //JOptionPane.showMessageDialog(null,"Scan direita para esquerda");
        //new VisualizaImagem(indiceFatia, exame, mtzTrabalho, -400, 1500);

        //faz o scan de cima para baixo
        for (int x = 0; x < mtzTrabalho.length; x++) {
            for (int y = 0; y < mtzTrabalho[0].length; y++) {
                if (mtzTrabalho[x][y] > -200) {
                    break;
                }
                mtzTrabalho[x][y] = -200;
            }
        }

        //JOptionPane.showMessageDialog(null,"Scan de cima para baixo");
        //new VisualizaImagem(indiceFatia, exame, mtzTrabalho, -400, 1500);

        //faz o scan de baixo para cima
        for (int x = 0; x < mtzTrabalho.length; x++) {
            for (int y = (mtzTrabalho[0].length - 1); y > -1; y--) {
                if (mtzTrabalho[x][y] > -200) {
                    break;
                }
                mtzTrabalho[x][y] = -200;
            }
        }

        //JOptionPane.showMessageDialog(null,"Scan de baixo para cima");
        //new VisualizaImagem(indiceFatia, exame, mtzTrabalho, -400, 1500);

        Histogram hs = new Histogram(mtzTrabalho, -1000, -200);
        int limiar = hs.getLocalMinima(hs.getMaxOcorrencias(-1000, -201), -200);
        
        //converte a matriz para tons de cinza
        mtzTrabalho = new Threshold().aplicaBinThreshold(mtzTrabalho, limiar, 255, 0);

        //JOptionPane.showMessageDialog(null,"Binarização com limiar = " + limiar);
        //new VisualizaImagem(mtzTrabalho);

        //retira a mesa do tomógrafo, se existente
        boolean vazio = false;
        for (int y = (mtzTrabalho[0].length / 2); y < mtzTrabalho[0].length; y++) {
            for (int x = 0; x < mtzTrabalho.length; x++) {
                if (vazio) {
                    mtzTrabalho[x][y] = 0;
                    continue;
                }

                if (mtzTrabalho[x][y] != 0) {
                    break;
                }

                //se chegou ao final da linha e não saiu do laço
                if (x == (mtzTrabalho.length - 1)) {
                    vazio = true;
                }
            }
        }

        //JOptionPane.showMessageDialog(null,"Remoção da mesa do tomógrafo");
        //new VisualizaImagem(mtzTrabalho);

        //converte a matriz para binário (necessário para o labeling)
        mtzTrabalho = new Threshold().aplicaBinThreshold(mtzTrabalho, 100, 0, 1);

        BinaryLabeling lbl = new BinaryLabeling(mtzTrabalho);

        //busca os dois maiores objetos da imagem
        buscaDoisMaiores(lbl);

        //verifica se os pulmões estão conectados, sendo reconhecidos como somente 1 objeto
        if(verificaConectados(lbl.getMatrizBinLabel(labelMaior1), maior1)){
            System.out.println("Conectados! Fatia: " + (indiceFatia + 1));

            //separa os pulmões (altera mtzTrabalho, separando os pulmões
            separaPulmoes(lbl.getMatrizBinLabel(labelMaior1));

            //refaz a rotulação, agora com os pulmões separados
            lbl = new BinaryLabeling(mtzTrabalho);
            //busca os dois maiores objetos da imagem
            buscaDoisMaiores(lbl);
        }

        //lbl = preencheCavidades(lbl);

        //verifica qual dos dois objetos começa mais a esquerda
        //ATENÇÃO, a matriz retornada abaixo não está com o "offset" de objetos, por isso é necessário acrescentar 1 na comparação, mas não no get da matriz booleana!
        mtzTrabalho = lbl.getLabelledMatrix();
        for (int x = 0; x < mtzTrabalho.length; x++) {
            for (int y = 0; y < mtzTrabalho[0].length; y++) {
                if (mtzTrabalho[x][y] == (labelMaior1 + 1)) {
                    exame.getFatia(indiceFatia).setMatrizPulmaoEsq(lbl.getMatrizBinLabel(labelMaior1));
                    exame.getFatia(indiceFatia).setMatrizPulmaoDir(lbl.getMatrizBinLabel(labelMaior2));
                    x = mtzTrabalho.length;
                    break;
                }

                if (mtzTrabalho[x][y] == (labelMaior2 + 1)) {
                    exame.getFatia(indiceFatia).setMatrizPulmaoEsq(lbl.getMatrizBinLabel(labelMaior2));
                    exame.getFatia(indiceFatia).setMatrizPulmaoDir(lbl.getMatrizBinLabel(labelMaior1));
                    x = mtzTrabalho.length;
                    break;
                }
            }
        }
    }

    private int[][] aplicaGauss(int[][] mtzTrabalho) {
        double sigma = 1.76;
        int tamanho = 5;

        int[][] matriz = copyArray(mtzTrabalho);

        return GaussianLPF.aplicaGauss1D(matriz, sigma, tamanho);
    }

    private boolean verificaConectados(boolean[][] matrizBinLabel, int maior) {
        int tamEsquerda = 0;
        int tamDireita = 0;
        
        for (int y = 0; y < mtzTrabalho[0].length; y++) {
            for (int x = 0; x < (mtzTrabalho.length / 2); x++) {
                if(matrizBinLabel[x][y]){
                    tamEsquerda++;
                }
            }

            for (int x = (mtzTrabalho.length / 2); x < mtzTrabalho.length; x++) {
                if(matrizBinLabel[x][y]){
                    tamDireita++;
                }
            }

        }

        //retorna verdadeiro se cada um dos lados tem pelo menos 30% do objeto, indicando que os pulmões estão conectados
        return (tamEsquerda >= (maior * 0.3)) & (tamDireita >= (maior * 0.3));
    }

    private BinaryLabeling preencheCavidades(BinaryLabeling lbl) {
        //monta a matriz somente com os dois maiores objetos da imagem, invertendo as cores (o que é fundo vira objeto e o que é objeto vira fundo)
        int[][] mtzTemp = new int[mtzTrabalho.length][mtzTrabalho[0].length];
        boolean mtzObj1[][] = lbl.getMatrizBinLabel(labelMaior1);
        boolean mtzObj2[][] = lbl.getMatrizBinLabel(labelMaior2);

        for (int x = 1; x < mtzTemp.length - 1; x++) {
            for (int y = 1; y < mtzTemp[0].length - 1; y++) {
                //inverte os objetos pelo fundo
                if(mtzObj1[x][y]||mtzObj2[x][y]){
                    mtzTemp[x][y] = 0;    
                } else {              
                    mtzTemp[x][y] = 1;
                }
            }
        }

        lbl = new BinaryLabeling(mtzTemp);

        for (int i = 1; i <= lbl.getTotal(); i++) {
            tamanho = lbl.getTamanhoTotal(i);

            if (tamanho < 75) {
                boolean[][] mtzAux = lbl.getMatrizBinLabel(i);
                for (int x = 0; x < mtzAux.length; x++) {
                    for (int y = 0; y < mtzAux[0].length; y++) {
                        if(mtzAux[x][y]){
                            mtzTrabalho[x][y] = 1;
                        }
                    }
                }
            }
        }

        lbl = new BinaryLabeling(mtzTrabalho);

        //busca os dois maiores objetos da imagem
        buscaDoisMaiores(lbl);

        return lbl;
    }

    private void buscaDoisMaiores(BinaryLabeling lbl) {
        maior1 = 0;
        maior2 = 0;
        labelMaior1 = 0;
        labelMaior2 = 0;

        for (int i = 1; i <= lbl.getTotal(); i++) {
            tamanho = lbl.getTamanhoTotal(i);

            if (tamanho > maior1) {
                maior2 = maior1;
                labelMaior2 = labelMaior1;
                maior1 = tamanho;
                labelMaior1 = i;
            } else {
                if (tamanho > maior2) {
                    maior2 = tamanho;
                    labelMaior2 = i;
                }
            }
        }
    }

    private void separaPulmoes(final boolean[][] matrizBinLabel) {

        int tamProcDir = 0;
        int tamProcEsq = 0;
        int menorColuna = 0;
        int menorTamanho = mtzTrabalho.length;
        int metadeMatriz = mtzTrabalho.length / 2;

        // vai testar 40 colunas a partir do meio da imagem para a direita e 40 para a esquerda
        for (int x = 0; x < 40; x++) {
            // varre até a metade das linhas
            for (int y = 0; y < metadeMatriz; y++) {
                // esquerda
                if (matrizBinLabel[metadeMatriz - x][y]) {
                    tamProcEsq++;
                }

                // direita
                if (matrizBinLabel[metadeMatriz + x][y]) {
                    tamProcDir++;
                }
            }

            // se encontrou coluna menor do lado direito
            if (tamProcDir != 0 && tamProcDir < menorTamanho) {
                menorTamanho = tamProcDir;
                menorColuna = metadeMatriz + x;
            }

            // se encontoru coluna menor do lado esquerdo
            if (tamProcEsq != 0 && tamProcEsq < menorTamanho) {
                menorTamanho = tamProcEsq;
                menorColuna = metadeMatriz - x;
            }

            // inicializa contadores
            tamProcDir = 0;
            tamProcEsq = 0;
        }

        // se encontrou a menor colua
        if (menorColuna != 0) {
            // varre todas as linhas, inicializando a coluna como fundo
            for (int y = 0; y < metadeMatriz; y++) {
                mtzTrabalho[menorColuna][y] = 0;
            }
        }
    }

}
