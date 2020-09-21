import time

from collections import Counter, defaultdict

import json
from kafka import KafkaConsumer, TopicPartition


def get_consumer(url: str) -> KafkaConsumer:
    return KafkaConsumer(
        group_id='counters',
        bootstrap_servers=url,
        auto_offset_reset='earliest',
        value_deserializer=lambda x: x.decode('utf-8')
    )


# goal here is to provide every minute an updated count of unique users per bucket
if __name__ == '__main__':
    consumer = get_consumer('localhost:9092')
    start_time = time.time()
    # TODO: this should be updated for more partitions
    partition = TopicPartition('buckets', 0)
    consumer.assign([partition])

    # instead of keeping in memory (risky) could serialize somewhere the updated list on every iteration
    # and deserialize the last update at the beginning of each iteration
    unique_users_per_bucket = defaultdict(set)
    counts = Counter()

    try:
        while True:
            # obtain the last offset value
            consumer.seek_to_end(partition)
            last_offset = consumer.position(partition)

            # loop until last offset, to ensure this stops
            consumer.seek_to_beginning(partition)
            num_messages = 0
            for message in consumer:
                data = json.loads(message.value)
                unique_users_per_bucket[data['bucket']] |= {data['uid']}
                counts[data['bucket']] = len(unique_users_per_bucket[data['bucket']])
                num_messages += 1
                if message.offset == last_offset - 1:
                    break

            # TODO: here should manually commit to not re-process data

            # this could instead be yet another kafka topic
            print(str(counts))
            # example metric, which could also be logged or sent to some topic or DB instead
            print('Processed {} messages'.format(str(num_messages)))

            # assuming the above finishes in 60 seconds
            # TODO: this could be logged for monitoring/metrics purposes
            time.sleep(60.0 - ((time.time() - start_time) % 60.0))
    except KeyboardInterrupt:
        pass
