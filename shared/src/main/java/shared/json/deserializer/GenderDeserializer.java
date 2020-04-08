package shared.json.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;

import shared.model.Gender;

/**
 * A {@link JsonDeserializer} for interpreting the {@link Gender} enumeration
 *
 * @author griffinbholt
 */
public class GenderDeserializer implements JsonDeserializer<Gender> {
    /**
     * Reads the gender abbreviation string from the JSON string ("m"/"f")
     * and converts it to a {@link Gender} enumeration, for internal server use
     * @param jsonElement The {@link JsonElement}, representing the gender abbreviation string
     * @param type The element type
     * @param context The JSON Serialization context
     * @return The converted {@link Gender} enumeration
     */
    @Override
    public Gender deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        return Gender.generate(jsonElement.getAsString());
    }
}
