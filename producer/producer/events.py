import random
import time
from datetime import datetime
from typing import Any, Dict, Iterator

from .constants import TIMESTAMP, UID

NUMBER_UIDS = 100
UIDS = ['uid{}'.format(i) for i in range(NUMBER_UIDS)]


def get_uid() -> str:
    return UIDS[random.randint(0, NUMBER_UIDS - 1)]


def produce_data() -> Iterator[Dict[str, Any]]:
    while True:
        data = {
            TIMESTAMP: int(time.time()),
            UID: get_uid()
        }

        yield data


# per 60 seconds; could create different topics for each granularity
def bucket_timestamp(timestamp: int) -> int:
    dt = datetime.fromtimestamp(timestamp)
    bucket = datetime(dt.year, dt.month, dt.day, dt.hour, dt.minute)

    return int(bucket.timestamp())
