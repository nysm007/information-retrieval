from index import *
from utils import *
entries = []


def strip_end(text, suffix):
    if not text.startswith(suffix):
        return text
    print(len(text)-len(suffix))
    return text[len(text)-len(suffix):]


with open("trans.txt", "r") as infile:
    for line in infile:
        if line.startswith('RelationType'):
            entry = {}
            words = line.split(' | ')
            # print(words)
            relation = words[0][14:]
            confidence = float(words[1][11:])
            entry['relation'] = relation
            entry['confidence'] = confidence
            # print(relation)
            # print(confidence)
            EntityType0 = words[2].strip()[13:]
            EntityValue0 = words[3].strip()[14:]
            EntityType1 = words[4].strip()[13:]
            EntityValue1 = words[5].strip()[14:]
            entry['entityType0'] = EntityType0
            entry['entityValue0'] = EntityValue0
            entry['entityType1'] = EntityType1
            entry['entityValue1'] = EntityValue1
            entries.append(entry)
set_entries_all(entries)
set_cnt(1)
add_query_to_queryset('bill gates microsoft')
set_confidence(0.35)
set_num_of_tuples(10)
set_relation('Work_For')
set_search_key('AIzaSyClVW-iN4ZPlOuWBPoZ_wPYCpGSCWT1LmI')
set_engine('009351493534667843800:hf-txjxwl2y')
after_process()

