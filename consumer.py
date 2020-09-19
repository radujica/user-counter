import json
from collections import Counter

from kafka import KafkaConsumer


def get_consumer(url: str) -> KafkaConsumer:
    return KafkaConsumer(
        'feed',
        group_id='counter',
        bootstrap_servers=url,
        auto_offset_reset='earliest',
        enable_auto_commit=True
    )


if __name__ == '__main__':
    consumer = get_consumer('localhost:9092')
    counter = Counter()
    for message in consumer:
        data = json.loads(message.value.decode('utf-8'))
        print(data)

        counter[data['uid']] += 1
        print(counter)
