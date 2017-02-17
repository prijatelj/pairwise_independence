
/**
 * Given a MultiLog object, uses two-porportion z-test to determine the 
 * independence between all pairs of methods depicted in the logs on same
 * test set. This requires same test, and same number of Tests for each 
 * method.
 *
 * @author Derek S. Prijatelj
 *
 * TODO Fix comments to be covariance specific.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.lang.Math;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

class Correlation {

	public static ArrayList<String> defectFiles = new ArrayList<>();
	/*
	 * TODO
	 *
	 * Double [L][L] (double max) down the diagonal. Every row & col contains a
	 * single double for the rate of pairwise independence between the
	 * respective methods. Use triangle array
	 *
	 * Needs to appropriately handle all methods that Fail all tests to give all
	 * compared to values the maximum double value, because non-computeable and
	 * gives us no information.
	 */

	/**
	 * Finds the pairwise correlation matrix between all logs provided in the
	 * given MultiLog.
	 * 
	 * @param ml MultiLog with all logs of exact same test set.
	 * @return 2d Double matrix where each row, col pair contains the respective
	 * independence value.
	 */
	public static double[][] process(MultiLog ml) {
		ArrayList<String> logDataNames = new ArrayList<>();
		// for passing the to export . . .
		for (int i = 0; i < ml.logs.size(); i++) {
			logDataNames.add(ml.logs.get(i).name);
		}
		int numTrials = ml.logs.size();
		
		
		int numExperimentsPerTrial = ml.logs.get(0).tests.size();
		int[][] correct = new int[numTrials][];
		double[][] cor = new double[numTrials][numTrials];
		for (int i = 0; i < numTrials; i++)
			correct[i] = determineCorrectness(ml, i);
		for (int i = 0; i < numTrials; i++)
			for (int j = 0; j < numTrials; j++) 
				cor[i][j] = correlate(correct[i], correct[j]);			
		exportCSV(cor, logDataNames, ml.name);
		return cor;
	}

	private static double correlate(int[] xs, int[] ys) {
		int n = xs.length;
		int xcount = 0;
		int ycount = 0;
		for (int i = 0; i < n; i++) {
			if (xs[i] == 1)
				xcount++;
			if (ys[i] == 1)
				ycount++;
		}

		double px = xcount / (double) n;
		double py = ycount / (double) n;

		double sumSquaredDevX = 0;
		double sumSquaredDevY = 0;

		for (int i = 0; i < n; i++) {
			sumSquaredDevX += Math.pow(xs[i] - px, 2);
			sumSquaredDevY += Math.pow(ys[i] - py, 2);
		}

		double sigX = Math.sqrt(sumSquaredDevX / (n - 1));
		double sigY = Math.sqrt(sumSquaredDevY / (n - 1));
		double r = 0;

		for (int i = 0; i < n; i++) {
			r += ((xs[i] - px) / sigX) * ((ys[i] - py) / sigY);
		}
		
		r = r / (n - 1);
		
		return r;
	}

	private static int[] determineCorrectness(MultiLog ml, int trialNumber) {
		int numExperiments = ml.logs.get(0).tests.size();
		int[] correct = new int[numExperiments];
		for (int t = 0; t < numExperiments; t++)
			correct[t] = isCorrect(ml, trialNumber, t) ? 1 : 0;
		return correct;
	}

	/**
	 * Checks if the first placed doc if it matches correct author.
	 * 
	 * @return Returns true iff top most document author matches correct author
	 *         and there is no tie for first place. Otherwise, false.
	 */
	private static boolean isCorrect(MultiLog ml, int i, int t) {
		// System.out.println("file: "+ ml.logs.get(i).name);
		String s1[] = ml.logs.get(i).tests.get(t).questionedDoc.trim().split(" ");
		String s2 = ml.logs.get(i).tests.get(t).results.get(0).author;
		if (s1.length <= 1) {
			if (!defectFiles.contains(ml.logs.get(i).name))
				defectFiles.add(ml.logs.get(i).name);
			return false;
		}
		// System.out.println(s1[1] + " ? "+ s2 + " : " + s1[1].equals(s2));
		if (s2.contains(" ")) { // Author must always be first
			s2 = (s2.split(" "))[0];
		}

		return s1[1].equals(s2) && ml.logs.get(i).tests.get(t).results.get(0).rank != ml.logs.get(i).tests.get(t).results.get(1).rank;
	}

	/**
	 * Finds covariance.
	 *
	 * @return covariance
	 */
	private static double cov(double ex, double ey, double exy) {
		return exy - (ex * ey);
	}

	/**
	 * Exports the provided 2d matrix of methods' z scores to .csv file.
	 *
	 * @param mat
	 *            2d matrix of type double: represents z scores
	 * @param methods
	 *            ArrayList of the names of the methods in order to match the
	 *            content in mat.
	 * @param name
	 *            name of the multilog mat was derived from.
	 */
	public static void exportCSV(double[][] mat, ArrayList<String> methods, String name) {

		if (mat.length == methods.size() && mat[0].length == methods.size()) {

			try {
				File csvFile = new File(name + ".csv");
				int ver;
				while (csvFile.exists()) {
					if (csvFile.getName().contains("_pwi_")) {
						ver = Integer.parseInt(csvFile.getName().substring(csvFile.getName().lastIndexOf('_') + 1, csvFile.getName().lastIndexOf('.')));
						csvFile = new File(name + "_pwi_" + (ver + 1) + ".csv");
					} else {
						csvFile = new File(name + "_pwi_1.csv");
					}
				}

				PrintWriter pw = new PrintWriter(csvFile);

				// print header row
				pw.print(name + ",");
				for (int i = 0; i < methods.size(); i++) {
					pw.print(methods.get(i));
					if (i < methods.size() - 1)
						pw.print(",");
				}
				pw.println();

				// Print Matrix
				for (int i = 0; i < mat.length; i++) { // rows
					pw.print(methods.get(i) + ",");
					for (int j = 0; j < mat[i].length; j++) {
						pw.print(mat[i][j]); // May need to swap i & j
						if (j < mat[i].length - 1)
							pw.print(",");
					}
					pw.println();
				}

				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Number of Method Names (" + methods.size() + ") does not match the size of the provided array's columns" + "and rows" + "(" + mat.length + ", " + mat[0].length + ")");
		}
	}

	/**
	 * Exports the provided 2d matrix of methods' z scores to .csv file.
	 *
	 * @param mat
	 *            1d matrix of type double: represents z scores
	 * @param methods
	 *            ArrayList of the names of the methods in order to match the
	 *            content in mat.
	 * @param name
	 *            name of the multilog mat was derived from.
	 * @param method
	 *            name of the method that mat applies to.
	 */
	public static void singletonExportCSV(double[] mat, ArrayList<String> methods, String name, String method) {

		if (mat.length == methods.size()) {
			try {
				File csvFile = new File(name + ".csv");
				int ver;
				while (csvFile.exists()) {
					if (csvFile.getName().contains("_pwi_")) {
						ver = Integer.parseInt(csvFile.getName().substring(csvFile.getName().lastIndexOf('_') + 1, csvFile.getName().lastIndexOf('.')));
						csvFile = new File(name + "_pwi_" + (ver + 1) + ".csv");
					} else {
						csvFile = new File(name + "_pwi_1.csv");
					}
				}
				String dir = csvFile.getName().substring(0, csvFile.getName().lastIndexOf('.'));
				File directory = new File(dir);
				if (!directory.exists()) {
					directory.mkdir();
				}

				PrintWriter pw = new PrintWriter(dir + File.separator + method + ".csv");

				// print header row
				pw.print(name + ",");
				for (int i = 0; i < methods.size(); i++) {
					pw.print(methods.get(i));
					if (i < methods.size() - 1)
						pw.print(",");
				}
				pw.println();

				// Print Matrix Row
				pw.print(method + ",");
				for (int j = 0; j < mat.length; j++) {
					pw.print(mat[j]); // May need to swap i & j
					if (j < mat.length - 1)
						pw.print(",");
				}
				pw.println();

				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Number of Method Names (" + methods.size() + ") does not match the size of the provided array's columns" + "(" + mat.length + ")");
		}
	}

	public static void main(String[] args) {
		MultiLog ml = new MultiLog("logs", "BatchName", false);
		double[][] m = process(ml, true);

		System.out.println("size = " + m.length + " by " + m[0].length);

		System.out.println("defectFiles = " + defectFiles.size());
		for (int i = 0; i < defectFiles.size(); i++)
			System.out.println(defectFiles.get(i));
	}
}
