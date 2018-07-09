import os
import sys
import math
import json
from utils import *
from extraction import *
from urllib import error
from nlp import find_relation_noun
import operator
import itertools

CUSTOM_SEARCH_KEY = ''
CUSTOM_SEARCH_ENGINE_ID = ''
RELATION_FLAG = ''
CONFIDENCE_THRESHOLD = 0.0
QUERY = ''
SEED_QUERY = ''
NUM_OF_TUPLES = 0
CNT = 0
ENTRIES_ALL = []
QUERY_SET = []
STOPWORDS = []


def index():
    global CUSTOM_SEARCH_KEY, CUSTOM_SEARCH_ENGINE_ID, RELATION_FLAG, \
        CONFIDENCE_THRESHOLD, QUERY, NUM_OF_TUPLES, SEED_QUERY, QUERY_SET
    CUSTOM_SEARCH_KEY, CUSTOM_SEARCH_ENGINE_ID, RELATION_FLAG, \
        CONFIDENCE_THRESHOLD, SEED_QUERY, NUM_OF_TUPLES = initialize_params()
    QUERY = SEED_QUERY
    start_ise_process(RELATION_FLAG, CONFIDENCE_THRESHOLD, QUERY, NUM_OF_TUPLES, CUSTOM_SEARCH_KEY,
                      CUSTOM_SEARCH_ENGINE_ID)
    fetch_round()


def set_entries_all(entries_all):
    global ENTRIES_ALL
    ENTRIES_ALL = entries_all


def add_query_to_queryset(query):
    global QUERY_SET
    QUERY_SET.append(query)


def set_cnt(cnt):
    global CNT
    CNT = cnt


def set_num_of_tuples(tup):
    global NUM_OF_TUPLES
    NUM_OF_TUPLES = tup


def set_confidence(conf):
    global CONFIDENCE_THRESHOLD
    CONFIDENCE_THRESHOLD = conf


def set_relation(relation):
    global RELATION_FLAG
    RELATION_FLAG = relation


def set_search_key(key):
    global CUSTOM_SEARCH_KEY
    CUSTOM_SEARCH_KEY = key


def set_engine(engine):
    global CUSTOM_SEARCH_ENGINE_ID
    CUSTOM_SEARCH_ENGINE_ID = engine


def fetch_round():
    global CNT, ENTRIES_ALL, QUERY, QUERY_SET
    CNT += 1
    QUERY_SET.append(QUERY)
    fetched_url = google_custom_search_get(QUERY, CUSTOM_SEARCH_KEY, CUSTOM_SEARCH_ENGINE_ID)
    write_iteration(CNT, QUERY)
    for i in range(len(fetched_url)):
        try:
            entries = retrieve_and_extract(fetched_url[i]['url'], RELATION_FLAG, CONFIDENCE_THRESHOLD)
        except error.HTTPError:
            write_non_extractable()
            continue
        if len(entries) == 0:
            write_relation_extracted(len(entries), len(ENTRIES_ALL))
            continue
        for entry in entries:
            ENTRIES_ALL.append(entry)
        write_relation_extracted(len(entries), len(ENTRIES_ALL))
    # write_pruning()
    # prune()
    # size = len(ENTRIES_ALL)
    # if size == 0:
    #     write_stalled()
    #     sys.exit()
    # write_all_relations(ENTRIES_ALL)
    # if size >= NUM_OF_TUPLES:
    #     write_success(size)
    #     sys.exit()
    # else:
    #     found_new_query = False
    #     for i in range(len(ENTRIES_ALL)):
    #         QUERY = str(ENTRIES_ALL[0]['entityValue0']).lower() + str(ENTRIES_ALL[0]['entityValue1']).lower()
    #         if QUERY not in QUERY_SET:
    #             found_new_query = True
    #             break
    #     if not found_new_query:
    #         write_non_new_query_found()
    #         sys.exit()
    #     fetch_round()
    after_process()


def after_process():
    global CNT, ENTRIES_ALL, QUERY, QUERY_SET
    write_pruning()
    prune()
    size = len(ENTRIES_ALL)
    if size == 0:
        write_stalled(QUERY)
        sys.exit()
    write_all_relations(ENTRIES_ALL)
    if size >= NUM_OF_TUPLES:
        write_success(size)
        sys.exit()
    else:
        found_new_query = gen_new_query(ENTRIES_ALL)
        if not found_new_query:
            write_non_new_query_found()
            sys.exit()
        fetch_round()


