# Install & Notes
- This was developed on Windows, however should 'just work' on other environments due to the conda environment.
No Linux machine available atm to develop on.
- Kafka version is 2.11-2.4.0 with data stored in `data/kafka` & `data/zookeeper`



    conda create --name doodle python=3.8
    source activate doodle
    conda install -c conda-forge kafka-python
    
    
# Setup

    # these are for default local on windows in CMD (from /bin/windows if not on path)

    # start zookeeper & kafka
    zookeeper-server-start.bat kafka-config/zookeeper.properties
    kafka-server-start.bat kafka-config/server.properties
    
    # create the topic 'feed'
    kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic feed
    

# Run

    source activate doodle
    # produce raw data, 2 options (first tested)
    python producer.py  # 1
    gzcat stream.gz | kafka-console-producer --broker-list localhost:9092 --topic feed  # 2
    # first consumer
    python bucketer.py


# Goal
Compute number of unique users per minute.    


# Thoughts along the way
A first idea is for the consumer to continuously process the messages and output the counts whenever a new message
passes the X time limit (e.g. 60 seconds have passed since we processed the first message, thus the current 'window' 
ends and we need to output the counts). However, this means that there will be no output for given timeframes/windows 
if no message was received within them, thus one cannot distinguish whether something failed 
or there was just no data to count.

A second idea is to read every X seconds everything we haven't read yet, though this assumes we can process everything
before the next X seconds pass (which would then trigger another computation).

In batch-processing terms, one would bucket the data per timeframe, e.g. all data between 00:00 and 00:01, then 
essentially run a countDistinct on uid.

For a dataset/view with continuous appends, one would then process only the appends (here could be new messages in a topic).
However, if messages might arrive for timestamps that have already been processed once, one would need to reprocess
everything (or choose some retention period and either assume it's not possible for a message to arrive with a timestamp
longer than 1 day in the past or even drop messages arriving 1+ day late if less accuracy is acceptable).


# Chosen idea
Target is to bucket the messages in 60 seconds groups, i.e. put all messages with ts within a minute in the same bucket,
 and then count unique users per bucket. The assumed use case is something like a dashboard which shows the number of unique
 users per minute, hence we need new output every minute.
 
 Therefore it's a 2-step process:
 
 1. Take all incoming messages, bucket them (e.g. ignore the second in the timestamp), then store in a Kafka topic with key=bucket.
 2. A consumer needs to keep a global set of (unique) uids per bucket. Every minute, take all the new bucketed messages
 and union to the per-bucket sets. Finally, output the counts, i.e. the length of the sets per bucket.


Pros
- Can easily add different bucketing for other intervals than 60 seconds (e.g. another consumer in-between bucketer.py
and counter.py which adds another bucket column; then at the end there could be 2 consumers, one for each bucket)
- Final consumer (counter.py) is able to output something (even if no new message) every set period of time (here 60s)
- Final consumer is able to read only new messages and update its counter
- Final consumer could instead serialize its latest counter (or e.g. send to a different topic) on every iteration then 
read it at the beginning of each subsequent iteration (to avoid holding data in memory and thus be more resilient)
- Due to partitioning per bucket, the consumer can be parallelized, i.e. process partitions in parallel, since
all data per bucket (due to having key=bucket) arrives to the same partition
- Even if messages arrive late, the counter would be updated
- Accuracy is expected to be very high, assuming 60s is enough to consume all the new messages and the timestamp in the message
(i.e. when it was produced) corresponds to the timestamp it arrives at the topic (i.e. when it was received); the current
solution is thus inaccurate (i.e. delayed by at least 60s) whenever data from a bucket arrives after the current bucket has passed 
(e.g. between 00:00 and 00:01, a message with timestamp within 00:00 & 00:01 arrives after upper bound of 00:01)


Cons
- The counter is continuously growing in keys and might take too much memory or time to de-/serialize. Here could have some
retention period or filter to avoid adding old messages to the bucketed topic, e.g. if something with ts older than 1 day arrives
- Updating the counter is assumed to take less than 60s


Extensions
- logging (or sending to some other topics) metrics such as the time taken to update counter or the number of (distinct)
users added each minute to the counter could be used for monitoring; the time taken on each step (serialization or processing)
could also be used to optimize
- increasing the number of partitions would allow scaling as mentioned above
- could run on the cloud, e.g. AWS MSK, for better availability and provisioning
- using a different json de-/serializer (like orjson) could be faster


# Next steps

- Docker-ize (local setup was of course just faster for a quick project)
- CI/CD (e.g. github actions)
- use some config file for topic name, input file, url, etc.
- tests, particularly unit (e.g. get_producer or bucket_timestamp) and integration 
(e.g. if all consumers still work as expected when some die)
- more checks & errors, e.g. for timezones and data format
- investigate Kstreams, Spark Streaming, or Python faust which should provide functionality for the above
- monitor Kafka itself (e.g. Confluent Control Center?)
- use a profiler to analyze performance, e.g. deduce if de-/serialization is an issue
