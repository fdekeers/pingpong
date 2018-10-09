from datetime import datetime


path = "/scratch/July-2018/evaluation/"
device = "dlink"
fileExperiment = "dlink-plug-8hr-data-oct-8-2018.timestamps"
fileDetection = "dlink-plug.detection.timestamps"
TIME_WINDOW = 15 # detection/signature window of 15 seconds
#NEG_TIME_WINDOW = -15 # detection/signature window of 15 seconds

# Open training timestamps file and store into a list
with open(path + device + "/" + fileExperiment, "r") as experiment:
	tsExperimentList = []
	for line in experiment:
		# Format "%m/%d/%Y %I:%M:%S %p"
		tsE = datetime.strptime(line, "%m/%d/%Y %I:%M:%S %p\n")
		tsExperimentList.append(tsE)

# Open detection timestamps file and store into a list
with open(path + device + "/" + fileDetection, "r") as detection:
	tsDetectionList = []
	for line in detection:
		# Format "%b %d, %Y %I:%M:%S %p"
		tsD = datetime.strptime(line, "%b %d, %Y %I:%M:%S %p\n")
		tsDetectionList.append(tsD)
		
if (len(tsExperimentList) > len(tsDetectionList)):
	maxTimestamps = len(tsExperimentList)
else:
	maxTimestamps = len(tsDetectionList)

i = 0
j = 0
while i < maxTimestamps:
	tsE = tsExperimentList[i]
	tsD = tsDetectionList[j]
	# Detection is always a bit later than training trigger
	delta = tsD - tsE
	# The following happens when we could detect less triggers than the experiment
	if (delta.seconds > TIME_WINDOW):
		print("Missing trigger at line: " + str(i) + ", t_experiment: " + str(tsE) + " and t_detection: " + str(tsD))
		i = i + 1
	# The following should not happen (we have more detected triggers than the experiment)
	#elif (delta.seconds < NEG_TIME_WINDOW):
	#	print("Mismatch at t_experiment: " + str(tsE) + " and t_detection: " + str(tsD))
	#	j = j + 1
	i = i + 1
	j = j + 1

print("Done parsing: " + str(i) + " lines")
