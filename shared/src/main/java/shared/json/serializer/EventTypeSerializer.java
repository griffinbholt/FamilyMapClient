package shared.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import shared.model.EventType;

/**
 * A {@link JsonSerializer} for interpreting the {@link EventType} class
 *
 * @author griffinbholt
 */
public class EventTypeSerializer implements JsonSerializer<EventType> {
    /**
     * Extracts the string from the {@link EventType} object,
     * for consistency between the client and server
     * @param eventType The {@link EventType} object being written to a JSON string
     * @param type The element type
     * @param context The JSON Serialization context
     * @return The extracted eventType string in the form of a {@link JsonElement}
     */
    @Override
    public JsonElement serialize(EventType eventType, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(eventType.getEventName());
    }
}
