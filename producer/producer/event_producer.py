from typing import Iterator


def produce_events() -> Iterator[int]:
    events = [1, 2, 3]

    for e in events:
        yield e
