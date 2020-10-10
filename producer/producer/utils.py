import random
import time


def random_sleep_between(low_bound: float, high_bound: float) -> None:
    time.sleep(random.uniform(low_bound, high_bound))
