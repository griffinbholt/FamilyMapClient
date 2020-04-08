package shared.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A {@link JsonSerializer} for double-precision floating point numbers
 *
 * @author griffinbholt
 */
public class DoubleSerializer implements JsonSerializer<Double> {
    /**
     * Rounds any double-precision floating point numbers to 4 decimal places,
     * for consistency between the client and server
     * @param value The double-precision floating point value being written to a JSON string
     * @param type The element type
     * @param context The JSON Serialization context
     * @return The interpreted double-precision floating point value in the form of a {@link JsonElement}
     */
    @Override
    public JsonElement serialize(Double value, Type type, JsonSerializationContext context) {
        BigDecimal bdPrice = new BigDecimal(value);
        bdPrice = bdPrice.setScale(4, RoundingMode.HALF_UP);
        return new JsonPrimitive(bdPrice);
    }
}
