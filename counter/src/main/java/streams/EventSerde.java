package streams;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class EventSerde implements Serde<Event> {
    private static final EventSerializer serializer = new EventSerializer();
    private static final EventDeserializer deserializer = new EventDeserializer();

    @Override
    public Serializer<Event> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<Event> deserializer() {
        return deserializer;
    }
    
}
