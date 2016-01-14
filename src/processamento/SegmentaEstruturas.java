package processamento;

import dados.Exame;
import testes.VisualizaImagem;

/**
 *
 * @author Rodrigo
 */
public class SegmentaEstruturas {
    private final Exame exame;

    public SegmentaEstruturas(Exame exame) {
        this.exame = exame;
    }

    public void segmenta() {        
        SegmentaPulmoes segPul = new SegmentaPulmoes(exame);
        segPul.segmenta();
        
        //executa todos os processos de segmentação
        
      //  new VisualizaImagem(exame.getFatia(30).getMatrizPulmoes());
        
    }
    
}
