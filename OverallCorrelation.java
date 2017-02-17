/**
 * Collection of methods for finding the overall correlation of a matrix of 
 * pairwise correlation.
 *
 * @author Derek S. Prijatelj
 */

public class OverallCorrelation{

    /**
     * Searches Correlation Matrix for highest correlation value not on
     * diagonal. Pessimistic overall correlatoin of the matrix.
     *
     * @param corMat n by n matrix containing Correlation values, mirrored
     * across diagonal.
     * @return the highest correlation value in Matrix.
     */
    public static double maxCor(double[][] corMat){
        double max = -1;

        for (int i = 0; i < corMat.length; i++) {
            for (int j = 0; j < corMat[i].length; j++) {
                if (i == j)
                    continue; // Ignore diagonal from finding Max
                if (max < corMat[i][j]){
                    max = corMat[i][j];
                }
            }
        }
        return max;
    }

    /**
     * Finds the average correlation of all correlation values, ignoring the
     * diagonal. This absolute values all contents in matrix, this found 
     * "correlation" represents how far from uncorrelated the contents are.
     *
     * @param corMat 2d double matrix of correlation between pairs. mirrs across
     * diagonal
     * @return average correlation*
     */
    public static double avgCor(double[][] corMat){
        double sum = 0;
        
        for (int i = 0; i < corMat.length; i++){
            for (int j = 0; j < corMat[i].length; j++){
                if (i == j)
                    continue;
                sum += Math.abs(corMat[i][j]);
            }
        }
        
        double n = corMat.length;
        
        return sum / (n * n - n); // due to mirrored data, same avg as 1/2 data
    }
    
    public static void main(String[] args){
        double[][] maxArr = {
            {1,     -.5,   .99},
            {-.5,   1,     .42},
            {.99,    .42,    1}
        };

        System.out.println(maxCor(maxArr));
        System.out.println(avgCor(maxArr));
    }
}
