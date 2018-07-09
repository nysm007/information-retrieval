# Project: Iterative Set Expansion Implementation
### COMS6111 Advanced Database Systems
 
We present an IR system with an implementation of *iterative set expansion* algorithm.

  - COMS 6111 Advanced Database Systems Project 2
  - Group 15
  - Project description can be found at [here]
  
### Write at the beginning
This README.md is quite long and detailed. **PLease read the running environment part**.

If reading README.md locally is uncomfortable, you can always view this 
README.md on [GitHub].

Please note that this repo is now private, we will make it public in the future

### Running Environment
This project is written using Python 3.6.2. This project needs [Stanford NLP] to run.

Important Notes:
- **All scripts in this folder(.sh, makefile) are written specifically to Python 3, you need at least version 3.5 to run it**
- **The VM to run this program needs to be Ubuntu 16.04**
- **If run on Ubuntu 14.04, the NLP part of this code will report error**
- **You should put [Stanfold NLP] folder and this folder in same directory**
  
### Commands
Refer to [here] obtain running commands instruction.

Running this program requires custom inputs.

    Params: <google api key> <google engine id> <r> <t> <q> <k>
    where:
    <google api key> is your Google Custom Search API Key (see above)
    <google engine id> is your Google Custom Search Engine ID (see above)
    <r> is an integer between 1 and 4, indicating the relation to extract: 1 is for Live_In, 2 is for Located_In, 3 is for OrgBased_In, and 4 is for Work_For
    <t> is a real number between 0 and 1, indicating the "extraction confidence threshold," which is the minimum extraction confidence that we request for the tuples in the output
    <q> is a "seed query," which is a list of words in double quotes corresponding to a plausible tuple for the relation to extract (e.g., "bill gates microsoft" for relation Work_For)
    <k> is an integer greater than 0, indicating the number of tuples that we request in the output

To run paradigm program, simply

```bash
python3 index.py API-key engine-key 4 0.35 "bill gates microsoft" 10
```
You can also use makefile

```bash
make run
```

To deploy onto ubuntu 16.04

First clone our [github repo],

```bash
git clone https://github.com/byyang007/IterativeSetExpansion.git
```

Then command into this directory and run shell scripts
```bash
cd IterativeSetExpansion
bash linux_pre_exec.sh
```
Then this program runs in nohup mode. Refer to `transcript.txt` for outputs.

### Internal Design
This project constitutes of two parts
 - The code we write which contains business logic
 - Standard Python Interface provided by [Stanford NLP]
 
Our business mainly compose of 4 parts
- **index.py**, which contains business logic
- **extraction.py**, which is responsible to extract information from url using [BeautifulSoup], then send result to **nlp.py**
- **nlp.py**, which send text through two pipelines while calling NLP client, then filter results and send back result list to **index.py**
- **utils.py**, which contains util functions w.r.t calling APIs and System I/Os

**How we extract text from web page**

Refer to *soup_text* function in extraction.py, we get rid of redundant elements like contents in between <script></script>, <style></style>,
then we remove extra blank lines in text and form target sentences line by line.

**How we perform our nlp part**

Upon receiving text from **extraction.py**, we send it to two pipelines

- pipeline1, this pipeline does not parse and reveal relation, it's aimed to reveal possible sentences which contains relations to extract
- pipeline2, this pipeline parse and reveal relation, for each sentence send from pipeline1, it processes and validate relation

**How we validate relation**

So the relation extracted by pipeline2 might be in variables forms,

we 
- (*valid_relation* function)discard those relation which 'entityType' = '0' and/or does not contain the relation type we wanted 
- (*relation_confidence_too_low* function) discard the relation where the confidence of the relation type we wanted, e.g. Work_For, is lower than any other three relation types as well as '_NR'
- (*relation_does_not_conform_to_type* function) discard those whose relation pair is not the one we wanted

Per third point, let's discuss in detail.

So we have four types of relation types - 'Live_In', 'Located_In', 'OrgBased_In', 'Work_For', each type indicate a relation pair(refer to *find_relation_noun* function in **nlp.py**):

    def find_relation_noun(val):
    return {
        'Live_In': ['PEOPLE', 'LOCATION'],
        'Located_In': ['LOCATION', 'LOCATION'],
        'OrgBased_In': ['ORGANIZATION', 'LOCATION'],
        'Work_For': ['ORGANIZATION', 'PEOPLE']
    }.get(val, [])

So for 'Work_For', the relation pair should be 'PEOPLE' work for 'ORGANIZATION', not 'PEOPLE' work for 'PEOPLE'
And that's what (*relation_does_not_conform_to_type* function)third point manage to take care of

