import numpy as np
import random
import math


class Flags:
    EMPTY_PLACE = 0
    FLAG_PLACE = 1

    def __init__(self, places_no, flags_no):
        self.places_no = places_no
        self.flags_no = flags_no
        self.flags_array = np.full(places_no, self.EMPTY_PLACE)

    # Inserts flags on given positions
    def put_flags(self, flags_position):
        self.flags_array.fill(self.EMPTY_PLACE)
        self.flags_array.put(flags_position, self.FLAG_PLACE)

    # Given a set of positions returns the number of matches (1 for
    # every position rewards correctly)
    def check_flags(self, flags_position):
        result = 0
        for pos in flags_position:
            if self.flags_array[pos] == self.FLAG_PLACE:
                result += 1
        return result

    def __str__(self):
        return "{}".format(self.flags_array)


class Hider:

    def __init__(self, flags):
        self.flags = flags
        self.frequencies = np.zeros(flags.places_no)

    def hideFlags(self):
        positions = random.sample(range(self.flags.places_no), self.flags.flags_no)
        for pos in positions:
            self.frequencies[pos] += 1
        self.flags.put_flags(positions)


class Seeker:

    def compute_actions(self):
        x = np.arange(self.flags.places_no)
        # instead of 2 should be number of flags
        mesh = np.array(np.meshgrid(x, x)).T.reshape(-1, 2)
        mesh.sort(axis=1)
        mesh = np.unique(mesh, axis=0)
        return mesh

    def __init__(self, flags):
        self.flags = flags
        self.rewards = 0
        self.gamma = 0.05
        self.actions = self.compute_actions()
        self.weights = np.ones(len(self.actions))

    def categorical_distribution(self, probabilities):
        rand, cumulative = random.random(), 0
        for i, p in enumerate(probabilities):
            cumulative += p
            if cumulative > rand:
                return i, p
        return i, p

    # exp3 place selection
    def select_place(self):
        probabilities = (1 - self.gamma) * (self.weights / sum(self.weights)) + (self.gamma * 1.0 / self.flags.places_no)
        print(probabilities)
        return self.categorical_distribution(probabilities)

    # exp3 weight update
    def update_weight(self, action_index, prob, reward):
        estimated_reward = (reward / self.flags.flags_no) / prob
        self.weights[action_index] *= math.exp(estimated_reward * self.gamma / len(self.actions))

    def seekFlags(self):
        # positions = random.sample(range(self.flags.places_no), self.flags.flags_no)
        action_index, prob = self.select_place()
        print("try positions: {}".format(self.actions[action_index]))
        reward = self.flags.check_flags(self.actions[action_index])
        self.rewards += reward
        self.update_weight(action_index, prob, reward)
        print(action_index, prob)


flags_places = 5
flags_no = 2
flags = Flags(flags_places, flags_no)
hider = Hider(flags)
seeker = Seeker(flags)
for i in range(1000):
    hider.hideFlags()
    print(flags)
    seeker.seekFlags()
    print(seeker.rewards)
    print()
print(seeker.rewards)
print(hider.frequencies)
