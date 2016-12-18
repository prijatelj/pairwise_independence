Pairwise Independence of JGAAP Linguistic Analysis Methods & Log Parser
==

This repository includes the Parser for JGAAP log files and the Pairwise Indepenence calculator that will determine the independence between all pairs of methods.

TODO
--
* Get the Pairwise Independence Statistics to work!
    + Fix the statistics so if there is a tie, it is a failure, not success. currently, the check if correct only checks if the first placed document is the correct document, it does not even acknowledge ties, and will accept a tie iff the correct document is placed in the first spot.
    + Fix TestData to store the placement integer of the results, to indicate ties.
* The Files read in do not read in ascending order, but rather some alpha-numeric method. An example order of reading: 1, 10, 11, 2, 20, 21, 3.
* Fix MultiLog to also accept a directory of singular Experiement logs (filled with multiple test with same method)
* Fix how EventDrivers are stored in TestData, and adjust for correct heirarchy
* Fix TestData to store the numerical value of NGrams and other numeric information as actual number data.