def prune():
    global ENTRIES_ALL
    ENTRIES_ALL = conform_entity_value(ENTRIES_ALL)
    ENTRIES_ALL = bottling(ENTRIES_ALL)
    # distinct_list = []
    # distinct_relations = []
    # for entry in ENTRIES_ALL:
    #     relation = (entry['entityValue0'], entry['entityValue1'])
    #     relation_reverse = (entry['entityValue1'], entry['entityValue0'])
    #     if relation not in distinct_relations and relation_reverse not in distinct_relations:
    #         distinct_relations.append(relation)
    #         distinct_list.append(entry)
    #     elif relation in distinct_relations:
    #         pos = distinct_relations.index(relation)
    #         distinct_list[pos]['confidence'] = max(distinct_list[pos]['confidence'], entry['confidence'])
    #     else:
    #         pos = distinct_relations.index(relation_reverse)
    #         distinct_list[pos]['confidence'] = max(distinct_list[pos]['confidence'], entry['confidence'])
    # ENTRIES_ALL = distinct_list
    # valid_dict = []
    # for i in range(len(ENTRIES_ALL)):
    #     e = ENTRIES_ALL[i]
    #     if e['confidence'] >= CONFIDENCE_THRESHOLD:
    #         valid_dict.append(e)
    # ENTRIES_ALL = valid_dict
    # remove the situation where one people belongs to multiple organization
    ENTRIES_ALL = remove_extra_noun(ENTRIES_ALL)
    ENTRIES_ALL = remove_stopwords(ENTRIES_ALL)
    ENTRIES_ALL = sorted(ENTRIES_ALL, key=lambda x: x['confidence'], reverse=True)
    size = len(ENTRIES_ALL)
    write_pruning_result(size)


def bottling(entries_all):
    """
    only keep those entries with confidence higher than CONFIDENCE_THRESHOLD
    :param entries_all:
    :return:
    """
    global CONFIDENCE_THRESHOLD
    valid_dict = []
    for i in range(len(ENTRIES_ALL)):
        e = ENTRIES_ALL[i]
        if e['confidence'] >= CONFIDENCE_THRESHOLD:
            valid_dict.append(e)
    return valid_dict


def conform_entity_value(entries_all):
    """
    this function is for conformity of entity-value type
    e.g. in @param entries_all we have two entries - 'bill(people), microsoft(organization)'
    and 'microsoft(organization), bill(people)'
    then we only keep one of them
    :param entries_all:
    :return:
    """
    global ENTRIES_ALL
    distinct_list = []
    distinct_relations = []
    for entry in ENTRIES_ALL:
        relation = (entry['entityValue0'], entry['entityValue1'])
        relation_reverse = (entry['entityValue1'], entry['entityValue0'])
        if relation not in distinct_relations and relation_reverse not in distinct_relations:
            distinct_relations.append(relation)
            distinct_list.append(entry)
        elif relation in distinct_relations:
            pos = distinct_relations.index(relation)
            distinct_list[pos]['confidence'] = max(distinct_list[pos]['confidence'], entry['confidence'])
        else:
            pos = distinct_relations.index(relation_reverse)
            distinct_list[pos]['confidence'] = max(distinct_list[pos]['confidence'], entry['confidence'])
    return distinct_list


def remove_extra_noun(entries_all):
    """
    Remove duplicate entries which have the same 'entityValue1', in Work_For, it's 'PEOPLE'
    only keep the highest confidence one
    e.g. if the scheme is 'bill(people), google, 0.1', 'bill(people), microsoft, 0.2'
    then we only keep 'bill(people), microsoft, 0.2'
    :param entries_all: a list of entries
    :return: de-duped entries_All
    """
    getx, gety = lambda a: a['entityValue1'], lambda a: a['confidence']  # or use operator.itemgetter
    groups = itertools.groupby(sorted(entries_all, key=getx), key=getx)
    m = [max(b, key=gety) for a, b in groups]
    copy = [l for l in entries_all if l in m]
    return copy


def get_stopwords():
    # TODO this part needs improvement, can read from a file
    global STOPWORDS
    stopwords = []
    stopwords.append('corporation')
    stopwords.append('school')
    stopwords.append('laboratory')
    stopwords.append('institute')
    stopwords.append('committee')
    stopwords.append('university')
    stopwords.append('ipo')
    stopwords.append('center')
    STOPWORDS = stopwords


def remove_stopwords(entries_all):
    """
    remove entries from @param entries_all which has entityValue in stopwords
    e.g. people = Laboratory, ORGANIZATION = school
    :param entries_all:
    :return:
    """
    global STOPWORDS
    get_stopwords()
    valid_entries = []
    for entry in entries_all:
        entityValue0 = str(entry['entityValue0']).lower()
        entityValue1 = str(entry['entityValue1']).lower()
        if entityValue0 not in STOPWORDS and entityValue1 not in STOPWORDS:
            valid_entries.append(entry)
    return valid_entries


def gen_new_query(entries_all):
    """
    generate new queries from entries_all
    :param entries_all:
    :return:
    """
    global QUERY_SET, QUERY
    for i in range(len(entries_all)):
        if not old_query(entries_all[i]):
            QUERY = str(entries_all[i]['entityValue1']).lower() + ' ' + str(entries_all[i]['entityValue0']).lower()
            return True
    return False


def old_query(query):
    global QUERY_SET
    for item in QUERY_SET:
        words = item.split(' ')
        if str(query['entityValue1']).lower() in words:
            return True
    return False


if __name__ == '__main__':
    index()