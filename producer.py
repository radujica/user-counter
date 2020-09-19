import json

from kafka import KafkaProducer


def get_producer(url: str) -> KafkaProducer:
    return KafkaProducer(
        bootstrap_servers=url
    )


def read_line(path: str) -> str:
    with open(path, 'r', encoding='utf-8') as f:
        for line in f:
            yield line


if __name__ == '__main__':
    producer = get_producer('localhost:9092')

    filepath = 'data/example.jsonl'
    for entry in read_line(filepath):
        print(entry)
        data = json.loads(entry)
        producer.send(
            topic='feed',
            value=entry.encode('utf-8'),
            key=data['uid'].encode('utf-8')
        ).get(timeout=5)
