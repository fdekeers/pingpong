from sklearn.cluster import KMeans
import matplotlib.cm as cm
import numpy as np
import matplotlib.pyplot as plt

# Create a subplot with 1 row and 2 columns
fig, (ax2) = plt.subplots(1, 1)
fig.set_size_inches(7, 7)

X = np.array([[132, 192], [117, 960], [117, 962], [1343, 0], [117, 1109], [117, 1110], [117, 1111], [117, 1116], [117, 1117], [117, 1118], [117, 1119], [1015, 0], [117, 966]])
#kmeans = KMeans(n_clusters=5, random_state=0).fit(X)
#print(kmeans.labels_)
#print(kmeans.labels_.tolist().count(3))
clusters = 5

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

# Draw white circles at cluster centers
#ax2.scatter(centers[:, 0], centers[:, 1], marker='o',
#            c="white", alpha=1, s=200, edgecolor='k')

#for i, c in enumerate(centers):
#    ax2.scatter(c[0], c[1], marker='$%d$' % i, alpha=1,
#                s=50, edgecolor='k')
#for i, c in enumerate(centers):
#	print(c[0], c[1])

ax2.set_title("The visualization of the clustered data.")
ax2.set_xlabel("Feature space for the 1st feature")
ax2.set_ylabel("Feature space for the 2nd feature")
plt.show()

