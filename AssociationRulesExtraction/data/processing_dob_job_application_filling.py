# coding: utf-8
import csv

file = open("DOB_Job_Application_Filings.csv", "r", encoding='utf-8')
csv_reader = csv.reader(file)

area = dict()
job_type = dict()
job_status = dict()
landmarked = dict() # column 15
estimate_fee = list()

line = 0
area_id = 0
job_type_id = 0
job_status_id = 0

for row in csv_reader:
    if line == 0:
        line += 1
        continue
    try:
        # estimate fee
        fee = int(float(row[47][1:].replace(',', '')))
        estimate_fee.append(fee)
    except Exception as e:
        print(e)
        continue
    # area id => manhattan, brooklyn...
    if (row[2] not in area):
        area[row[2]] = area_id
        area_id += 1
    # job type
    if (row[8] not in job_type):
        job_type[row[8]] = job_type_id
        job_type_id += 1
    # job status
    if (row[9] not in job_status):
        job_status[row[9]] = job_status_id
        job_status_id += 1


    line += 1
    if (line == 2000):
        break

estimate_fee.sort()

print("Total number of areas: {}".format(len(area)))
print("Total number of job_type: {}".format(len(job_type)))
print("Total number of job_type: {}".format(len(job_status)))

print("Statistics about the active level:")
length = len(estimate_fee)

print("Very active: {}".format(estimate_fee[int(length * 0.9)]))
print("Active: {}".format(estimate_fee[int(length * 0.8)]))
print("Normal: {}".format(estimate_fee[int(length * 0.7)]))
print("Inactive: {}".format(estimate_fee[int(length * 0.6)]))
print("Very inactive: {}".format(estimate_fee[int(length * 0.5)]))

th1 = estimate_fee[int(length * 0.5)]
th2 = estimate_fee[int(length * 0.6)]
th3 = estimate_fee[int(length * 0.7)]
th4 = estimate_fee[int(length * 0.8)]
th5 = estimate_fee[int(length * 0.9)]

file = open("DOB_Job_Application_Filings.csv", "r", encoding='utf-8')
csv_reader = csv.reader(file)

out = open("INTEGRATED-DATASET.csv", 'w')
csv_writer = csv.writer(out, dialect='excel')

result = list()
line = 0
for row in csv_reader:
    curr = list()
    if (line == 0):
        line += 1
        continue
    try:
        aid = area[row[2]]
        jtid = job_type[row[8]]
        jsid = job_status[row[9]]
    except Exception as e:
        print(e)
        continue
    landmarked = "N"
    if (row[15] == "Y"):
        landmarked = "Y"

    for x in range(0, len(area)):
        if (x == aid):
            curr.append("Y")
        else:
            curr.append("N")
    for x in range(0, len(job_type)):
        if (x == jtid):
            curr.append("Y")
        else:
            curr.append("N")
    for x in range(0, len(job_status)):
        if (x == jsid):
            curr.append("Y")
        else:
            curr.append("N")
    curr.append(landmarked)
    active_rate = 0
    try:
        active_rate = int(float(row[47][1:].replace(',', '')))
    except Exception:
        pass
    for x in range(0, 5):
        curr.append("N")

    if (active_rate < th1):
        curr[len(curr) - 5] = "Y"
    elif (active_rate < th2):
        curr[len(curr) - 4] = "Y"
    elif (active_rate < th3):
        curr[len(curr) - 3] = "Y"
    elif (active_rate < th4):
        curr[len(curr) - 2] = "Y"
    else:
        curr[len(curr) - 1] = "Y"
    line += 1
    result.append(curr)
    if (line == 2000):
        break

id_area = dict()
for k, v in area.items():
    id_area[v] = k
id_job_type = dict()
for k, v in job_type.items():
    id_job_type[v] = k
id_job_status = dict()
for k, v in job_status.items():
    id_job_status[v] = k

head = list()
for x in range(0, len(area)):
    head.append(id_area[x])

for x in range(0, len(job_type)):
    head.append(id_job_type[x])

for x in range(0, len(job_status)):
    head.append(id_job_status[x])

head.append("Landmark")
head.append("Very Cheap")
head.append("Cheap")
head.append("Normal")
head.append("Expensive")
head.append("Very expensive")


out = open("INTEGRATED-DATASET.csv", 'w')
csv_writer = csv.writer(out, dialect='excel')
csv_writer.writerow(head)
cnt = 0
for ele in result:
    csv_writer.writerow(ele)
    cnt += 1
    if (cnt == 2000):
        break
out.close()
print("final")