Pairwise Independence of JGAAP Linguistic Analysis Methods & Log Parser
==

This repository includes the Parser for JGAAP log files and the Pairwise Indepenence calculator that will determine the independence between all pairs of methods.

TODO
--
* ! Logs must be appropriately discarded if they contain NaN values for results section! Meaning, they must throw a unique and appropriate error to inform the user of the NaN value in file, and then the user (MultiLog and PairwiseI.) must handle that correctly (i.e. discard the test run).
    + uncertain if when Errors caught, nothing is added to ArrayList, or a null or incomplete object is added to the array list. Need to figure out and handle.
* Refine Pairwise Independence Statistics
    + Decide on how to handle 100% success and failure files
* The Files read in do not read in ascending order, but rather some alpha-numeric method. An example order of reading: 1, 10, 11, 2, 20, 21, 3.
* Fix how EventDrivers are stored in TestData, and adjust for correct heirarchy
    + Fix TestData to store the numerical value of NGrams and other numeric information as actual number data.

