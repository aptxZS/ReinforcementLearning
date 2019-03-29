import numpy as np
import random
import math
import matplotlib.pyplot as plt


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


# Given a flag object computes all the possible actions
def compute_actions(flags):
    x = np.arange(flags.places_no)
    # instead of 2 should be number of flags
    mesh = np.array(np.meshgrid(x, x)).T.reshape(-1, 2)
    mesh.sort(axis=1)
    mesh = np.unique(mesh, axis=0)
    duplicates = []
    for i in range(len(mesh)):
        if mesh[i][0] == mesh[i][1]:
            duplicates.append(i)
    return np.delete(mesh, duplicates, 0)


class Hider:

    def __init__(self, flags, hider_type):
        self.valid_types = ['random', 'e-greedy', 'fpl']
        if hider_type not in self.valid_types:
            raise ValueError("results: status must be one of {}".format(self.valid_types))
        self.hider_type = hider_type
        self.flags = flags
        self.actions = compute_actions(flags)
        self.frequencies = np.zeros(len(self.actions))
        self.epsilon = 0.1
        self.rewards = np.zeros(len(self.actions))
        self.action_index = 0
        self.fpl_lr = 35

    def select_action(self):
        if random.random() > self.epsilon:
            return np.argmax(self.rewards)
        return np.random.choice(self.actions.shape[0])

    # normalize and reverse the reward obtain by seeker
    def compute_hider_reward(self, reward):
        return abs(reward - self.flags.flags_no) / float(self.flags.flags_no)

    def hide_flags_epsilon_greedy(self):
        self.action_index = self.select_action()
        positions = self.actions[self.action_index]
        self.frequencies[self.action_index] += 1
        self.flags.put_flags(positions)

    def update_reward_epsilon_greedy(self, reward):
        reward = self.compute_hider_reward(reward)
        n = float(self.frequencies[self.action_index])
        prev_reward = self.rewards[self.action_index]
        self.rewards[self.action_index] = ((n - 1) / n) * prev_reward + (1 / n) * reward

    def hide_flags_fpl(self):
        exp_dist = np.random.exponential(self.fpl_lr, len(self.actions))
        # Add exponentially drawn noise
        new_rewards = self.rewards + exp_dist
        self.action_index = np.argmax(new_rewards)
        self.frequencies[self.action_index] += 1
        self.flags.put_flags(self.actions[self.action_index])

    def update_reward_fpl(self, reward):
        reward = self.compute_hider_reward(reward)
        self.rewards[self.action_index] += reward

    def hideFlagsRandomly(self):
        random_index = np.random.choice(self.actions.shape[0])
        positions = self.actions[random_index]
        # positions = [0, 1]
        self.frequencies[random_index] += 1
        self.flags.put_flags(positions)

    def hide_flags(self):
        if self.hider_type == self.valid_types[0]:
            return self.hideFlagsRandomly()
        if self.hider_type == self.valid_types[1]:
            return self.hide_flags_epsilon_greedy()
        return self.hide_flags_fpl()

    def update_reward(self, reward):
        if self.hider_type == self.valid_types[1]:
            return self.update_reward_epsilon_greedy(reward)
        if self.hider_type == self.valid_types[2]:
            return self.update_reward_fpl(reward)
        return


class Seeker:

    def __init__(self, flags):
        self.flags = flags
        self.rewards = 0
        self.gamma = 0.2
        self.actions = compute_actions(flags)
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
        return self.categorical_distribution(probabilities)

    # exp3 weight update
    def update_weight(self, action_index, prob, reward):
        estimated_reward = (reward / self.flags.flags_no) / prob
        self.weights[action_index] *= math.exp(estimated_reward * self.gamma / len(self.actions))

    def seekFlags(self):
        # positions = random.sample(range(self.flags.places_no), self.flags.flags_no)
        # self.rewards += self.flags.check_flags(positions)
        action_index, prob = self.select_place()
        reward = self.flags.check_flags(self.actions[action_index])
        self.rewards += reward # / self.flags.flags_no
        self.update_weight(action_index, prob, reward)
        return reward


strategies = {'e-greedy', 'fpl', 'random'}
rounds_no = 500
flags_places = 5
flags_no = 2
flags = Flags(flags_places, flags_no)
results = dict()
for s in strategies:
    cumulative_rewards = np.zeros(rounds_no)
    reward_history = np.zeros(rounds_no)
    for i in range(100):
        hider = Hider(flags, s)
        seeker = Seeker(flags)
        pred = 0
        for i in range(rounds_no):
            hider.hide_flags()
            reward = seeker.seekFlags()
            reward_history[i] += reward
            cumulative_rewards[i] += reward + pred
            pred += reward
            hider.update_reward(reward)
    cumulative_rewards /= 100
    reward_history /= 100
    print("Results for hider strategy: {}".format(s))
    print(seeker.rewards)
    print(hider.frequencies)
    results[s] = (cumulative_rewards, reward_history)

# Displaying graphs of seeker's regret ==> cumulative reward / rounds_no
for s in strategies:
    reward = results[s][0]
    x_axis = np.linspace(1, rounds_no, rounds_no)
    #   total_regret = np.cumsum(flags_no - reward)
    plt.plot(x_axis, reward / x_axis, label=s)
    plt.legend()
plt.show()

for s in strategies:
    regret = 2 - results[s][1]
    plt.plot(x_axis, np.cumsum(regret)/x_axis, label=s)
    plt.legend()
plt.show()
#   plt.plot(x_axis, (reward) / x_axis )
