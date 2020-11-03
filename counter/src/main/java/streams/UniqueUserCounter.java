package streams;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class UniqueUserCounter {

    private static final String inputTopic = "feed";
    private static final Logger logger = LoggerFactory.getLogger(UniqueUserCounter.class.getName());

    public static void main(final String[] args) {
        final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9093";
        final Properties streamsConfiguration = getStreamsConfiguration(bootstrapServers);

        final Jedis jedis = new Jedis("localhost", 6379);

        final StreamsBuilder builder = new StreamsBuilder();
        createWordCountStream(builder, jedis, logger);
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        jedis.close();
    }

    private static Properties getStreamsConfiguration(final String bootstrapServers) {
        final Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "unique-user-counter");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "unique-user-counter-client");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, EventSerde.class.getName());
        // flush every 10 seconds
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        // disable record caches
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        return streamsConfiguration;
    }

    private static void createWordCountStream(final StreamsBuilder builder, final Jedis jedis, Logger logger) {
        final KStream<String, Event> events = builder.stream(inputTopic);
        events.foreach(new ForeachAction<String, Event>(){
            @Override
            public void apply(String key, Event event) {
                long returnValue = jedis.pfadd(String.valueOf(event.getBucketTimestamp()), event.getUid());
                logger.info("Return value is: " + returnValue);
            }
        });
    }
}
