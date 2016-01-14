package dados;

/**
 *
 * @author Rodrigo
 */
public class FatiaExame {

    private int[][] matrizCoeficientes;
    private boolean[][] matrizPulmaoEsq;
    int tamanhoPulmaoEsq = 0;
    private boolean[][] matrizPulmaoDir;
    int tamanhoPulmaoDir = 0;
    private float sliceThickness;
    private int rows = 512;
    private int columns = 512;
    private int rescaleSlope = 1;
    private int rescaleIntercept = -1024;
    private boolean isPadded = false;
    private int padValue = 65000;

    public void setMatrizCoeficientes(int[][] matrizCoeficientes) {
        this.matrizCoeficientes = matrizCoeficientes;
    }

    public int[][] getMatrizCoeficientes() {
        return matrizCoeficientes;
    }

    public void setMatrizPulmaoEsq(boolean[][] matrizPulmaoEsq) {
        this.matrizPulmaoEsq = matrizPulmaoEsq;
        
        tamanhoPulmaoEsq = 0;
        for (int i = 0; i < matrizPulmaoEsq.length; i++) {
            for (int j = 0; j < matrizPulmaoEsq[i].length; j++) {
                if (matrizPulmaoEsq[i][j]){
                    tamanhoPulmaoEsq++;
                }
            }
        }
        
    }

    public boolean[][] getPulmaoEsq() {
        return matrizPulmaoEsq;
    }

    public int getTamanhoPulmaoEsq() {
        return tamanhoPulmaoEsq;
    }
    
    public void setMatrizPulmaoDir(boolean[][] matrizPulmaoDir) {
        this.matrizPulmaoDir = matrizPulmaoDir;
        
        tamanhoPulmaoDir = 0;
        for (int i = 0; i < matrizPulmaoDir.length; i++) {
            for (int j = 0; j < matrizPulmaoDir[i].length; j++) {
                if (matrizPulmaoDir[i][j]){
                    tamanhoPulmaoDir++;
                }
            }
        }
        
    }

    public boolean[][] getPulmaoDir() {
        return matrizPulmaoDir;
    }
    
    public int getTamanhoPulmaoDir() {
        return tamanhoPulmaoDir;
    }
    
    public void setSliceThickness(float sliceThickness) {
        this.sliceThickness = sliceThickness;
    }

    public float getSliceThickness() {
        return sliceThickness;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getRows() {
        return rows;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getColumns() {
        return columns;
    }

    public void setRescaleSlope(int rescaleSlope) {
        this.rescaleSlope = rescaleSlope;
    }

    public int getRescaleSlope() {
        return rescaleSlope;
    }

    public void setRescaleIntercept(int rescaleIntercept) {
        this.rescaleIntercept = rescaleIntercept;
    }

    public int getRescaleIntercept() {
        return rescaleIntercept;
    }

    public void setIsPadded(boolean isPadded) {
        this.isPadded = isPadded;
    }

    public boolean isPadded() {
        return isPadded;
    }

    public void setPadValue(int padValue) {
        this.padValue = padValue;
    }    
    
    public int getPadValue() {
        return padValue;
    }

}
