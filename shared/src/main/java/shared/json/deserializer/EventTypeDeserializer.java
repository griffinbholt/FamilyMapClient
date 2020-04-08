package shared.json.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

import shared.model.EventType;

/**
 * A {@link JsonDeserializer} for interpreting the {@link EventType} class
 *
 * @author griffinbholt
 */
public class EventTypeDeserializer implements JsonDeserializer<EventType> {
    /**
     * Reads the eventType element from a JSON string and converts it to an {@link EventType} object,
     * for internal server use
     * @param jsonElement The {@link JsonElement}, representing the eventType string
     * @param type The element type
     * @param context The JSON Serialization context
     * @return The converted {@link EventType} object
     */
    @Override
    public EventType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        return EventType.generate(jsonElement.getAsString());
    }
}
