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
        String searchFilter = (searchTerm != null && !searchTerm.isBlank()) ? searchTerm.toLowerCase() : null;

        return data.stream()
                .filter(obj -> {

                    if (searchFilter != null && searchFields != null && !searchFields.isEmpty()) {
                        boolean anyFieldMatches = searchFields.stream().anyMatch(fieldPath -> {
                            try {
                                Object value = getNestedFieldValue(obj, fieldPath);
                                return value != null && value.toString().toLowerCase().contains(searchFilter);
                            } catch (Exception e) {
                                return false;
                            }
                        });

                        if (!anyFieldMatches) return false;
                    }

                    if (filters != null && !filters.isEmpty()) {
                        for (Map.Entry<String, String> entry : filters.entrySet()) {
                            try {
                                Object value = getNestedFieldValue(obj, entry.getKey());
                                if (value == null || !value.toString().contains(entry.getValue())) {
                                    return false;
                                }
                            } catch (Exception e) {
                                return false;
                            }
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }


    public static Object getNestedFieldValue(Object obj, String fieldPath) throws Exception {
        String[] parts = fieldPath.split("\\.");
        Object current = obj;

        for (String part : parts) {
            if (current == null) return null;

            Field field = getFieldIncludingSuper(current.getClass(), part);
            if (field == null) return null;

            field.setAccessible(true);
            current = field.get(current);
        }

        return current;
    }

    private static Field getFieldIncludingSuper(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }



}
