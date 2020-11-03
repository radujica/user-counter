package streams;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class EventSerializer implements Serializer<Event> {

    private static final ObjectMapper om = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Event data) {
        byte[] returnValue = null;
        try {
            returnValue = om.writeValueAsString(data).getBytes();
        } catch (JsonProcessingException e) {
            throw new SerializationException();
        }

        return returnValue;
    }
    
}
