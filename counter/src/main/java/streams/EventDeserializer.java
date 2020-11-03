package streams;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class EventDeserializer implements Deserializer<Event> {

    private static final ObjectMapper om = new ObjectMapper();

    @Override
    public Event deserialize(String topic, byte[] data) {
        Event returnValue = null;
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            returnValue = om.readValue(data, Event.class);
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return returnValue;
    }
    
}
