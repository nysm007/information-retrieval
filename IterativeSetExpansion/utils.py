# utils.py is for utilization of System I/Os and HTTP Get Methods
import os
import sys
from enum import Enum
import requests
from urllib import parse
import json

EXEC_PREFIX = ''
WRITE_PATH = os.path.abspath(os.path.dirname(__file__)) + '/' + 'transcript.txt'


class Writer:
    def __init__(self, *writers):
        self.writers = writers

    def write(self, text):
        for w in self.writers:
            w.write(text)


class GoogleSearchParams(Enum):
    SEARCH_KEY = '&key='
    SEARCH_ENGINE_ID = '&cx='
    QUERY = 'q'
    FORMAT = 'alt'
    NUM = 'num'


def set_write_path(write_path):
    global WRITE_PATH
    WRITE_PATH = write_path


def write(t):
    f_out = open(WRITE_PATH, 'a')
    print(t)
    f_out.write(t)
    f_out.write('\n')


def truncate():
    """
    truncate the file at designated WRITE_PATH
    :return: void
    """
    raw = open(WRITE_PATH, "r+")
    raw.seek(0)
    raw.truncate()


def find_relation(val):
    # 1 is for Live_In, 2 is for Located_In, 3 is for OrgBased_In, and 4 is for Work_For
    return {
        1: 'Live_In',
        2: 'Located_In',
        3: 'OrgBased_In',
        4: 'Work_For'
    }.get(val, 'None_Found')


def initialize_params():
    """
    READ params from STDIN to global variables, some params transformed to int
    :return: generate transformed params from STDIN
    """
    system_input = sys.argv[1:]
    if len(system_input) != 6:
        wrong_system_input()
    for i in range(len(system_input)):
        if i == 2:
            relation = find_relation(int(system_input[i]))
            if relation == 'None_Found':
                wrong_system_input(True)
            yield relation
        elif i == 5:
            yield int(system_input[i])
        elif i == 3:
            yield float(system_input[i])
        elif i == 4:
            yield str(system_input[i]).lower()
        else:
            yield system_input[i]


def wrong_system_input(wrong_relation=False):
    """
    Print instructions if received wrong input from STDIN
    :return: void
    """
    global EXEC_PREFIX
    write('Usage: ' + EXEC_PREFIX + " <API Key> <Engine Key> <Relation> <Threshold> <'Query'> <k-tuples>")
    if wrong_relation:
        write('Invalid relation. Per relation, please input an integer from 1 to 4')
    sys.exit()


def google_custom_search_get(query,search_key='AIzaSyClVW-iN4ZPlOuWBPoZ_wPYCpGSCWT1LmI', engine_id='009351493534667843800:hf-txjxwl2y'):
    """
    implement HTTP Get for Google Custom Search
    :return: a list of dictionaries
    """
    fetched_url = []
    params = {}
    params[GoogleSearchParams.QUERY.value] = query
    params[GoogleSearchParams.FORMAT.value] = 'json'
    params[GoogleSearchParams.NUM.value] = '10'
    url = 'https://www.googleapis.com/customsearch/v1?'
    url += parse.urlencode(params)
    url += GoogleSearchParams.SEARCH_KEY.value + search_key
    url += GoogleSearchParams.SEARCH_ENGINE_ID.value + engine_id
    try:
        response = requests.get(url)
    except requests.exceptions.RequestException:
        http_get_failure()
        sys.exit()
    # payload = json.loads(response.text.encode('utf-8'))['items']
    # fix to linux
    try:
        payload = json.loads(response.text)['items']
    except KeyError:
        api_exceeds_usage()
        sys.exit()
    for var in payload:
        url = var['link']
        entry = {
            'url': url
        }
        fetched_url.append(entry)
    return fetched_url



def start_ise_process(relation, threshold, query, num_of_tuples, client_key='AIzaSyClVW-iN4ZPlOuWBPoZ_wPYCpGSCWT1LmI', engine_key='009351493534667843800:hf-txjxwl2y'):
    """
    To start everything, truncate the document first then start extracting
    :param relation:
    :param threshold:
    :param query:
    :param num_of_tuples:
    :param client_key:
    :param engine_key:
    :return:
    """
    # first truncate the document
    truncate()
    write('Parameters:')
    write('Client Key      = ' + client_key)
    write('Engine Key      = ' + engine_key)
    write('Relation        = ' + relation)
    write('Threshold       = ' + str(threshold))
    write('Query           = ' + query)
    write('# of Tuples     = ' + str(num_of_tuples))
    write('Loading necessary libraries; this will take a few seconds...')


def write_non_extractable():
    write("Program could not extract text content from this web site; moving to the next one...")


def write_processing(url):
    write('Processing: ' + url)


def http_get_failure():
    write('HTTP GET failure. Check API quota or/and internet connection')


def write_annotate_entry(entry, sentence):
    write('=============== EXTRACTED RELATION ===============')
    write('Sentence: ' + sentence)
    s = 'RelationType: ' + str(entry['relation']) + ' | Confidence=' + str(entry['confidence'])
    s += ' | EntityType1= ' + str(entry['entityType0']) + ' | EntityValue1= ' + str(entry['entityValue0'])
    s += ' | EntityType2= ' + str(entry['entityType1']) + ' | EntityValue2= ' + str(entry['entityValue1'])
    write(s)
    write('============== END OF RELATION DESC ==============')


def write_iteration(cnt, query):
    write('=========== Iteration: ' + str(cnt) + ' - Query: ' + query + ' ===========')


def write_relation_extracted(t, all):
    write('Relations extracted from this website: ' + str(t) + '(Overall: ' + str(all) + ')')


def write_pruning():
    write('Pruning relations below threshold...')


def write_pruning_result(n):
    write('Number of tuples after pruning: ' + str(n))


def write_all_relations(entry_all):
    write('== == == == == == == == == ALL RELATIONS == == == == == == == == =')
    for entry in entry_all:
        s = 'Relation Type: ' + entry['relation'] + ' | ' + 'Confidence: ' + str(round(entry['confidence'], 3)) + ' | '
        s += 'Entity # 1: ' + str(entry['entityValue0']) + ' (' + str(entry['entityType0']) + ')' + '               	 |'
        s += 'Entity # 2: ' + str(entry['entityValue1']) + ' (' + str(entry['entityType1']) + ')'
        write(s)


def write_success(n):
    write('Program reached ' + str(n) + ' number of tuples. Shutting down...')


def write_stalled(query):
    write('No relations found with query <' + str(query) + '>. Start with a different seed tuple.')
    write('Done')


def write_non_new_query_found():
    write('None new query is found')
    write('Iterative Set Expansion Stop')


def api_exceeds_usage():
    write('API exceeds daily usage')