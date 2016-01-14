package utils;

/**
 *
 * @author rodri
 */
public class Utils {
    
        public static int[][] copyArray(final int[][] array) {
        final int[][] copy = new int[array.length][];

        for (int i = 0; i < array.length; i++) {
            copy[i] = new int[array[i].length];
            System.arraycopy(array[i], 0, copy[i], 0, array[i].length);
        }

        return copy;
    }

    
}
