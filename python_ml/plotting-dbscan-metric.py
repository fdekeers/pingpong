from sklearn.cluster import DBSCAN
from sklearn import metrics
import sys
import math
import matplotlib.cm as cm
import numpy as np
import matplotlib.pyplot as plt

# metric function for clustering
def metric(x, y):
	# Compare 2 datapoints in array element 2 and 3 that contains C or S
	if x[2] != y[2] or x[3] != y[3]:
		# We are not going to cluster these together since they have different directions
		return sys.maxsize;
	else:
		# Compute Euclidian distance here
		return math.sqrt((x[0] - y[0])**2 + (x[1] - y[1])**2)

# Create a subplot with 1 row and 2 columns
fig, (ax2) = plt.subplots(1, 1)
fig.set_size_inches(20, 20)


# Read from file
# TODO: Just change the following path and filename 
# 	when needed to read from a different file
path = "/scratch/July-2018/Pairs3/"
device = "kwikset-off-phone-side"
filename = device + ".txt"
plt.ylim(0, 2000)
plt.xlim(0, 2000)

# Number of triggers
trig = 50

# Read and create an array of pairs
with open(path + filename, "r") as pairs:
	pairsArr = []
	pairsSrcLabels = []
	for line in pairs:
		# We will see a pair and we need to split it into xpoint and ypoint
		xpoint, ypoint, srcHost1, srcHost2, src1, src2 = line.split(", ")
		# Assign 1000 for client and 0 for server to create distance
		src1Val = 1000 if src1 == 'C' else 0
		src2Val = 1000 if src2 == 'C' else 0
		pair = [int(xpoint), int(ypoint), int(src1Val), int(src2Val)]
		pairSrc = [int(xpoint), int(ypoint), srcHost1, srcHost2, src1, src2]
		# Array of actual points
		pairsArr.append(pair)
		# Array of source labels
		pairsSrcLabels.append(pairSrc)

# Formed array of pairs		
#print(pairsArr)
X = np.array(pairsArr);

# Compute DBSCAN
# eps = distances
# min_samples = minimum number of members of a cluster
#db = DBSCAN(eps=20, min_samples=trig - 5).fit(X)
# TODO: This is just for seeing more clusters
db = DBSCAN(eps=20, min_samples=trig - 45, metric=metric).fit(X)
core_samples_mask = np.zeros_like(db.labels_, dtype=bool)
core_samples_mask[db.core_sample_indices_] = True
labels = db.labels_

# Number of clusters in labels, ignoring noise if present.
n_clusters_ = len(set(labels)) - (1 if -1 in labels else 0)

#print('Estimated number of clusters: %d' % n_clusters_)

import matplotlib.pyplot as plt

# Black removed and is used for noise instead.
unique_labels = set(labels)
#print("Labels: " + str(labels))

colors = [plt.cm.Spectral(each)
          for each in np.linspace(0, 1, len(unique_labels))]
for k, col in zip(unique_labels, colors):
    cluster_col = [1, 0, 0, 1]
    if k == -1:
        # Black used for noise.
        col = [0, 0, 0, 1]

    class_member_mask = (labels == k)

    xy = X[class_member_mask & core_samples_mask]
    plt.plot(xy[:, 0], xy[:, 1], 'o', markerfacecolor=tuple(cluster_col),
             markeredgecolor='k', markersize=10)

    xy = X[class_member_mask & ~core_samples_mask]
    plt.plot(xy[:, 0], xy[:, 1], 'o', markerfacecolor=tuple(col),
             markeredgecolor='k', markersize=6)

# Print lengths
count = 0
for pair in pairsArr:
	if labels[count] == -1:
		plt.text(pair[0], pair[1], str(pair[0]) + ", " + str(pair[1]), fontsize=10)
	else:
	# Only print the frequency when this is a real cluster
		plt.text(pair[0], pair[1], str(pair[0]) + ", " + str(pair[1]) + 
			" f: " + str(labels.tolist().count(labels[count])), fontsize=10)
	count = count + 1

# Print source-destination labels
count = 0
for pair in pairsSrcLabels:
	# Only print the frequency when this is a real cluster
	plt.text(pair[0], pair[1], str(pair[4]) + "->" + str(pair[5]))
	count = count + 1
	
plt.title(device + ' - Clusters: %d' % n_clusters_)
plt.show()

