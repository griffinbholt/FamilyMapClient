package shared.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
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
    private final static JsonInterpreter jsonInterpreter = new JsonInterpreter();

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

    private JsonInterpreter() {
    }

    public static String generateJsonString(Object object) {
        return jsonInterpreter.gsonSerializer.toJson(object);
    }

    public static Object parseJson(String jsonString, Class<?> jsonClass) {
        return jsonInterpreter.gsonDeserializer.fromJson(jsonString, jsonClass);
    }

    public static Object parseJson(Path filePath, Class<?> jsonClass) {
        File locationFile = filePath.toFile();

        try (FileReader fileReader = new FileReader(locationFile)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            Gson gson = new Gson();
            return gson.fromJson(bufferedReader, jsonClass);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
