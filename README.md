# What is this?
A nice example on how to count and display unique users through streaming.

# Requirements
    docker
    docker-compose

# Setup
    # get containers running
    docker-compose up -d

# Dev notes
    # local env for producer
    conda create --name user-counter_producer python=3.8
    source activate user-counter_producer
    pip install -e .
    
    # local env for counter
    ...
    
    # create the topic
    docker exec -t user-counter_kafka-server_1 /opt/bitnami/kafka/bin/kafka-topics.sh --create --if-not-exists --replication-factor 1 --partitions 1 --bootstrap-server :9092 --topic feed
