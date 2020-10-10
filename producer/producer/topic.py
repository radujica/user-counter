from confluent_kafka.admin import AdminClient, NewTopic


def topic_exists(client: AdminClient, name: str) -> bool:
    return name in client.list_topics().topics


def create_topic(
    client: AdminClient,
    name: str,
    num_partitions: int = 1,
    num_replicas: int = 1
):
    return client.create_topics([NewTopic(name, num_partitions, num_replicas)])
