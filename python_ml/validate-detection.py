from datetime import datetime

#path = "/scratch/July-2018/experimental_result/smarthome/"
path = "/scratch/July-2018/experimental_result/standalone/"
# D-Link plug
#device = "dlink-plug/self-test"
#fileExperiment = "dlink-plug-nov-7-2018.timestamps"
#fileDetection = "device-side-detection"
#fileDetection = "phone-side-detection"
#device = "dlink-plug/timestamps"
#fileExperiment = "dlink-plug-smarthome-nov-8-2018.timestamps"
#fileDetection = "dlink-plug-smarthome-nov-8-2018.phone.wlan1.detections"
#fileDetection = "dlink-plug-smarthome-nov-8-2018.phone.eth0.detections"
#fileDetection = "dlink-plug-smarthome-nov-8-2018.device.eth0.detections"

# TP-Link plug
#device = "tplink-plug/self-test"
#fileExperiment = "tplink-plug-nov-8-2018.timestamps"
#fileDetection = "device-side-detection"
#fileDetection = "phone-side-detection"
#device = "tplink-plug/timestamps"
#fileExperiment = "tplink-plug-smarthome-nov-9-2018.timestamps"
#fileDetection = "tplink-plug-smarthome-nov-9-2018.eth0.device.detections"
#fileDetection = "tplink-plug-smarthome-nov-9-2018.wlan1.phone.detections"

# D-Link siren
#device = "dlink-siren/self-test"
#fileExperiment = "dlink-siren-nov-9-2018.timestamps"
#fileDetection = "phone-side-detection"
device = "dlink-siren/timestamps"
fileExperiment = "dlink-siren-smarthome-nov-10-2018.timestamps"
fileDetection = "dlink-siren-smarthome-nov-10-2018.eth0.phone.detections"

# Kwikset door lock
device = "kwikset-doorlock/self-test"
fileExperiment = "kwikset-doorlock-nov-10-2018.timestamps"
fileDetection = "phone-side-detection"
#device = "kwikset-doorlock/timestamps"
#fileExperiment = "kwikset-doorlock-smarthome-nov-10-2018.timestamps"
#fileDetection = "kwikset-doorlock-smarthome-nov-10-2018.eth0.phone.detections"

# Arlo camera
#device = "arlo-camera/self-test"
#fileExperiment = "arlo-camera-aug-10-2018.timestamps"
#fileDetection = "detection-on-training-phone-side"
# Blossom sprinkler
#device = "blossom-sprinkler/self-test"
#fileExperiment = "blossom-sprinkler-aug-13-2018.timestamps"
#fileDetection = "detection-on-training-device-side"
# Nest thermostat
#device = "nest-thermostat/self-test"
#fileExperiment = "nest-aug-15-2018.timestamps"
#fileDetection = "detection-on-training-phone-side"
# Hue bulb
#device = "hue-bulb/self-test"
#fileExperiment = "hue-bulb-aug-7-2018.timestamps"
#fileDetection = "detection-on-training-phone-side"
# TPLink bulb
#device = "tplink-bulb/self-test"
#fileExperiment = "tplink-bulb-aug-3-2018.timestamps"
#fileDetection = "detection-on-training-phone-side"
# WeMo Insight Plug
#device = "wemo-insight-plug/self-test"
#fileExperiment = "wemo-insight-july-31-2018.timestamps"
#fileDetection = "detection-on-training-device-side"

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
	if(len(tsExperimentList) <= i or len(tsDetectionList) <= j):
		break;
	
	tsE = tsExperimentList[i]
	tsD = tsDetectionList[j]
	# Detection is always a bit later than training trigger
	delta1 = tsD - tsE
	delta2 = tsE - tsD
	#print("tsE: " + str(tsE) + " - tsD: " + str(tsD) + " - delta1: " + str(delta1.seconds) + " - delta2: " + str(delta2.seconds))
	# The following happens when we could detect less triggers than the experiment
	if (delta1.seconds > TIME_WINDOW and delta2.seconds > TIME_WINDOW):
		print("Missing trigger at line: " + str(i) + ", t_experiment: " + str(tsE) + " and t_detection: " + str(tsD))
		#print(str(tsD))
		i = i + 1
	# The following should not happen (we have more detected triggers than the experiment)
	#elif (delta.seconds < NEG_TIME_WINDOW):
	#	print("Mismatch at t_experiment: " + str(tsE) + " and t_detection: " + str(tsD))
	#	j = j + 1
	i = i + 1
	j = j + 1

print("Done parsing: " + str(i) + " lines")
