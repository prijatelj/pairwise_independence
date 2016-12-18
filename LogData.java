/**
 * Given JGAAP log file, parses all useful data from log. Parser is highly
 * dependent based on in text qualities of the log file.
 *
 * @author Derek S. Prijatelj
 */

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LogData{
    public class Pair{
        public String author;
        public double value;
        public Pair(){}

        public Pair(String author, double value){
            this.author = author;
            this.value = value;
        }
    }
    
    /**
     * Stores all information of a single JGAAP test.
     * 
     * TODO This object should be the one to process all the data from the file
     * in its own constructors for each test. Then the LogData strings the tests
     * together neatly in an ArrayList. If there is an empty line after every
     * test, this should be implementable. This may not be necessary though, 
     * only prettier.
     */
    public class TestData{
        public String questionedDoc;
        public ArrayList <String> canonicizers;
        public ArrayList <String> eventDrivers;
        public ArrayList <String> analysis;
        public ArrayList <Pair> results;
        public TestData(){}
        
        public TestData (String docName){
            questionedDoc = docName;
            canonicizers = new ArrayList <>();
            eventDrivers = new ArrayList <>();
            analysis = new ArrayList <>();
            results = new ArrayList <>();
        }
    }

    public String name; // either name of single log file or dir of logs.
    public ArrayList <TestData> tests = new ArrayList <>();

    public LogData(){}
    public LogData(String logFilePath)
            throws InvalidLogFileType, InvalidLogStructure, NotADirectory{
        this(logFilePath, false);
    }
    
    /**
     * Constructs LogData object either from a single given file, or a given
     * directory known to contain files that have the same test set and have the
     * exact same test method.
     *
     * @param   logFilePath String of file path to the log file
     * @param   isDir       True if given path is to a directory, file otherwise
     */
    public LogData(String filePath, boolean isDir)
            throws InvalidLogFileType, InvalidLogStructure, NotADirectory{
        try{
            if(isDir){
                File dir = new File(filePath);
                if (!dir.isDirectory()){
                    throw new NotADirectory();
                }
                name = dir.getName();
                for (final File logDir : dir.listFiles()){
                    try{
                        // TODO fix weak file type check
                        if (logDir.isDirectory())
                            throw new NotADirectory();
                        parseBegin(logDir);
                    }
                    catch (InvalidLogFileType | InvalidLogStructure | 
                            NotADirectory e) {
                        System.err.println("Error: " + logDir.getPath() + 
                        " is not a valid log file: Continuing with test batch");
                        continue;
                    }
                }
            } else {
                // TODO fix weak file type check
                if (!(filePath.contains(".")
                    && (filePath.substring(filePath.lastIndexOf('.'))).
                    equals(".txt")))
                    throw new InvalidLogFileType();

                File logFile = new File(filePath);
                if (logFile.isDirectory())
                    throw new InvalidLogFileType();
                
                name = logFile.getName();
                parseBegin(logFile);
            }
        }
        catch (InvalidLogFileType e){
            System.err.println("Error: " + filePath + " is not a valid "+
                "log file type");
            throw e;
        }
        catch (NotADirectory e) {
            System.err.println("Error: " + filePath + " is not a valid "+
                "directory");
            throw e;
        }
    }

    /**
     * Given a log file, begins the parsing process if valid log file.
     *
     * @param   logFile Log file to be parsed.
     */
    private void parseBegin(File logFile)
            throws InvalidLogFileType, InvalidLogStructure{
        try{
            Scanner sc = new Scanner(logFile);
            String line;
            
            // loops through all blank lines until all log tests processed
            while (sc.hasNextLine()){
                line = sc.nextLine();
                if (!line.isEmpty() && line.length() >= 3
                        && line.contains(" ")){
                    
                    tests.add(new TestData(line.substring(0,
                        line.lastIndexOf(' '))));
                    line = sc.nextLine();

                    if (line.length() >= 12 && 
                            (line.substring(0, 12)).equals("Canonicizers")
                            && sc.hasNextLine()){
                        parseCanonicizer(sc, tests.get(tests.size()-1));
                    } else{
                        sc.close();
                        throw new InvalidLogStructure();
                    }
                } else if (!line.isEmpty()) {
                    sc.close();
                    throw new InvalidLogStructure();
                }
            }
            sc.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        /*
         * TODO The means of handling and rethrowing an error may be incorrect
         * here.
         */
        catch (InvalidLogStructure e){
            System.err.println("Error: Invalid Log Structure or Syntax");
            throw e;
        }
    }
    
    /**
     * Parses the Canonicizer part of the current test in the log file. Updates
     * the current TestData object in tests.
     *
     * @param   sc      Scanner used to read the log file
     * @param   test    TestData object being added to
     * 
     * TODO May lead to error if there exists any canonicizer that starts with 
     *      "EventDrivers"
     */
    private void parseCanonicizer(Scanner sc, TestData test) 
            throws IOException, InvalidLogStructure{
        
        String line = (sc.nextLine()).trim();
        
        while(!( line.length() >= 12 
                && (line.substring(0, 12)).equals("EventDrivers"))
                && !line.isEmpty()){
            test.canonicizers.add(line);
            if (sc.hasNextLine())
                line = (sc.nextLine()).trim();
            else {
                sc.close();
                throw new InvalidLogStructure();
            }
        }

        if (line.length() >= 12
                && (line.substring(0, 12)).equals("EventDrivers")
                && sc.hasNextLine()){
            parseEventDrivers(sc, test);
        } else {
            sc.close();
            throw new InvalidLogStructure();
        }
    }

    
    /**
     * Parses the EventDriver part of the current test in the log file. Updates
     * the current TestData object in tests.
     *
     * @param   sc      Scanner used to read the log file
     * @param   test    TestData object being added to
     *
     * TODO: Appropriately handle the hierarchy of data of Event Handlers
     * TODO May lead to error if there exists any EventDrivers that starts with 
     *      "Analysis"
     */
    private void parseEventDrivers(Scanner sc, TestData test) 
            throws IOException, InvalidLogStructure{

        String line = (sc.nextLine()).trim();
        
        while(!(line.length() >= 8 && (line.substring(0, 8)).equals("Analysis"))
                && !line.isEmpty()){
            test.eventDrivers.add(line);
            if (sc.hasNextLine())
                line = (sc.nextLine()).trim();
            else {
                sc.close();
                throw new InvalidLogStructure();
            }
        }
        
        if (line.length() >= 8 && (line.substring(0, 8)).equals("Analysis")
                && sc.hasNextLine()){
            parseAnalysis(sc, test);
        } else {
            sc.close();
            throw new InvalidLogStructure();
        }
    }
    
    /**
     * Parses the Analysis part of the current test in the log file. Updates
     * the current TestData object in tests.
     *
     * @param   sc      Scanner used to read the log file
     * @param   test    TestData object being added to
     */
    private void parseAnalysis(Scanner sc, TestData test) 
            throws IOException, InvalidLogStructure{
        
        String line = (sc.nextLine()).trim();
        
        while(!(line.length() >= 2 && (line.substring(0, 2)).equals("1."))
                && !line.isEmpty()){
            test.analysis.add(line);
            if (sc.hasNextLine())
                line = (sc.nextLine()).trim();
            else {
                sc.close();
                throw new InvalidLogStructure();
            }
        }
        
        if (line.length() >= 2 && (line.substring(0, 2)).equals("1.")){
            parseResults(sc, test, line);
        } else {
            sc.close();
            throw new InvalidLogStructure();
        }
    }

    /**
     * Parses the analyzed results of the current test in the log file. Updates
     * the current TestData object in tests. The results are in the order 
     * presented in the file.
     *
     * @param   sc      Scanner used to read the log file
     * @param   test    TestData object being added to
     */
    private void parseResults(Scanner sc, TestData test, String line)
            throws IOException, InvalidLogStructure{
        
        String[] strArr;
        do{
            // tokenize the content. 2nd token is author name, 3rd prob value
            strArr = line.split(" ");
            test.results.add(new Pair(strArr[1],
                Double.parseDouble(strArr[2])));

            if (sc.hasNextLine())
                line = (sc.nextLine()).trim();
            else
                break;
        } while (!line.isEmpty() );
    }
    
    /**
     * Prints out the entire Log's data in order, as seen in the format of the
     * actual log.
     *
     * EventDrivers will obviously be incorrect due to the above todo.
     */
    public void print(){
        System.out.println("\nLog: " + name);
        for (int i = 0; i < tests.size(); i++){
            System.out.println("\nQuestioned Document: " +
                tests.get(i).questionedDoc);
            
            System.out.println("Canonicizers:");
            for (int j = 0; j < tests.get(i).canonicizers.size(); j++){
                System.out.println("\t"+tests.get(i).canonicizers.get(j));
            }

            System.out.println("EventDrivers:");
            for (int j = 0; j < tests.get(i).eventDrivers.size(); j++){
                System.out.println("\t"+tests.get(i).eventDrivers.get(j));
            }

            System.out.println("Analysis:");
            for (int j = 0; j < tests.get(i).analysis.size(); j++){
                System.out.println("\t"+tests.get(i).analysis.get(j));
            }

            for (int j = 0; j < tests.get(i).results.size(); j++){
                System.out.println((j+1) + ". "
                    + tests.get(i).results.get(j).author
                    + " " + tests.get(i).results.get(j).value);
            }
        }
    }
}
