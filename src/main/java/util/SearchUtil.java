package util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.lang.reflect.Field;

public class SearchUtil {

    public static <T> List<T> search(List<T> data,
                                     Class<T> clazz,
                                     String searchTerm,
                                     List<String> searchFields,
                                     Map<String, String> filters) {
        String searchFilter = (searchTerm == null || searchTerm.isBlank()) ? null : searchTerm.toLowerCase();

        return data.stream().filter(obj -> {
            boolean match = true;

            // ------ SearchTerm logic ------
            if (searchFilter != null && searchFields != null) {
                boolean found = false;
                for (String fieldPath : searchFields) {
                    try {
                        Object value = getNestedFieldValue(obj, fieldPath);
                        if (value != null && value.toString().toLowerCase().contains(searchFilter)) {
                            found = true;
                            break;
                        }
                    } catch (Exception ignored) {}
                }
                match &= found;
            }

            // ------ Filters logic ------
            if (filters != null) {
                for (Map.Entry<String, String> entry : filters.entrySet()) {
                    try {
                        Object fieldValue = getNestedFieldValue(obj, entry.getKey());
                        if (fieldValue == null || !fieldValue.toString().contains(entry.getValue())) {
                            match = false;
                            break;
                        }
                    } catch (Exception ignored) {
                        match = false;
                        break;
                    }
                }
            }

            return match;
        }).collect(Collectors.toList());
    }

    // Access nested field by reflection (supports "a.b.c")
    public static Object getNestedFieldValue(Object obj, String fieldPath) throws Exception {
        String[] parts = fieldPath.split("\\.");
        Object current = obj;

        for (String part : parts) {
            if (current == null) return null;
            Field field = current.getClass().getDeclaredField(part);
            field.setAccessible(true);
            current = field.get(current);
        }

        return current;
    }
}
