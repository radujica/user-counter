package streams;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    private final long bucketTimestamp;
    private final long timestamp;
    private final String uid;

    @JsonCreator
    public Event(
        @JsonProperty("bucket") long bucketTimestamp,
        @JsonProperty("ts") long timestamp,
        @JsonProperty("uid") String uid
    ) {
        this.bucketTimestamp = bucketTimestamp;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public long getBucketTimestamp() {
        return bucketTimestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public String toString() {
        return "Event [bucketTimestamp=" + bucketTimestamp + ", timestamp=" + timestamp + ", uid=" + uid + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (bucketTimestamp ^ (bucketTimestamp >>> 32));
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
        result = prime * result + ((uid == null) ? 0 : uid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Event other = (Event) obj;
        if (bucketTimestamp != other.bucketTimestamp)
            return false;
        if (timestamp != other.timestamp)
            return false;
        if (uid == null) {
            if (other.uid != null)
                return false;
        } else if (!uid.equals(other.uid))
            return false;
        return true;
    }
}
