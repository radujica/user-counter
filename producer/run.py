import json
import logging
from typing import Any, Dict

from confluent_kafka import Producer
from confluent_kafka.admin import AdminClient

from producer.constants import BUCKET, TIMESTAMP
from producer.events import bucket_timestamp, produce_data
from producer.topic import create_topic, topic_exists
from producer.utils import random_sleep_between


TOPIC = 'feed'
CONFIG = {'bootstrap.servers': ':9092'}


logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def create_topic_if_not_exists(client: AdminClient, topic: str) -> None:
    if not topic_exists(client, topic):
        create_topic(client, topic)
        logger.info('Created topic {}'.format(topic))
    else:
        logger.info(
            'Topic {} already exists. Using this existing topic'.format(topic)
        )


def process_event(data: Dict[str, Any]) -> Dict[str, Any]:
    bucket = bucket_timestamp(data[TIMESTAMP])
    data.update({BUCKET: bucket})

    return data


def report_delivery(error: str, message: str) -> None:
    if error is not None:
        logger.info('Message delivery failed: {}'.format(error))
    else:
        logger.info(
            'Message delivered to {} [{}]'.format(
                message.topic(),
                message.partition()
            )
        )


def run() -> None:
    client = AdminClient(CONFIG)
    create_topic_if_not_exists(client, TOPIC)

    producer = Producer(CONFIG)

    for event in produce_data():
        producer.poll(0)
        event = process_event(event)
        producer.produce(
            TOPIC,
            value=json.dumps(event).encode('utf-8'),
            key=event[BUCKET],
            callback=report_delivery
        )
        random_sleep_between(0.0, 3.0)

    producer.flush()


if __name__ == '__main__':
    run()
