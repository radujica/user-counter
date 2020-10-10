package counter;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;


public class UniqueUserCounter {
    static final String inputTopic = "feed";

    public static void main(final String[] args) {
        final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        final Properties streamsConfiguration = getStreamsConfiguration(bootstrapServers);

        final StreamsBuilder builder = new StreamsBuilder();
        createWordCountStream(builder);
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
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
        final KStream<String, String> textLines = builder.stream(inputTopic);
        final Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);

        // final KTable<String, Long> wordCounts = textLines
        // Split each text line, by whitespace, into words.  The text lines are the record
        // values, i.e. we can ignore whatever data is in the record keys and thus invoke
        // `flatMapValues()` instead of the more generic `flatMap()`.
        // .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
        // Group the split data by word so that we can subsequently count the occurrences per word.
        // This step re-keys (re-partitions) the input data, with the new record key being the words.
        // Note: No need to specify explicit serdes because the resulting key and value types
        // (String and String) match the application's default serdes.
        // .groupBy((keyIgnored, word) -> word)
        // Count the occurrences of each word (record key).
        // .count();
    }
}
