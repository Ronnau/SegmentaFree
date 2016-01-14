package testes;

import dados.Exame;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 *
 * @author Rodrigo
 */
public class VisualizaImagem {

    public VisualizaImagem(int[][] matrizImagem) {
        BufferedImage imagem = new BufferedImage(matrizImagem.length, matrizImagem[0].length, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < imagem.getWidth(); x++) {
            for (int y = 0; y < imagem.getHeight(); y++) {
                int px =(int)matrizImagem[x][y]<<16 | (int)matrizImagem[x][y] << 8 | (int)matrizImagem[x][y];
                imagem.setRGB(x, y, px);
            }
        }
        exibeImagem(imagem);
    }

    public VisualizaImagem(int indice, Exame exame, int[][] matrizImagem, int WL, int WW) {
        exibeImagem(exame.geraImagemMtz(indice, matrizImagem, WL, WW));
    }

    
    private void exibeImagem(BufferedImage imagem) {
        JDialog janela = new JDialog();
        JLabel label = new JLabel(new ImageIcon(imagem));
        label.setBounds(10, 10, imagem.getWidth(), imagem.getHeight());
        janela.setSize(imagem.getWidth() + 35 , imagem.getHeight() + 60);
        janela.setLocationRelativeTo(null);
        janela.getContentPane().setLayout(null);
        janela.getContentPane().add(label);
        janela.setModal(true);
        janela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        janela.setVisible(true);
    }
    
}
