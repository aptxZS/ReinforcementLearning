import random
import sys
import numpy as np
import matplotlib.pyplot as plt

graph = {0: {1: 2, 4: 4},
         1: {2: 3},
         2: {3: 5, 4: 1},
         3: {0: 8},
         4: {3: 3}}


def constructGraph(nodes, probability):
  result = np.zeros((nodes, nodes))
  result_dict = {}
  for i in range(nodes):
    partial_dict = {}
    for j in range(nodes):
      if j == i:
        result[i][j] = 0
      elif j < i:
        result[i][j] = result[j][i]
        if result[j][i] > 0:
          partial_dict[j] = result[j][i]
      else:
        if random.random() <= probability:
          partial_dict[j] = 1
          result[i][j] = 1
        else:
          result[i][j] = 0
      result_dict[i] = partial_dict
  return result, result_dict


def allPairsShortestPath(g):
  nodes = len(g)
  dist = np.zeros((nodes, nodes))
  pred = np.zeros((nodes, nodes))
  for u in g:
    for v in g:
      dist[u][v] = sys.maxsize
      pred[u][v] = None
    dist[u][u] = 0
    pred[u][u] = None
    for v in g[u]:
      dist[u][v] = g[u][v]
      pred[u][v] = u
  for mid in g:
    for u in g:
      for v in g:
        newlen = dist[u][mid] + dist[mid][v]
        if newlen < dist[u][v]:
          dist[u][v] = newlen
          pred[u][v] = pred[mid][v]
  return(dist, pred)


def constructShortestPath(s, t, pred):
  path = [t]
  while t != s:
    try:
      tmp = pred[s][t]
      t = tmp.astype(int)
    except IndexError:
      return None
    if t is None:
      return None
    path.insert(0,t)
  return path


def constructFrequencyDict(dist):
  result = {}
  for i in range(len(dist)):
    for j in range(len(dist)):
      if i != j and j > i:
       key = dist[i][j]
       if key in result:
         result[key] += 1
       else:
         result[key] = 1
  labels, values = [], []
  for key in sorted(result):
    labels.append(int(key))
    values.append(result[key])
  return labels, values


def plotResults(labels, values, nodes, prob):
  X = np.arange(len(values))
  plt.bar(X, values, width=0.5)
  plt.xticks(X, labels)
  ymax = max(values) + 1
  plt.ylim(0, ymax)
  plt.suptitle("Path length frequencies ({}, {})".format(nodes, prob), fontsize=16)
  plt.ylabel('Frequency')
  plt.xlabel('Path lengths')
  plt.show()


random.seed(1)
nodes = 100
probabilities = [0.02, 0.05, 0.08, 0.1, 0.2, 0.3, 0.5, 0.7, 1]
for prob in probabilities:
  print(prob)
  graph2, graph2_dict = constructGraph(nodes, prob)
  dist, pred = allPairsShortestPath(graph2_dict)
  labels, values = constructFrequencyDict(dist)
  plotResults(labels, values, nodes, prob)
