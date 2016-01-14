package dados;

import br.feevale.jeffcfbr.dicom.DICOMFile;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import processamento.SegmentaEstruturas;

/**
 *
 * @author Rodrigo
 */
public class Exame {

    public static final int DIRETORIO = 1;
    public static final int ARQUIVO = 2;

    private FatiaExame[] fatias;
    // Nunca manipular essa variável diretamente fora dos métodos de carga!!!
    private DICOMFile[] arquivosDICOM;
    private final boolean leuArquivo;
    private int numeroFatias;
    private String[] arquivos;

    public Exame(int tipo, String localizacao) throws Exception {

        if (tipo == DIRETORIO) {
            leuArquivo = false;
            carregaDir(localizacao);
        } else {
            leuArquivo = true;
            carregaArq(localizacao);
        }
        
        SegmentaEstruturas segEst = new SegmentaEstruturas(this);
        segEst.segmenta();
    }

    private void carregaDir(String localizacao) {
        arquivos = new File(localizacao).list((File dir, String name) -> name.toLowerCase().endsWith(".dcm"));

        Arrays.sort(arquivos);
        numeroFatias = arquivos.length;
        arquivosDICOM = new DICOMFile[numeroFatias];
        fatias = new FatiaExame[numeroFatias];
        for (int i = 0; i < numeroFatias; i++) {
            arquivosDICOM[i] = new DICOMFile(localizacao + "\\" + arquivos[i]);            
            carregaDadosFatia(i, i, 0);
        }
    }

    private void carregaArq(String localizacao) {
        
        Path p = Paths.get(localizacao);
        arquivos = new String[] { p.getFileName().toString() };
        arquivosDICOM = new DICOMFile[] { new DICOMFile(localizacao) };

        //a declaração do array varia de acordo com o número de fatias que tem o exame. Substituir o valor usado abaixo pelo numero de fatias
        numeroFatias = 1;

        fatias = new FatiaExame[numeroFatias];
        for (int i = 0; i < numeroFatias; i++) {
            carregaDadosFatia(i, 0, i);
        }
    }

    private void carregaDadosFatia(int indice, int arquivo, int fatia) {
        fatias[indice] = new FatiaExame();
        try {
            fatias[indice].setColumns(Integer.parseInt(arquivosDICOM[arquivo].getAttribute(new AttributeTag("(0x0028,0x0011)"))[0]));
        } catch (DicomException ex) {
            Logger.getLogger(Exame.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {        
            fatias[indice].setRows(Integer.parseInt(arquivosDICOM[arquivo].getAttribute(new AttributeTag("(0x0028,0x0010)"))[0]));
        } catch (DicomException ex) {
            Logger.getLogger(Exame.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            fatias[indice].setSliceThickness(Float.parseFloat(arquivosDICOM[arquivo].getAttribute(new AttributeTag("(0x0018,0x0050)"))[0]));
        } catch (DicomException ex) {
            Logger.getLogger(Exame.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            fatias[indice].setRescaleIntercept(Integer.parseInt(arquivosDICOM[arquivo].getAttribute(new AttributeTag("(0x0028,0x1052)"))[0]));
        } catch (DicomException ex) {
            Logger.getLogger(Exame.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            fatias[indice].setRescaleSlope(Integer.parseInt(arquivosDICOM[arquivo].getAttribute(new AttributeTag("(0x0028,0x1053)"))[0]));
        } catch (DicomException ex) {
            Logger.getLogger(Exame.class.getName()).log(Level.SEVERE, null, ex);
        }
        fatias[indice].setIsPadded(arquivosDICOM[arquivo].isPadded());
        fatias[indice].setPadValue(arquivosDICOM[arquivo].getPadValue());
        fatias[indice].setMatrizCoeficientes(converteHU(arquivosDICOM[arquivo].getOriginalImage(fatia).getRaster(), fatias[indice].getRescaleIntercept(), fatias[indice].getRescaleSlope(),fatias[indice].isPadded(),fatias[indice].getPadValue()));          

        //System.out.println("Linhas: " + fatias[indice].getRows() + "\nColunas: " + fatias[indice].getColumns()+ "\nSlope: " + fatias[indice].getRescaleSlope() + "\nIntercept: " + fatias[indice].getRescaleIntercept() + "\nThickness: " + fatias[indice].getSliceThickness());
        
        //AttributeList al = arquivosDICOM[arquivo].getAttributesList();
        //System.out.println(al.toString());

    }

    private int[][] converteHU(WritableRaster raster, int rescaleIntercept, int rescaleSlope, boolean isPadded, int padValue) {
        
        int[][] matrizHU = new int[raster.getWidth()][raster.getHeight()];
        for (int x = 0; x < matrizHU.length; x++) {
            for (int y = 0; y < matrizHU[x].length; y++) {
                if (isPadded && raster.getPixel(x, y, new int[1])[0] == padValue ){
                    matrizHU[x][y] = padValue;
                } else {
                    matrizHU[x][y] = raster.getPixel(x, y, new int[1])[0] * rescaleSlope + rescaleIntercept;
                }
            }
        }
        return matrizHU;
    }

    public BufferedImage getImagemFatia(int indice, int WL, int WW ) {
        return getDICOMFile(ajustaIndiceArquivo(indice)).applyWLWW(ajustaIndiceFatia(indice), WL, WW);
    }

    public FatiaExame getFatia(int fatia) {
        return fatias[fatia];
    }

    public String getNomeArquivo(int fatia) {
        if (leuArquivo) {
            return arquivos[0];
        }

        return arquivos[fatia];
    }

    public int getNumeroFatias() {
        return numeroFatias;
    }

    private DICOMFile getDICOMFile(int indice){
        return arquivosDICOM[ajustaIndiceArquivo(indice)];
    }

    private int ajustaIndiceArquivo(int indice){
       return leuArquivo ? 0 : indice;
    }

    private int ajustaIndiceFatia(int indice){
       return leuArquivo ? indice : 0;
    }

    //TESTE!!!!
    public BufferedImage geraImagemMtz(int indice, int[][] matrizImagem, int WL, int WW){
        DICOMFile dicom = getDICOMFile(ajustaIndiceArquivo(indice));
        return dicom.applyWLWW(matrizImagem, dicom.isSigned(), dicom.isInverted(), dicom.getRescaleSlope(), dicom.getRescaleIntercept(), dicom.isPadded(), dicom.getPadValue(), WL, WW);
    }

}
