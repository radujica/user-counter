import json
import logging
from datetime import datetime

from kafka import KafkaConsumer, KafkaProducer

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def get_consumer(url: str) -> KafkaConsumer:
    return KafkaConsumer(
        'feed',
        group_id='counters',
        bootstrap_servers=url,
        auto_offset_reset='earliest',
        enable_auto_commit=True,
        value_deserializer=lambda x: x.decode('utf-8')
    )


def get_producer(url: str) -> KafkaProducer:
    return KafkaProducer(
        bootstrap_servers=url,
        value_serializer=lambda x: json.dumps(x).encode('utf-8'),
        key_serializer=lambda x: x.encode('utf-8')
    )


# per 60 seconds; could create different topics for each granularity
def bucket_timestamp(timestamp: int) -> str:
    dt = datetime.fromtimestamp(timestamp)
    bucket = datetime(dt.year, dt.month, dt.day, dt.hour, dt.minute)

    return str(int(bucket.timestamp()))


# bucket the data; bucketing allows to read just relevant timeframes and also parallelization
if __name__ == '__main__':
    consumer = get_consumer('localhost:9092')
    producer = get_producer('localhost:9092')

    for message in consumer:
        raw_data = json.loads(message.value)
        # select only relevant data
        selected_data = {k: v for k, v in raw_data.items() if k in {'ts', 'uid'}}
        # add the bucket
        selected_data['bucket'] = bucket_timestamp(raw_data['ts'])
        producer.send(
            topic='buckets',
            key=selected_data['bucket'],
            value=selected_data
        ).get(timeout=5)

        logger.info('Bucketed data: {}'.format(str(selected_data)))
