import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import com.google.common.io.Files;
import com.jgaap.JGAAPConstants;
import com.jgaap.backend.API;
import com.jgaap.backend.AnalysisDrivers;
import com.jgaap.backend.CSVIO;
import com.jgaap.backend.DistanceFunctions;
import com.jgaap.backend.EventCullers;
import com.jgaap.backend.EventDrivers;
import com.jgaap.backend.ExperimentEngine;
import com.jgaap.backend.Utils;
import com.jgaap.classifiers.CentroidDriver;
import com.jgaap.classifiers.WEKASMO;
import com.jgaap.distances.CosineDistance;
import com.jgaap.distances.IntersectionDistance;
import com.jgaap.distances.ManhattanDistance;
import com.jgaap.eventDrivers.CharacterEventDriver;
import com.jgaap.eventDrivers.CharacterNGramEventDriver;
import com.jgaap.eventDrivers.DefinitionsEventDriver;
import com.jgaap.eventDrivers.NaiveWordEventDriver;
import com.jgaap.eventDrivers.RareWordsEventDriver;
import com.jgaap.generics.AnalysisDriver;
import com.jgaap.generics.DistanceFunction;
import com.jgaap.generics.EventCuller;
import com.jgaap.generics.EventDriver;
import com.jgaap.generics.NeighborAnalysisDriver;
import com.jgaap.util.Document;

public class JGAAPTester {
	static API jgaap = API.getInstance();
	static int i = 0;
	static int k = 0;
	static String pathString = "H:/Java/workspace/JGAAPMods/logs/";
	static String expTarget = "H:/Java/workspace/JGAAPMods/logs2/";
	static PrintStream sys = System.out;
	static PrintStream exp;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		sys.println("Working");
		//createExperimentCSV(new File("H:/Java/workspace/JGAAPMods/Texts/Mysteries"), .2);
		//createExperimentCSV(new File("H:/Java/workspace/JGAAPMods/Texts/SciFi"), .2);
		
//		BasicConfigurator.configure();
//
//		// loadAAACProblem("A");
//		exp = new PrintStream((new File(expTarget + "exp.csv")));
//		exp.println("Experiments");
//		sanFranciscoMethods();
//		exp.close();
//		ExperimentEngine.runExperiment(expTarget + "exp.csv", "English"); // log
//																			// file
//																			// generation
//		@SuppressWarnings("unchecked")
//		List<File> allFiles = new ArrayList<File>();
//		File dir = new File("H:/Java/workspace/JGAAPMods/tmp");
//		getFilesRecursive(dir, allFiles);
//		System.out.println(allFiles);
//		for (File f : allFiles)
//			Files.move(f, new File(expTarget + f.getName()));

