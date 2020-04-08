package shared.json.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.time.Year;

/**
 * A {@link JsonDeserializer} for interpreting the {@link Year} class.
 *
 * @author griffinbholt
 */
public class YearDeserializer implements JsonDeserializer<Year> {
    /**
     * Reads the year integer from the JSON string and converts it to a {@link Year} object,
     * for internal server use
     * @param jsonElement The {@link JsonElement}, representing the year integer
     * @param type The element type
     * @param context The JSON Serialization context
     * @return The converted {@link Year} object
     */
    @Override
    public Year deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        return Year.of(jsonElement.getAsInt());
    }
}
