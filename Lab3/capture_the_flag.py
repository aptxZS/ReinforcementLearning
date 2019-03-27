import numpy as np
import random


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
    # every position guessed correctly)
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
        self.flags.put_flags(positions)


class Seeker:

    def __init__(self, flags):
        self.flags = flags
        self.guessed = 0

    def seekFlags(self):
        positions = random.sample(range(self.flags.places_no), self.flags.flags_no)
        print("try positions: {}".format(positions))
        self.guessed += self.flags.check_flags(positions)


flags_places = 5
flags_no = 2
flags = Flags(flags_places, flags_no)
hider = Hider(flags)
seeker = Seeker(flags)
for i in range(10):
    hider.hideFlags()
    print(flags)
    seeker.seekFlags()
    print(seeker.guessed)
    print()
print(seeker.guessed)
