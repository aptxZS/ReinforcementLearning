# %matplotlib inline
# import matplotlib as plt
import numpy as np
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans


# hypothesis computes $h_theta$
def hypothesis(theta, X):
    # YOUR CODE HERE
    return X @ theta


# grad_cost_func computes the gradient of J for linear regression given J is the MSE 
def grad_cost_func(theta, X, y):
    M = X.shape[0]
    return (1/M) * X.T @ (hypothesis(theta, X) - y)


# cost_func computes the cost function J
def cost_func(theta, X, y):
    M = X.shape[0]
    return (1/(2 * M)) * (hypothesis(theta, X) - y).T @ (hypothesis(theta, X) - y)


rawData = np.genfromtxt('/home/dimitri/Documents/SOTON/ReinforcementLearning/Assignments/Lab2/housing.data')
N, pp1 = rawData.shape
# Last column is target
X = np.matrix(rawData[:, 0:pp1-1])
y = np.matrix(rawData[:, pp1-1]).T
print(X.shape, y.shape)
# Solve linear regression, plot target and prediction
w = (np.linalg.inv(X.T*X)) * X.T * y
yh_lin = X*w
plt.plot(y, yh_lin, '.', Color='magenta')
# J = 20basis functions obtained by k-means clustering
# sigma set to standard deviation of entire data
J = 20
kmeans = KMeans(n_clusters=J, random_state=0).fit(X)
sig = np.std(X)
# Construct design matrix
U = np.zeros((N, J))
for i in range(N):
    for j in range(J):
        U[i][j] = np.linalg.norm(X[i] - kmeans.cluster_centers_[j])

# Solve RBF model, predict and plot via Moore-Penrose inverse
w = np.dot((np.linalg.inv(np.dot(U.T, U))), U.T) * y
yh_rbf = np.dot(U, w)
plt.plot(y, yh_rbf, '.', Color='cyan')
plt.show()
print(np.linalg.norm(y-yh_lin), np.linalg.norm(y-yh_rbf))

# Solve via stochastic gradient descent
N = 1000
alpha = 0.0000001
w = np.random.randn(U.shape[1], 1)
loss_vec = []
weight_vec = []
for i in range(N):
    p = np.random.randint(0, U.shape[0])
    w = w - alpha * grad_cost_func(w, U[p:p+1], y[p:p+1])
    loss_vec.append(cost_func(w, U[p:p+1], y[p:p+1]))
    weight_vec.append(w[0])

print(w)
plt.scatter(weight_vec, loss_vec)
plt.show()
