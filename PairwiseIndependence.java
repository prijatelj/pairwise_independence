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
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

class PairwiseIndependence{
    /* TODO
     * safely handles the partitioning of the process task to avoid
     * OutOfMemoryError causing crash.
     */
    public static void safeProcess(){
        int numPartitions = 1;
        boolean run = false;
        while (run && numPartitions <= 2000){
            try{
                for (int part = 0; part < numPartitions; part++){
                    
                }
            } catch(OutOfMemoryError e){
               numPartitions *= 2;
            }
        }
    }

    public static ArrayList <String> defectFiles = new ArrayList <> ();

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

     * @param   ml      MultiLog with all logs of exact same test set.
     * @param   export  if true: exports matrix to .csv file; false: no export.
     * @return  2d Double matrix where each row, col pair contains the 
     *          respective independence value.
     */
    public static double[][] process(MultiLog ml, boolean export){
        
        /*
         * pwIndependence   2d Matrix of the Z scores results
         * incorrect        stores how many incorrect for that log
         * correct          stores how many correct for that log
         * checked          indicates if the Log has been checked by correct
         *                      and incorrect.
         * ignore           each log has a boolean value of to ignore or
         *                  not in parallel with it. Ignore if contains NaN
         *                  results
         */
        double[][] pwIndependence = 
            new double[ml.logs.size()][ml.logs.size()];
        int[] incorrect = new int[ml.logs.size()];
        int[] correct = new int[ml.logs.size()];
        boolean[] checked = new boolean[ml.logs.size()];
        boolean[] ignore = new boolean[ml.logs.size()];
        int iGivenj = 0, iGivenNotj = 0;

        ArrayList <String> logDataNames = new ArrayList <> ();
        
        for (int i = 0; i < ml.logs.size(); i++){
            logDataNames.add(ml.logs.get(i).name);
        }

        for (int i = 0; i < ml.logs.size(); i++){ // L
            
            //logDataNames.add(ml.logs.get(i).name);

            for (int j = 0; j < ml.logs.size(); j++){ // L-1
                if (j == i){
                    pwIndependence[i][j] = Double.MAX_VALUE;
                    continue;
                }
                
                iGivenj = 0; iGivenNotj = 0;
                
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
                        } else if (isICorrectGivenNotJ(ml, i, j, t)){
                            iGivenNotj++;
                        }
                    }
                } else if (!checked[i]){
                    for (int t = 0; t < ml.logs.get(i).tests.size(); t++){
                        if (isCorrect(ml, i, t)){
                            correct[i]++;
                        } else {
                            incorrect[i]++;
                        }
                        checked[i] = true;
                        if (isICorrectGivenJ(ml, i, j, t)) {
                            iGivenj++;
                        } else if (isICorrectGivenNotJ(ml, i, j, t)){
                            iGivenNotj++;
                        }
                    }
                } else if (!checked[j]){
                    for (int t = 0; t < ml.logs.get(i).tests.size(); t++){
                        if (isCorrect(ml, j, t)){
                            correct[j]++;
                        } else {
                            incorrect[j]++;
                        }
                        checked[j] = true;
                        if (isICorrectGivenJ(ml, i, j, t)){
                            iGivenj++;
                        } else if (isICorrectGivenNotJ(ml, i, j, t)){
                            iGivenNotj++;
                        }
                    }
                } else{
                    for (int t = 0; t < ml.logs.get(i).tests.size(); t++){
                        if (isICorrectGivenJ(ml, i, j, t)){
                            iGivenj++;
                        } else if (isICorrectGivenNotJ(ml, i, j, t)){
                            iGivenNotj++;
                        }
                    }
                }
                pwIndependence[i][j] = twoPorportionZTest(
                    correct[i], incorrect[i], correct[j], incorrect[j],
                    iGivenj, iGivenNotj, ml.logs.get(i).tests.size());
            }

            /*
             * Secure Data by once completed, export CSV
             * Export every log after it has been compared to all other logs
             */
            if (export) {
                singletonExportCSV(pwIndependence[i], logDataNames,
                    ml.name, ml.logs.get(i).name);
            }
        }
        
        if (export){
            exportCSV(pwIndependence, logDataNames, ml.name);
        }

        return pwIndependence; // TODO Currently returns Z value. Not Prob.
    }

    /**
     * Checks if the first placed doc if it matches correct author.
     * @return Returns true iff top most document author matches correct author
     *          and there is no tie for first place. Otherwise, false.
     */
    private static boolean isCorrect(MultiLog ml, int i, int t){
        //System.out.println("file: "+ ml.logs.get(i).name);
        String s1[] = ml.logs.get(i).tests.get(t).questionedDoc.split(" ");
        String s2 = ml.logs.get(i).tests.get(t).results.get(0).author;
        if (s1.length <= 1){
            if (!defectFiles.contains(ml.logs.get(i).name))
                defectFiles.add(ml.logs.get(i).name);
            return false;
        }
        //System.out.println(s1[1] + " ? "+ s2 + " : " + s1[1].equals(s2));
        if (s2.contains(" ")){ // Author must always be first
            s2 = (s2.split(" "))[0];
        }
        return s1[1].equals(s2) && 
            ml.logs.get(i).tests.get(t).results.get(0).rank != 
            ml.logs.get(i).tests.get(t).results.get(1).rank;
    }

    private static boolean isICorrectGivenJ(MultiLog ml, int i, int j,
            int t){
        return isCorrect(ml, i, t) && isCorrect(ml, j, t);
    }
    private static boolean isICorrectGivenNotJ(MultiLog ml, int i, int j,
            int t){
        return isCorrect(ml, i, t) && !isCorrect(ml, j, t);
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
        /* View Parameters Given
        System.out.println("\ni:" + i + " iNot:"+ iNot +
            " j:" + j + " jNot:" + jNot + " iGivenj:" + iGivenj +
            " iGivenNotj:" + iGivenNotj + " T:" +T);
        //*/
        /*
        if (i == 0 || j == 0 || iNot == 0 || jNot == 0){
            return Double.MAX_VALUE; // Unable to Compute.
        }
        //*/

        /*
         * Currently fudges the numbers by adding 1 to both i, iNot, j,
         * jNot, iGivenj, and iGivenNotj. Also adds 2 to T. This will only
         * distort experiments with low number of tests, large test numbers
         * should be generally unaffected.
         *
         * TODO Figure out how to properly handle 100% success and failure
         * in the results of a file. Discarding 100% failues makes sense,
         * due to that method being obviously terrible and useless, but if
         * we were to find a perfect method, then should it too be discarded
         * from independence testing? (This is a divide by Zero problem)
         */
        //*
        i++; iNot++; j++; jNot++;
        iGivenj++; iGivenNotj++;
        T += 2;
        //*/
        double numerator = (iGivenj / j) - (iGivenNotj / jNot);
        double denominator = Math.sqrt( ((i*iNot)/T) * ((1/j) + (1/jNot)));
        //System.out.println(numerator + " " + denominator);
        
        return numerator/denominator;
    }
    
    /**
     * Exports the provided 2d matrix of methods' z scores to .csv file.
     *
     * @param   mat     2d matrix of type double: represents z scores
     * @param   methods ArrayList of the names of the methods in order to
     *                  match the content in mat.
     * @param   name    name of the multilog mat was derived from.
     */
    public static void exportCSV(double[][] mat,
            ArrayList <String> methods, String name){

        if (mat.length == methods.size() &&
                mat[0].length == methods.size()){

            try{
                File csvFile = new File(name + ".csv");
                int ver;
                while (csvFile.exists()){
                    if(csvFile.getName().contains("_pwi_")){
                        ver = Integer.parseInt(
                            csvFile.getName().substring(
                                csvFile.getName().lastIndexOf('_') + 1,
                                csvFile.getName().lastIndexOf('.')
                            )
                        );
                        csvFile = new File(name +
                            "_pwi_" + (ver+1) + ".csv");
                    } else {
                        csvFile = new File(name + "_pwi_1.csv");
                    }
                }

                PrintWriter pw = new PrintWriter(csvFile);

                // print header row
                pw.print(name + ",");
                for (int i = 0; i < methods.size(); i++){
                    pw.print(methods.get(i));
                    if (i < methods.size()-1)
                        pw.print(",");
                }
                pw.println();
                
                // Print Matrix
                for (int i = 0; i < mat.length; i++){ // rows
                    pw.print(methods.get(i) + ",");
                    for (int j = 0; j < mat[i].length; j++){
                        pw.print(mat[i][j]); // May need to swap i & j
                    if (j < mat[i].length-1)
                        pw.print(",");
                    }
                    pw.println();
                }

                pw.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        } else {
            System.err.println("Number of Method Names (" + methods.size() +
            ") does not match the size of the provided array's columns" +
            "and rows" + "(" + mat.length + ", " + mat[0].length + ")");
        }
    }

    /**
     * Exports the provided 2d matrix of methods' z scores to .csv file.
     *
     * @param   mat     1d matrix of type double: represents z scores
     * @param   methods ArrayList of the names of the methods in order to
     *                  match the content in mat.
     * @param   name    name of the multilog mat was derived from.
     * @param   method  name of the method that mat applies to.
     */
    public static void singletonExportCSV(double[] mat,
            ArrayList <String> methods, String name, String method){

        if (mat.length == methods.size()){
            try{
                File csvFile = new File(name + ".csv");
                int ver;
                while (csvFile.exists()){
                    if(csvFile.getName().contains("_pwi_")){
                        ver = Integer.parseInt(
                            csvFile.getName().substring(
                                csvFile.getName().lastIndexOf('_') + 1,
                                csvFile.getName().lastIndexOf('.')
                            )
                        );
                        csvFile = new File(name +
                            "_pwi_" + (ver+1) + ".csv");
                    } else {
                        csvFile = new File(name + "_pwi_1.csv");
                    }
                }
                String dir = csvFile.getName().substring(
                        0, csvFile.getName().lastIndexOf('.')
                        );
                File directory = new File(dir);
                if (!directory.exists()){
                    directory.mkdir();
                }

                PrintWriter pw = new PrintWriter( 
                    dir + File.separator + method + ".csv"
                    );

                // print header row
                pw.print(name + ",");
                for (int i = 0; i < methods.size(); i++){
                    pw.print(methods.get(i));
                    if (i < methods.size()-1)
                        pw.print(",");
                }
                pw.println();
                
                // Print Matrix Row
                pw.print(method + ",");
                for (int j = 0; j < mat.length; j++){
                    pw.print(mat[j]); // May need to swap i & j
                if (j < mat.length-1)
                    pw.print(",");
                }
                pw.println();

                pw.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        } else {
            System.err.println("Number of Method Names (" + methods.size() +
            ") does not match the size of the provided array's columns" + 
            "(" + mat.length + ")");
        }
    }

    public static void main(String[] args){
        MultiLog ml = new MultiLog("sml", "BatchName", true); 
        double[][] m = process (ml, true);
    
        /* Print
        for (int i = 0; i < m.length; i++){
            for (int j = 0; j < m[i].length; j++){
                System.out.printf("%e ", m[i][j]);
            }
            System.out.println();
        }
        //*/

        System.out.println("size = " + m.length + " by " + m[0].length);
        
        System.out.println("defectFiles = " + defectFiles.size());
        for(int i = 0; i < defectFiles.size(); i++)
            System.out.println(defectFiles.get(i));
        //ml.print();
        
        /*  Test LogData Print Out
        try{
            LogData l = new LogData("Experimentsexp # 5-2016-12-18.txt");
            l.print();
        }
        catch (InvalidLogFileType | InvalidLogStructure | ResultContainsNaN |
                NotADirectory e){
            e.printStackTrace();
        }
        */
    }
}
