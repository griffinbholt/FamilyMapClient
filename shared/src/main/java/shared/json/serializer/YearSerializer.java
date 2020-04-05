package shared.json.serializer;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Year;

/**
 * A {@link JsonSerializer} for interpreting the {@link Year} class
 * @author griffinbholt
 */
public class YearSerializer implements JsonSerializer<Year> {
    /**
     * Extracts the value of the {@link Year} object as an integer
     * for consistency between the client and server
     * @param year The {@link Year} object being written to a JSON string
     * @param type The element type
     * @param context The JSON Serialization context
     * @return The extracted integer value of the {@link Year} object in the form of a {@link JsonElement}
     */
    @Override
    public JsonElement serialize(Year year, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(year.getValue());
    }
}