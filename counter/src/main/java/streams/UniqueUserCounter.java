package streams;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import redis.clients.jedis.Jedis;

public class UniqueUserCounter {
    static final String inputTopic = "feed";

    public static void main(final String[] args) {
        final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        final Properties streamsConfiguration = getStreamsConfiguration(bootstrapServers);

        // final Jedis jedis = new Jedis("localhost", 6379);
        // long returnValue = jedis.pfadd("tsbucket", "uid");
        // System.out.println(returnValue);

        // final StreamsBuilder builder = new StreamsBuilder();
        // createWordCountStream(builder);
        // final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

        // streams.cleanUp();
        // streams.start();

        // Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        // jedis.close();
    }

    static Properties getStreamsConfiguration(final String bootstrapServers) {
        final Properties streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "unique-user-counter");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "unique-user-counter-client");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        // flush every 10 seconds
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        // disable record caches
        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        return streamsConfiguration;
    }

    // TODO THIS
    static void createWordCountStream(final StreamsBuilder builder) {
        final KStream<String, Event> textLines = builder.stream(inputTopic);
    }
}
