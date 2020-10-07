# What is this?
A nice example on how to count and display unique users through streaming.

# Requirements
    docker
    docker-compose

# Setup
    # get containers running
    docker-compose up -d
    # create the topic
    docker exec -t user-counter_kafka-server_1 /opt/bitnami/kafka/bin/kafka-topics.sh --create --replication-factor 1 --partitions 1 --bootstrap-server :9092 --topic feed
