package at.fhtw.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Parse JSON string into an instance of the specified class.
     *
     * @param json  JSON string to parse
     * @param clazz target class type
     * @param <T>   type of the target class
     * @return parsed object of type T
     * @throws RuntimeException if parsing fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }
}