		MultiLog ml = new MultiLog(expTarget,"scifi",false);
		double [][] results = Covariance.process(ml, true);
	}

	private static void sanFranciscoMethods() {
		int[] ns = { 3, 4, 6, 8 };
		// String canonicizers = "Normalize Whitespace&Unify Case";

		ArrayList<EventDriver> eds = new ArrayList<EventDriver>();
		eds.add(new NaiveWordEventDriver());
		eds.add(new RareWordsEventDriver());
		eds.add(new CharacterEventDriver());
		for (int i = 0; i < 4; i++) {
			CharacterNGramEventDriver ed = new CharacterNGramEventDriver();
			ed.setParameter("N", ns[i]);
			eds.add(ed);
		}

		List<DistanceFunction> dfs = new ArrayList<DistanceFunction>();
		dfs.add(new IntersectionDistance());
		dfs.add(new CosineDistance());
		dfs.add(new ManhattanDistance());

		AnalysisDriver cd = new CentroidDriver();
		AnalysisDriver smo = new WEKASMO();
		for (EventDriver ed : eds) {
			for (DistanceFunction df : dfs) {
				exp.println("exp # " + (i++) + "-, " + "Normalize Whitespace" + '&' + "Unify Case," + ed.displayName() + "," + cd.displayName() + "," + df.displayName() + "," + "H:/Java/workspace/JGAAPMods/Texts/Scifi/load.csv");
			}
		}

		for (EventDriver ed : eds) {
				exp.println("exp # " + (i++) + "-, " + "Normalize Whitespace" + '&' + "Unify Case," + ed.displayName() + "," + smo.displayName() + "," + "," + "H:/Java/workspace/JGAAPMods/Texts/SciFi/load.csv");
		}

	}

	private static File createExperimentCSV(File expDirectory, double testRatio) throws FileNotFoundException { // create

		File[] authors = expDirectory.listFiles();
		File f;
		PrintStream output = new PrintStream(f = new File(expDirectory.getPath() + "/load.csv"));

		for (File author : authors) {
			String cor = author.getName();
			File[] works = author.listFiles();
			for (int i = 0; i < works.length; i++) {
				String path = works[i].getPath().replace(",", "");
				if (i < works.length * testRatio)
					output.println("," + path + ", Correct: " + cor);
				else
					output.println(cor + "," +path + ",");
			}
		}

		return f;
	}

	private static void getFilesRecursive(File pFile, List<File> list) {
		System.out.println(pFile.listFiles());
		for (File files : pFile.listFiles()) {
			if (files.isDirectory()) {
				getFilesRecursive(files, list);
			} else {
				list.add(files);
			}
		}
	}

	private static void tryAllAnalyzers() {
		// int load = 5;
		List<EventDriver> eds = EventDrivers.getEventDrivers();// .subList(0,load);
		List<AnalysisDriver> ads = AnalysisDrivers.getAnalysisDrivers();// .subList(0,load);
		List<DistanceFunction> dfs = DistanceFunctions.getDistanceFunctions();// .subList(0,load);
		List<EventCuller> ecs = EventCullers.getEventCullers();// .subList(0,load);
		for (AnalysisDriver ad : ads) {
			if (ad instanceof NeighborAnalysisDriver)
				for (DistanceFunction df : dfs) {
					for (EventDriver ed : eds) {
						for (EventCuller ec : ecs) {
							try {
								exp.println("exp # " + (i++) + "-, " + "Normalize Whitespace&Unify Case," + ed.displayName() + "#" + ec.displayName() + "," + ad.displayName() + "," + df.displayName() + "," + JGAAPConstants.JGAAP_RESOURCE_PACKAGE
										+ "aaac/problem" + "A" + "/load" + "A" + ".csv");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			else {
				for (EventDriver ed : eds) {
					for (EventCuller ec : EventCullers.getEventCullers()) {
						try {
							exp.println("exp # " + (i++) + "-, " + "Normalize Whitespace&Unify Case," + ed.displayName() + "#" + ec.displayName() + "," + ad.displayName() + "," + JGAAPConstants.JGAAP_RESOURCE_PACKAGE + "aaac/problem" + "A" + "/load"
									+ "A" + ".csv");
							// go();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private static void tryEachEventDriver(List<EventDriver> eds) {
		for (EventDriver ed : eds) {
			jgaap.addEventDriver(ed);
			for (EventCuller ec : EventCullers.getEventCullers()) {
				jgaap.addEventCuller(ec, ed);
				try {
					go();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				jgaap.removeEventCuller(ec);
			}
		}
	}

	private static void tryAllSetsOfEventDrivers(List<EventDriver> eds) {
		if (eds.isEmpty() || jgaap.getEventDrivers().size() > 5) {
			long start = System.currentTimeMillis();
			try {
				go();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (!eds.isEmpty()) {
			tryEachEventDriver(eds.subList(1, eds.size()));
			EventDriver ed = eds.get(0);
			if (!(ed instanceof DefinitionsEventDriver))
				jgaap.addEventDriver(ed);
			for (EventCuller ec : EventCullers.getEventCullers()) {
				jgaap.addEventCuller(ec, ed);
				tryEachEventDriver(eds.subList(1, eds.size()));
				jgaap.removeEventCuller(ec);
			}
			jgaap.removeEventDriver(ed);

		}
	}

	private static void go() throws Exception {

		jgaap.execute();
		for (Document d : jgaap.getDocuments()) {
			if (!d.isAuthorKnown()) {
				File file = new File(pathString + (++i) + ".txt");
				file.createNewFile();
				if (i > k * 2)
					sys.println(k = i);
				PrintWriter br = new PrintWriter(file);
				br.write(d.getResult());
				br.close();
			}
		}

	}

	private static void loadAAACProblem(String problem) {
		String filepath = JGAAPConstants.JGAAP_RESOURCE_PACKAGE + "aaac/problem" + problem + "/load" + problem + ".csv";
		List<Document> documents = Collections.emptyList();
		try {
			documents = Utils.getDocumentsFromCSV(CSVIO.readCSV(com.jgaap.JGAAP.class.getResourceAsStream(filepath)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Document document : documents) {
			jgaap.addDocument(document);
		}
		// UpdateKnownDocumentsTree();
		// UpdateUnknownDocumentsTable();

	}

	private void loadSciFiNovels() throws Exception {
		List<Document> docs = new ArrayList<Document>();
		File[] authorX = (new File("E:/Java/workspace/JGAAPMods/docs/SciFi/AuthorX")).listFiles();
		File[] authorY = (new File("E:/Java/workspace/JGAAPMods/docs/SciFi/AuthorY")).listFiles();
		for (int i = 0; i < authorX.length; i++) {
			if (i <= authorX.length / 2)
				docs.add(new Document(authorX[i].getAbsolutePath(), "Azimov"));
			else
				docs.add(new Document(authorX[i].getAbsolutePath(), ""));

		}
		for (int i = 0; i < authorY.length; i++) {
			if (i < authorY.length / 2)
				docs.add(new Document(authorY[i].getAbsolutePath(), "Heinlein"));
			else
				docs.add(new Document(authorY[i].getAbsolutePath(), ""));
		}
		for (Document d : docs) {
			jgaap.addDocument(d);
		}
	}

}
