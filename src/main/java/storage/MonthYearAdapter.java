package storage;

import com.google.gson.*;
import model.MonthYear;

import java.lang.reflect.Type;

public class MonthYearAdapter implements JsonSerializer<MonthYear>, JsonDeserializer<MonthYear> {
    @Override
    public MonthYear deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return MonthYear.parse(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(MonthYear monthYear, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(monthYear.toString());
    }
}
