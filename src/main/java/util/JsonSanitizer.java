package util;

import com.google.gson.*;

public class JsonSanitizer {

    public static JsonElement removeKeyRecursively(JsonElement element, String keyToRemove) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            obj.remove(keyToRemove); // حذف کلید اگر وجود دارد

            for (String key : obj.keySet()) {
                JsonElement value = obj.get(key);
                obj.add(key, removeKeyRecursively(value, keyToRemove)); // ادامه بازگشت روی اعضای داخلی
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            JsonArray newArray = new JsonArray();
            for (JsonElement item : array) {
                newArray.add(removeKeyRecursively(item, keyToRemove));
            }
            return newArray;
        }
        return element;
    }

    public static String sanitizeJson(String json, String keyToRemove) {
        try {
            JsonElement root = JsonParser.parseString(json);
            JsonElement cleaned = removeKeyRecursively(root, keyToRemove);
            return new GsonBuilder().setPrettyPrinting().create().toJson(cleaned);
        } catch (JsonSyntaxException e) {
            return json; // اگر JSON مشکل داشت همون رو برگردون
        }
    }
}
