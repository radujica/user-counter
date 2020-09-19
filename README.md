# Install & Notes
- This was developed on Windows, however should 'just work' on other environments due to the conda environment.
No Linux machine available atm to develop on
- Kafka version is 2.11-2.4.0 with data stored in `data/kafka` & `data/zookeeper`
    
    
    conda create --name doodle python=3.8
    source activate doodle
    conda install -c conda-forge kafka-python
    
    
# Setup

    # this is for default local on windows in CMD (from /bin/windows if not on path)

    # start zookeeper & kafka
    zookeeper-server-start.bat kafka-config/zookeeper.properties
    kafka-server-start.bat kafka-config/server.properties
    
    # create the topic 'feed'
    kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic feed
    

# Run

    source activate doodle
    python producer.py
    python consumer.py
    
    
# Goal
Return number of unique users per X time, where `X = {minute, hour, day}`


# Choices

# Next steps

- Docker-ize (local setup was of course just faster for a quick project)
- Use some config file for topic name, input file, etc.
