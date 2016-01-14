package janela;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Rodrigo
 */
public class Controller {

    public static final int DIRETORIO = 1;
    public static final int ARQUIVO = 2;

    private File ultimoDiretorio = null;
    private File ultimoArquivo = null;

    private JFileChooser selecaoDirArq = null;

    private final FileFilter filtroArquivosDcm = new FileNameExtensionFilter("Arquivo DICOM", "dcm");
    private final FileFilter filtroArquivosBmp = new FileNameExtensionFilter("Arquivo BMP", "bmp");

    private View janela;
    Model dados;

    public void iniciaAplicacao() {
        prepararSeletorDirArq();

        janela = new View(this);
        janela.exibe();
    }

    public boolean temExameCarregado() {
        return dados != null;
    }

    void selecionarDirArq(int tipo) {
        if (ultimoDiretorio != null) {
            selecaoDirArq.setCurrentDirectory(ultimoDiretorio);
        }

        int tipoModel;

        if (tipo == DIRETORIO) {
            tipoModel = Model.DIRETORIO;

            selecaoDirArq.setDialogTitle("Selecione o diretório onde estão os arquivos DICOM");
            selecaoDirArq.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else {
            tipoModel = Model.ARQUIVO;

            selecaoDirArq.setDialogTitle("Selecione o arquivo DICOM");
            selecaoDirArq.setFileFilter(filtroArquivosDcm);
            selecaoDirArq.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }

        if (selecaoDirArq.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            final File itemSelecionado = selecaoDirArq.getSelectedFile();

            try {
                dados = new Model(tipoModel, itemSelecionado.getAbsolutePath());
                janela.limitaSlider(dados.getNumeroFatias());
                janela.atualizaImagem();

                if (tipo == DIRETORIO) {
                    ultimoDiretorio = itemSelecionado;
                    ultimoArquivo = null;
                } else {
                    ultimoDiretorio = new File(itemSelecionado.getParent());
                    ultimoArquivo = itemSelecionado;
                }
            } catch (Exception ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    void salvarFatia() {
        selecaoDirArq.setDialogTitle("Selecione o diretório onde será gravada a imagem da fatia");
        selecaoDirArq.setFileSelectionMode(JFileChooser.FILES_ONLY);
        selecaoDirArq.setCurrentDirectory(ultimoDiretorio);
        selecaoDirArq.setFileFilter(filtroArquivosBmp);
        selecaoDirArq.setSelectedFile(new File(getNomeArquivoBmp(janela.getValorSlider())));

        if (selecaoDirArq.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            final BufferedImage imagem = geraImagem();

            try {
                ImageIO.write(imagem, "bmp", selecaoDirArq.getSelectedFile());
                JOptionPane.showMessageDialog(null, "Arquivo '" + selecaoDirArq.getSelectedFile().getName() + "' salvo com sucesso.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Ocorreu um erro ao salvar a fatia.");
            }
        }
    }
    
    void salvarExame() {
        selecaoDirArq.setDialogTitle("Selecione o diretório onde serão gravadas as imagens do exame");
        selecaoDirArq.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (selecaoDirArq.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                for (int fatia = 0; fatia < dados.getNumeroFatias(); fatia++) {
                    final File fatiaExame = new File(selecaoDirArq.getSelectedFile(), getNomeArquivoBmp(fatia));
                    ImageIO.write(geraImagem(fatia), "bmp", fatiaExame);
                }

                JOptionPane.showMessageDialog(null, "Exame salvo com sucesso.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Ocorreu um erro ao salvar o exame.");
            }
        }
    }

    BufferedImage geraImagem() {
        return geraImagem(janela.getValorSlider());
    }

    BufferedImage geraImagem(final int fatia) {
        BufferedImage imagemOriginal = dados.getImagemFatia(fatia, janela.getWL(), janela.getWW());
        BufferedImage imagem = new BufferedImage(imagemOriginal.getWidth(), imagemOriginal.getHeight(), BufferedImage.TYPE_INT_RGB);

        // Faz uma cópia da imagem original pixel a pixel pelo RGB
        for (int ix = 0; ix < imagem.getWidth(); ix++) {
            for (int iy = 0; iy < imagem.getHeight(); iy++) {
                int rgbOriginal = imagemOriginal.getRGB(ix, iy);
                imagem.setRGB(ix, iy, rgbOriginal);
            }
        }

        if (janela.isSegPulmaoEsqMarcado()) {
            pintaImagem(imagem, dados.getMatrizPulmaoEsq(fatia), Color.yellow);
        }
        if (janela.isSegPulmaoDirMarcado()) {
            pintaImagem(imagem, dados.getMatrizPulmaoDir(fatia), Color.green);
        }

        return imagem;
    }
    
    private void pintaImagem(BufferedImage imagem, boolean[][] matrizOrgao, Color cor) {
        for (int ix = 0; ix < matrizOrgao.length; ix++) {
            for (int iy = 0; iy < matrizOrgao[0].length; iy++) {
                if(matrizOrgao[ix][iy]) {
                    imagem.setRGB(ix, iy, cor.getRGB());
                }
            }
        }
    }

    private void prepararSeletorDirArq() {
        selecaoDirArq = new JFileChooser();
        selecaoDirArq.setAcceptAllFileFilterUsed(false);
    }

    private String getNomeArquivoBmp(final int fatia) {
        final String nomeArquivo = dados.getNomeArquivo(fatia);
        return nomeArquivo.substring(0, nomeArquivo.lastIndexOf(".")) + "_" + (fatia + 1) + ".bmp";
    }

}
