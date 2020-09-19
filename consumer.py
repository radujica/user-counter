from kafka import KafkaConsumer
import json


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
    for message in consumer:
        # message value and key are raw bytes -- decode if necessary!
        # e.g., for unicode: `message.value.decode('utf-8')`
        print("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition,
                                             message.offset, message.key,
                                             message.value))
        print(json.loads(message.value.decode('utf-8')))
