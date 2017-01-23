Pairwise Independence of JGAAP Linguistic Analysis Methods & Log Parser
==

This repository includes the Parser for JGAAP log files and the Pairwise Indepenence calculator that will determine the independence between all pairs of methods.

For determining pairwise independence between methods, call process() from PairwiseIndependence.java and supply it with a MultiLog.

To Make a MultiLog object, call new MultiLog("file/path/to/Directory/of/Logs");

Full Parameter list is: MultiLog("file/path", "ExperimentName", byDir)

The First parameter will either be a file path to a directory of logs, or, if byDir is true, then a file path to a directory of directories of logs.

If you have a directory of directories of logs, then you must pass the String file path, a string name of the Experiment you are running, and boolean value: true. 

* If a large experiment were to end early via termination or crash, the files saved in the temporary directory will be deleted if the same experiment were to be re-run, due to the names being the same. This is not as much an error in the code, as it is an obvious result from the expected nature of a temporary data storage. These files in this temporary directory ONLY serve as a safeguard for the current instance, nothing more.

TODO
--
* Refine Pairwise Independence Statistics
    + Decide on how to handle 100% success and failure files
* The Files read in do not read in ascending order, but rather some alpha-numeric method. An example order of reading: 1, 10, 11, 2, 20, 21, 3. Is this a problem?
* Fix TestData to store the numerical value of NGrams and other numeric information as actual number data.
* Triangular Array for the PairWise Independence, and other optimizatoins.
* Create single Pairwise class that the Zscore(Independence) and Covariance classes call upon.
* Adjust the Covariance class to be a correlation class, so that the magnitude of the relation between two methods is more easily interpretable. (just have to find each methods standard deviation)
