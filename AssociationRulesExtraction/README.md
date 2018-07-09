# Extract Association Rules
### COMS6111 Advanced Database Systems
 
We implemented the a-priori algorithm as presented in this [paper](http://www.cs.columbia.edu/~gravano/Qual/Papers/agrawal94.pdf).

  - COMS 6111 Advanced Database Systems Project 3
  - Group 15
  
### Project Structure

    .
    ├── INTEGRATED-DATASET.csv
    ├── README.md
    ├── data
    │   ├── DOB_Job_Application_Filings.csv
    │   └── processing_dob_job_application_filling.py
    ├── example-run.txt
    ├── pom.xml
    ├── run.sh
    └── src
        └── main
            └── java
                └── com
                    └── yq2212
                        └── cs6111
                            └── proj3
                                ├── Controller
                                │   └── Main.java
                                ├── Module
                                │   ├── BitSetComparator.java
                                │   ├── BitSetConfidenceComparator.java
                                │   ├── Candidate.java
                                │   └── CandidateComparator.java
                                └── Util
                                    └── IOUtils.java

 
### Choice of dataset
For thie project, we choose a dataset of [NYC DOB Job Application Filling](https://data.cityofnewyork.us/Housing-Development/DOB-Job-Application-Filings/ic3t-wcy2). This dataset contains all the construction jobs in NYC and lots of 
detailed information, e.g., job location, job street, job type, estimated job fee, etc.. For our project, we select Borough, Job type, Job status, landmarked and total estimated fee, because we want to take a look at which area of 
NYC has more construction/building jobs to do, and this message may be useful for the urban planing and some other things.

### High level data cleansing procedure
The original dataset contains upto 82 columns, for consideration stated above and the purpose of a clear deliver of the algorithm, we select five colums to generate our INTEGRATED_DATASET.
The first four attribute of our dataset is processed to have a "one-hot" style encoding, which is a common technique. For the last attribute, the estimated fee, we rated the cost to five level:
Very cheap, cheap, normal, expensive and very expensive. The criterion of the rating depends on the statistic characteristic. 

### Instructions for running programs

The *usage* of running program is 
```sh
$ java -jar AssociationRulesExtraction-jar-with-dependencies.jar <input.csv> <minSupp> <minConf>
```
**Notes**:
 - **minSupp** and **minConf** should be double-digit double between 0.0 and 1.0 (e.g., 0.17, 0.6) 
 - The output of this program is located in working directory as "output.txt"
 
### Internal Design
This Project is designed under OOD Manner with Typical Controller - Model - Util pattern.
 - Controller 
   - *Main.java*. This class maintains the business logic of the whole process. 
   Concretely, the Main class is responsible for system parameter input, maintain a table of all the attribute 
   as well as a table for all transactions. This transaction table is organized using self-defining structure "Candidate", and sorted
   by their transactions size.
 - Module
   - *Candidate.java*. This class is the most important design in the project. 
   Each Candidate is initialized by all the transactions in the main logic, and the size of the support items is saved for 
   the further comparision in the main iteration.
 - Util
   - *IOUtils*. This class contains all methods of System IOs and file read / write functions.
   
### Explanation of the example-run

The *example-run.txt* is the output with the *<input.csv>* as *INTEGRATED-DATASET.csv*, *minSupp* as *0.17*, *minConf* as *0.65*.

For the first two column in "minimum support" section:

``
[Alteration-2], 66%
[MANHATTAN], 50%
[Very Cheap], 49%
``

We can see that about 66% percent of the building construction type is "Alteration-2", which means "[Multiple types of work, not
 affecting use, egress or occupancy](https://www1.nyc.gov/site/buildings/homeowner/permits.page)". It's actually a reasonable point if we 
 think about it, and we can interpret this as the buildings in NYC is still safe to use/live but need renovation/decoration. 
 
 Besides, we can also see that 50% percent of building jobs is in Manhattan, and 49% activities' estimated fee are classified as "very cheap"(below $409 in 
 this dataset). 
 
 For the confidence section:
 
 ``
 [Alteration-2] => [MANHATTAN](Conf: 76%, Supp: 38%)
 ``
 
 We can do some simple speculation like: if it's a "Alteration-2" building job, we have 76% of confidence that it's happened in Manhattan.
 
