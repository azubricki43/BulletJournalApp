package storage;

import com.google.gson.*;
import model.WeekYear;

import java.lang.reflect.Type;

public class WeekYearAdapter implements JsonSerializer<WeekYear>, JsonDeserializer<WeekYear> {
    @Override
    public WeekYear deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return WeekYear.parse(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(WeekYear weekYear, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(weekYear.toString());
    }
}
