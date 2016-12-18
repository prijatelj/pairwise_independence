/**
 * Given a MultiLog object, uses two-porportion z-test to determine the 
 * independence between all pairs of methods depicted in the logs on same
 * test set. This requires same test, and same number of Tests for each 
 * method.
 *
 * @author Derek S. Prijatelj
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.lang.Math;

class PairwiseIndependence{
    /*
     * TODO 
     *
     * Double [L][L] (double max) down the diagonal. Every row & col
     * contains a single double for the rate of pairwise independence
     * between the respective methods.
     *
     * Needs to appropriately handle all methods that Fail all tests to give
     * all compared to values the maximum double value, because 
     * non-computeable and gives us no information.
     */

    /**
     * Finds the pairwise independence matrix between all logs provided in
     * the given MultiLog.

     * @param   ml  MultiLog with all logs of exact same test set.
     * @return  2d Double matrix where each row, col pair contains the 
     *          respective independence value.
     */
    public static double[][] process(MultiLog ml){
        
        /*
         * pwIndependence   2d Matrix of the Z scores results
         * incorrect        stores how many incorrect for that log
         * correct          stores how many correct for that log
         * checked          indicates if the Log has been checked by correct
         *                      and incorrect.
         */
        double[][] pwIndependence = 
            new double[ml.logs.size()][ml.logs.size()];
        int[] incorrect = new int[ml.logs.size()];
        int[] correct = new int[ml.logs.size()];
        boolean[] checked = new boolean[ml.logs.size()];

        for (int i = 0; i < ml.logs.size(); i++){ // L
            for (int j = 0; j < ml.logs.size(); j++){ // L-1
                if (j == i){
                    pwIndependence[i][j] = Double.MAX_VALUE;
                    continue;
                }
                int iGivenj = 0, iGivenNotj = 0;
                if(!checked[i] && !checked[j]){
                    // Assumes Test size for i and j are equal
                    for (int t = 0; t < ml.logs.get(i).tests.size(); t++){
                        if (isCorrect(ml, i, t)){
                            correct[i]++;
                        } else {
                            incorrect[i]++;
                        }
                        if (isCorrect(ml, j, t)){
                            correct[j]++;
                        } else {
                            incorrect[j]++;
                        }
                        checked[i] = true;
                        checked[j] = true;
                        if (isICorrectGivenJ(ml, i, j, t)){
                            iGivenj++;
                        } else {
                            iGivenNotj++;
                        }
                    }
                }
                else if (!checked[i]){
                    for (int t = 0; t < ml.logs.get(i).tests.size(); t++){
                        if (isCorrect(ml, i, t)){
                            correct[i]++;
                        } else {
                            incorrect[i]++;
                        }
                        checked[i] = true;
                        if (isICorrectGivenJ(ml, i, j, t)) {
                            iGivenj++;
                        } else {
                            iGivenNotj++;
                        }
                    }
                }
                else if (!checked[j]){
                    for (int t = 0; t < ml.logs.get(i).tests.size(); t++){
                        if (isCorrect(ml, j, t)){
                            correct[j]++;
                        } else {
                            incorrect[j]++;
                        }
                        checked[j] = true;
                        if (isICorrectGivenJ(ml, i, j, t)){
                            iGivenj++;
                        } else {
                            iGivenNotj++;
                        }
                    }
                }
                else{
                    for (int t = 0; t < ml.logs.get(j).tests.size(); t++){
                        if (isICorrectGivenJ(ml, i, j, t)){
                            iGivenj++;
                        } else {
                            iGivenNotj++;
                        }
                    }
                }
                pwIndependence[i][j] = twoPorportionZTest(
                    correct[i], incorrect[i], correct[j], incorrect[j],
                    iGivenj, iGivenNotj, ml.logs.get(i).tests.size());
            }
        }
        return pwIndependence; // TODO Currently returns Z value. Not Prob.
    }

    /**
     * Checks if the first placed doc if it matches correct author
     * TODO This is only intended for the JGAAP 
     */
    private static boolean isCorrect(MultiLog ml, int i, int t){
        String s1[] = ml.logs.get(i).tests.get(t).questionedDoc.split(" ");
        String s2 = ml.logs.get(i).tests.get(t).results.get(0).author;
        //System.out.println(s1[1] + " ? "+ s2 + " : " + s1[1].equals(s2));
        return s1[1].equals(s2);
    }

    private static boolean isICorrectGivenJ(MultiLog ml, int i, int j,
            int t){
        return isCorrect(ml, i, t) == isCorrect(ml, j, t);
    }

    /**
     * Finds the Z score value of Independence between i and j methods.
     *
     * @param   i           Number of times i correct
     * @param   iNot        Number of times i incorrect
     * @param   j           Number of times j correct
     * @param   jNot        Number of times j incorrect
     * @param   iGivenj     Number of times i correct when j is correct
     * @param   iGivenNotj  Number of times i correct when j is incorrect
     * @return  double value of the Z score of independence between the logs
     */
    private static double twoPorportionZTest(double i, double iNot, 
            double j, double jNot, double iGivenj, double iGivenNotj,
            double T){
        if (i == 0 || j == 0){
            return Double.MAX_VALUE;
        }
        System.out.println("i:" + i + " iNot:"+ iNot +
            " j:" + j + " jNot:" + jNot + " iGivenj:" + iGivenj +
            " iGivenNotj:" + iGivenNotj + " T:" +T);
        double numerator = (iGivenj / j) - (iGivenNotj / jNot);
        double denominator = Math.sqrt((i/T) * (iNot/T) * ((1/j)
            + (1/jNot)));
        System.out.println(numerator + " " + denominator);
        return numerator/denominator;
    }

    public static void main(String[] args){
        //*
        double[][] m = process (new MultiLog("sml", "yep", true));
    
        for (int i = 0; i < m.length; i++){
            for (int j = 0; j < m[i].length; j++){
                System.out.printf("%e ", m[i][j]);
            }
            System.out.println();
        }
        //*/
        /*/
        MultiLog m = new MultiLog("suffer");
        m.print();
        System.out.println("Size = " + m.logs.size());
        //*
        /*
        try{
            //LogData l = new LogData("Greek Analysis (topic 1 -> topic 2Char 4-grams2016-12-15.txt");
            //LogData l = new LogData("suffer", true);
            l.print();
        }
        catch (InvalidLogFileType | InvalidLogStructure | NotADirectory
                e) {
            // TODO figure out if its best to use error handlers or return
            // boolean value from LogData stating if everything is alright 
            // or not.
            System.out.println("TODO: handle custom errors appropriately!");
        }
        //*/
    }
}
