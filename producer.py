import logging
import time

from kafka import KafkaProducer

# TODO: don't set globally
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def get_producer(url: str) -> KafkaProducer:
    return KafkaProducer(
        bootstrap_servers=url,
        value_serializer=lambda x: x.encode('utf-8')
    )


def read_line(path: str) -> str:
    with open(path, 'r', encoding='utf-8') as f:
        for line in f:
            yield line


# produce an entry every second
if __name__ == '__main__':
    producer = get_producer('localhost:9092')

    filepath = 'data/stream.jsonl'
    for entry in read_line(filepath):
        logger.info('Sent raw data: {}'.format(str(entry)))

        producer.send(
            topic='feed',
            value=entry
        ).get(timeout=5)

        time.sleep(1)
