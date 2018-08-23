from sklearn.cluster import KMeans
import numpy as np
X = np.array([[132, 192], [117, 960], [117, 962], [1343, 0], [117, 1109], [117, 1110], [117, 1111], [117, 1116], [117, 1117], [117, 1118], [117, 1119], [1015, 0], [117, 966]])
kmeans = KMeans(n_clusters=5, random_state=0).fit(X)
print(kmeans.labels_)
print(kmeans.labels_.tolist().count(3))
