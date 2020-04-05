package shared.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import shared.model.Gender;

import java.lang.reflect.Type;

/**
 * A {@link JsonSerializer} for interpreting the {@link Gender} enumeration
 * @author griffinbholt
 */
public class GenderSerializer implements JsonSerializer<Gender> {
    /**
     * Extracts the gender abbreviation string ("m"/"f") from the {@link Gender} enumeration
     * for consistency between the client and server
     * @param gender the {@link Gender} enumeration being written to a JSON string
     * @param type The element type
     * @param context The JSON Serialization context
     * @return The extracted gender abbreviation string in the form of a {@link JsonElement}
     */
    @Override
    public JsonElement serialize(Gender gender, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(gender.toAbbreviation());
    }
}
