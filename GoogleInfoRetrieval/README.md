# Google Information Retrieval Interaction System
### COMS6111 Advanced Database Systems
 
We present an interactive information retrieval system with Google custom search API based on Rocchio algorithm.

  - COMS 6111 Advanced Database Systems Project 1
  - Group 15
  
### Features

  - Input a query and desired precision, receive a whole round of 10 short entries from Google
  - Indicate whether a single snippet is relevant to the query by clicking 'Y' or 'N'
  - Query is enhanced, next round's entries are going to be even more relevant thanks to Rocchio!
  - Stop when desired precision is obtained
  
### Project Structure

    .
    ├── GoogleInfoRetrieval-jar-with-dependencies.jar
    ├── README.md
    ├── pom.xml
    ├── src
    │   └── main
    │       └── java
    │           └── com
    │               └── coms6111
    │                   └── group15
    │                       └── proj1
    │                           ├── Controller
    │                           │   └── Retrieval.java
    │                           ├── Model
    │                           │   ├── DocumentEntry.java
    │                           │   ├── QueryEntry.java
    │                           │   ├── Rocchio.java
    │                           │   └── StopWordsBag.java
    │                           └── Util
    │                               ├── APIUtils.java
    │                               └── IOUtils.java
    ├── stopwords.txt
    └── transcript.txt

- *[src]* contains the source code for this project
  - *[package com.coms6111.group15.proj1.Controller]* contains **Retrieval.java** which maintains the logic of round-by-round interactions
  - *[package com.coms6111.group15.proj1.Model]* contains **DocumentEntry.java**, **QueryEntry.java**, **Rocchio.java** and **StopWordsBag.java**, which are four object classes to manipulate
  - *[package com.coms6111.group15.proj1.Util]* contains **APIUtils.java** and **IOUtils.java** which are helpful to System IO and Google custom search API calling.


### Instructions for running programs

The *usage* of running program is 
```sh
$ java -jar GoogleInfoRetrieval-jar-with-dependencies.jar <APISearchKey> <EngineId> <Precison> <query>
```
**Notes**:
 - You can provide your own APISearchKey, EngineId, Precision and query
 - *stopwords.txt* should be put into root directory
 - <Precision> should be a double-digit double between 0.0 and 1.0 (e.g., 0.8) 
 - If <query> is more than one word, round them with ""(e.g., "arcade fire")
 - This program reads *stopwords.txt* from root directory, and will output *transcript.txt* after deleting the previous existing one
 - If '*CTRL-C*' signal appears, since outputStream is not closed, new *transcript.txt* will not be formed. *transcript.txt* refreshes only after the system exits with code 0
 
### Internal Design
This Project is designed under OOD Manner with Typical Controller - Model - Util pattern.
 - Controller 
   - **Retrieval.java** maintains the business logic of the whole process.
 - Model
   - *QueryEntry.java* stands for the model of the query that user inputs, it uses a *map* to store term frequency inside one query
   - *StopWordsBag.java* is the model to take in all words inside *stopwords.txt* using a *set* and determine if one word is inside that set 
   - *DocumentEntry.java* is the model of the retrieved title and snippets from Google API. It collects words's term frequency from each round's result, interact with *Rocchio* class to modify term weight map.
   - *Rocchio.java* is the implementation of Rocchio algorithm to add new words into query
 - Util
   - *IOUtils* contains all methods of System IOs and file read / write
   - *APIUtils* contains all method of calling Google Custom Search API

### How to do query augmentation
 
 
 - First, we calculate a tfMap for our the current query. 
 - Second, for the 10 returned items, we divided them to two seperate lists based on user's choice, relevant list and irrelevant list. In this process, we need to calculate the relevant and irrelevant term weights respectively. 
 - After that, we comes to dfMap, which counts the frequency of each word in tfMap. 
 - Then, for every term in positive contributors (query word and relevant word), use *Rocchio* algorithm to calculate new weights for every term:

```
newTermweight = alpha * previousQueryWeight + beta * revelantTermWeight + gama * irrelevantTermWeight
```
- Finally, we choose the top 2 terms from our new term weight map to construct a new query.
 
### Google Custom Search Engine API Key and Engine ID
- Google Custom Search Engine API Key: AIzaSyClVW-iN4ZPlOuWBPoZ_wPYCpGSCWT1LmI
- Engine Id:  009351493534667843800:hf-txjxwl2y

[src]:<https://github.com/byyang007/GoogleInfoRetrieval/tree/master/src>
[package com.coms6111.group15.proj1.Controller]:<https://github.com/byyang007/GoogleInfoRetrieval/tree/master/src/main/java/com/coms6111/group15/proj1/Controller>
[package com.coms6111.group15.proj1.Model]:<https://github.com/byyang007/GoogleInfoRetrieval/tree/master/src/main/java/com/coms6111/group15/proj1/Model>
[package com.coms6111.group15.proj1.Util]:<https://github.com/byyang007/GoogleInfoRetrieval/tree/master/src/main/java/com/coms6111/group15/proj1/Util>