**How we implement Iterative Set Expansion**
This is the main business logic part
First, we keep a query_set(Refer to *QUERY_SET* var in **index.py**) to contain queries for each round

Upon receiving results for 1 round 10 urls, we prune.

**How we prune**

1. Note that our relation contains in two different forms, e.g.
         
         `{'entityType0' = 'Organization', 'entityValue0' = 'Microsoft', 'EntityType1' = 'People', 'EntityValue1' = 'bill'}`
    
         `{'entityType0' = 'People', 'entityValue0' = 'bill', 'EntityType1' = 'Organization', 'EntityValue1' = 'Microsoft'}`

These two relations are actually one relation, so (Refer to *conform_entity_value* function) we conform these two types to a standard relation type with highest confidence of two

2. (Refer to *bottling* function)Then we discard those with confidence lower than designated

3. Then (Refer to *remove_extra_noun* function), we only keep the relation which has highest confidence for each 'EntityValue1'

    e.g. we have
    
    `{'entityType0' = 'Organization', 'entityValue0' = 'Microsoft', 'EntityType1' = 'People', 'EntityValue1' = 'bill', 'confidence' = '0.5'}`
    
    `{'entityType0' = 'Organization', 'entityValue0' = 'Google', 'EntityType1' = 'People', 'EntityValue1' = 'bill', 'confidence' = '0.35'}`

   Then we only keep the first relation and discard the second

4. (Refer to *remove_stopwords* function)Then we remove entries if the 'entityValue' is contained by stopwords
   e.g. discard those relation which 'Laboratory' work for 'School'

5. Then we reversely sort entries by confidence and this iteration is complete.

Results After Iteration1 for 'bill gates microsoft'

    Number of tuples after pruning: 9
    == == == == == == == == == ALL RELATIONS == == == == == == == == =
    Relation Type: Work_For | Confidence: 0.554 | Entity # 1: Microsoft (ORGANIZATION)               	 |Entity # 2: Bill (PEOPLE)
    Relation Type: Work_For | Confidence: 0.497 | Entity # 1: Google (ORGANIZATION)               	 |Entity # 2: Gates (PEOPLE)
    Relation Type: Work_For | Confidence: 0.485 | Entity # 1: Facebook (ORGANIZATION)               	 |Entity # 2: Zuckerberg (PEOPLE)
    Relation Type: Work_For | Confidence: 0.485 | Entity # 1: Microsoft (ORGANIZATION)               	 |Entity # 2: Myhrvold (PEOPLE)
    Relation Type: Work_For | Confidence: 0.485 | Entity # 1: Apple (ORGANIZATION)               	 |Entity # 2: Jobs (PEOPLE)
    Relation Type: Work_For | Confidence: 0.465 | Entity # 1: Amazon (ORGANIZATION)               	 |Entity # 2: Bezos (PEOPLE)
    Relation Type: Work_For | Confidence: 0.446 | Entity # 1: Microsoft (ORGANIZATION)               	 |Entity # 2: Nadella (PEOPLE)
    Relation Type: Work_For | Confidence: 0.444 | Entity # 1: Hathaway (ORGANIZATION)               	 |Entity # 2: Buffett (PEOPLE)
    Relation Type: Work_For | Confidence: 0.435 | Entity # 1: Microsoft (ORGANIZATION)               	 |Entity # 2: Paul (PEOPLE)
    
**How we generate new queries**

(Refer to *gen_new_query* function) Our scheme is

1. Always pick the highest confidence one if we can
2. Avoid queries which contained in query_set var we keep, that's why our new query is `zuckerberg facebook`

    =========== Iteration: 2 - Query: zuckerberg facebook ===========

### Sample Transcript
Refer to `transcript_4_0.35_billgatesmicrosoft_10.txt` for sample transcript.

### Other Python script

Since running this program requires time, if you wanna start iteration 2 separately after iteration 1, you can refer to `read_logs.py` to read logs of iteration 1.

### Google Custom Search Engine API Key and Engine ID
- Google Custom Search Engine API Key: AIzaSyClVW-iN4ZPlOuWBPoZ_wPYCpGSCWT1LmI
- Engine Id:  009351493534667843800:hf-txjxwl2y

[GitHub]:<https://github.com/byyang007/IterativeSetExpansion>
[here]:<http://www.cs.columbia.edu/~gravano/cs6111/proj2.html>
[github repo]:<https://github.com/byyang007/IterativeSetExpansion>
[Stanford NLP]:<https://stanfordnlp.github.io/CoreNLP/>
[BeautifulSoup]:<https://www.crummy.com/software/BeautifulSoup/>
