from sklearn.cluster import KMeans
import matplotlib.cm as cm
import numpy as np
import matplotlib.pyplot as plt

# Create a subplot with 1 row and 2 columns
fig, (ax2) = plt.subplots(1, 1)
fig.set_size_inches(7, 7)


# Read from file
# TODO: Just change the following path and filename 
# 	when needed to read from a different file
path = "/scratch/July-2018/Pairs/"
filename = "alexa-off.txt"

# Read and create an array of pairs
with open(path + filename, "r") as pairs:
	pairsArr = []
	for line in pairs:
		# We will see a pair and we need to split it into xpoint and ypoint
		xpoint, ypoint = line.split(", ")
		pair = [int(xpoint), int(ypoint)]
		pairsArr.append(pair)

# Formed array of pairs		
#print(pairsArr)
X = np.array(pairsArr);

clusters = 25

# Plot the data points based on the clusters
clusterer = KMeans(n_clusters=clusters, random_state=10)
cluster_labels = clusterer.fit_predict(X)
# 2nd Plot showing the actual clusters formed
colors = cm.nipy_spectral(cluster_labels.astype(float) / clusters)
ax2.scatter(X[:, 0], X[:, 1], marker='o', s=100, lw=0, alpha=0.3,
            c=colors, edgecolor='k')

# Labeling the clusters
centers = clusterer.cluster_centers_
# Label with cluster centers and frequencies
for i, c in enumerate(centers):
	mark = '[' + str(int(c[0])) + ', ' + str(int(c[1])) + ']' + ', ' + str(clusterer.labels_.tolist().count(i))
	ax2.scatter(c[0], c[1], marker='$%s$' % mark, alpha=1, s=3000, edgecolor='k')
	print('[' + str(int(c[0])) + ', ' + str(int(c[1])) + ']' + ', ' + str(clusterer.labels_.tolist().count(i)))

ax2.set_title("The visualization of the clustered data.")
ax2.set_xlabel("Feature space for the 1st feature")
ax2.set_ylabel("Feature space for the 2nd feature")
plt.show()
