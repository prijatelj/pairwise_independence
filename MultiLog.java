
/**
 * Given a file path to a directory, loops through all files and converts
 * all valid JGAAP docs into an ArrayList of LogData objects stored along
 * with the name of the grouping of logs, either the dir name or given name.
 *
 * @author Derek S. Prijatelj
 *
 * TODO The Logs are NOT in order as expected. they go by 0-9 of first, but then
 * if it has any other numbers following it, they are processed first. So 10
 * comes right after 1, and before the teens and all that before 2. This is due
 * to how the files are read in by the machine.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class MultiLog {
	public String name;
	public ArrayList<LogData> logs = new ArrayList<>();

	public MultiLog() {
	}

	public MultiLog(String pathToDir) {
		this(pathToDir, "");
	}

	public MultiLog(String pathToDir, String name) {
		this(pathToDir, name, false);
	}

	/**
	 * Constructor creates a MultiLog from a directory of log files with each
	 * log a different method, or from a directory of directories of log files,
	 * where each directoy signifies a new method.
	 *
	 * @param pathToDir
	 *            String representation of file path to Multi Log Dir
	 * @param name
	 *            String name of MultiLog, default name of Multi Log Dir
	 * @param byDir
	 *            Boolean switch: True: Method change by directories
	 *
	 *            TODO May want to edit LogData to have a constructor that
	 *            accepts File
	 */
	public MultiLog(String pathToDir, String name, boolean byDir) {
		try {
			File mlDir = new File(pathToDir);
			if (!mlDir.isDirectory()) {
				throw new NotADirectory();
			}
			if (name.isEmpty()) {
				this.name = mlDir.getName();
			} else {
				this.name = name;
			}

			if (byDir) {
				for (final File file : mlDir.listFiles()) {
					try {
						// TODO Need to ensure I do not add nulls at all to logs
						logs.add(new LogData(file.getPath(), true));
					} catch (InvalidLogFileType | InvalidLogStructure | NotADirectory e) {
						continue;
					} catch (ResultContainsNaN e) {
						e.printStackTrace();
						continue;
						/*
						 * TODO by catching this error, do I immediately stop
						 * the creation of that log? If so good, otherwise, need
						 * to delete or overwrite that log form logs.
						 */
					}
				}
			} else {
				// System.out.println("Number of Files:" +
				// mlDir.listFiles().length);
				for (final File file : mlDir.listFiles()) {
					try {
						// TODO Need to ensure I do not add nulls at all to logs
						logs.add(new LogData(file.getPath()));
					} catch (InvalidLogFileType | InvalidLogStructure | NotADirectory e) {
						continue;
					} catch (ResultContainsNaN e) {
						e.printStackTrace();
						continue;
						/*
						 * TODO by catching this error, do I immediately stop
						 * the creation of that log? If so good, otherwise, need
						 * to delete or overwrite that log form logs.
						 */
					}
				}
			}
		}
		/*
		 * catch (IOException e) { e.printStackTrace(); }
		 */
		catch (NotADirectory e) {
			System.err.println("Error: " + pathToDir + " is not a valid " + "directory");
			e.printStackTrace();
		}
	}

	public void print() {
		System.out.println("\nMultiLog: " + name);
		for (int i = 0; i < logs.size(); i++) {
			logs.get(i).print();
			System.out.println();
		}
	}
}
