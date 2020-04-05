package shared.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Year;

import shared.json.deserializer.EventTypeDeserializer;
import shared.json.deserializer.GenderDeserializer;
import shared.json.deserializer.YearDeserializer;
import shared.json.serializer.DoubleSerializer;
import shared.json.serializer.EventTypeSerializer;
import shared.json.serializer.GenderSerializer;
import shared.json.serializer.YearSerializer;
import shared.model.EventType;
import shared.model.Gender;

public class JsonInterpreter {
    private final Gson gsonSerializer = new GsonBuilder()
            .registerTypeAdapter(Year.class, new YearSerializer())
            .registerTypeAdapter(EventType.class, new EventTypeSerializer())
            .registerTypeAdapter(Gender.class, new GenderSerializer())
            .registerTypeAdapter(Double.class, new DoubleSerializer())
            .setPrettyPrinting()
            .create();

    private final Gson gsonDeserializer = new GsonBuilder()
            .registerTypeAdapter(Year.class, new YearDeserializer())
            .registerTypeAdapter(EventType.class, new EventTypeDeserializer())
            .registerTypeAdapter(Gender.class, new GenderDeserializer())
            .create();

    public String generateJsonString(Object object) {
        return gsonSerializer.toJson(object);
    }

    public Object parseJson(String jsonString, Class<?> jsonClass) {
        return gsonDeserializer.fromJson(jsonString, jsonClass);
    }
}
