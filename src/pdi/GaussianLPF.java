package pdi;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author RodrigoPC
 * Geração de kernel 1D e 2D baseado em: http://dev.theomader.com/gaussian-kernel-calculator/
 */
public class GaussianLPF {

    public static int[][] aplicaGauss1D(int[][] mtzTrabalho, double sigma, int tamanho) {
        return Convolution.aplicaConvolucao1D(mtzTrabalho, geraKernel1D(sigma, tamanho));
    }

    public static int[][] aplicaGauss2D(int[][] mtzTrabalho, double sigma, int tamanho) {
        return Convolution.aplicaConvolucao2D(mtzTrabalho, geraKernel2D(sigma, tamanho));
    }
    
    private static double[] geraKernel1D(double sigma, int tamanho) {
        return geraKernel(sigma, tamanho, 100);
    }
    
    private static double[][] geraKernel2D(double sigma, int tamanho) {
        
        double[] temp = geraKernel(sigma, tamanho, 100);
        double[][] kernel = new double[tamanho][tamanho];
        int x = 0;
        int y = 0;
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                kernel[x][y] = temp[i] * temp[j];
                y++;
            }
            y = 0;
            x++;
        }        

        System.out.println();
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                System.out.print(kernel[i][j] + " ");                
            }
            System.out.println();
        }
        
        return kernel;
    }


    
    
    public static double[] geraKernel(double sigma, int kernelSize, double sampleCount) {
        double samplesPerBin = Math.ceil(sampleCount / kernelSize);
        // need an even number of intervals for simpson integration => odd number of samples
        if (samplesPerBin % 2 == 0) {
            ++samplesPerBin;
        }

        double weightSum = 0;
        double kernelLeft = -Math.floor(kernelSize / 2);

        List<Double> allSamples = new ArrayList<>();

        // now sample kernel taps and calculate tap weights
        for (int tap = 0; tap < kernelSize; ++tap) {
            double left = kernelLeft - 0.5 + tap;

            List<Double[]> tapSamples = calcSamplesForRange(left, left + 1, samplesPerBin, sigma);
            double tapWeight = integrateSimphson(tapSamples);
            allSamples.add(tapWeight);
            weightSum += tapWeight;
        }

        double[] sampleArray = new double[allSamples.size()];
        // renormalize kernel and round to 6 decimals
        for (int i = 0; i < allSamples.size(); ++i) {
            sampleArray[i] = allSamples.get(i) / weightSum;
        }
        return sampleArray;
    }

    private static List<Double[]> calcSamplesForRange(double minInclusive, double maxInclusive, double samplesPerBin, double sigma) {
        return sampleInterval(minInclusive,
                maxInclusive,
                samplesPerBin, sigma);
    }

    private static List<Double[]> sampleInterval(double minInclusive, double maxInclusive, double sampleCount, double sigma) {
        List<Double[]> result = new ArrayList<>();
        double stepSize = (maxInclusive - minInclusive) / (sampleCount - 1);

        for (int s = 0; s < sampleCount; ++s) {
            double x = minInclusive + s * stepSize;
            double y = gaussianDistribution(x, sigma);

            result.add(new Double[]{x, y});
        }

        return result;
    }

    private static double integrateSimphson(List<Double[]> samples) {
        double result = samples.get(0)[1] + samples.get(samples.size() - 1)[1];

        for (int s = 1; s < samples.size() - 1; ++s) {
            double sampleWeight = (s % 2 == 0) ? 2.0 : 4.0;
            result += sampleWeight * samples.get(s)[1];
        }

        double h = (samples.get(samples.size() - 1)[0] - samples.get(0)[0]) / (samples.size() - 1);
        return result * h / 3.0;
    }

    private static double gaussianDistribution(double x, double sigma) {
        double n = 1.0 / (Math.sqrt(2 * Math.PI) * sigma);
        return Math.exp(-x * x / (2 * sigma * sigma)) * n;
    }

}
