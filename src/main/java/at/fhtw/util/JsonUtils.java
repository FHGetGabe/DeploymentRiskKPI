package at.fhtw.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

  private static final ObjectMapper mapper = new ObjectMapper();

  public static <T> T fromJson (String json,
                                Class<T> clazz) {
    try {
      return mapper.readValue(json, clazz);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse JSON", e);
    }
  }

  public static <T> T convertValue (Object source,
                                    Class<T> targetClass) {
    try {
      return mapper.convertValue(source, targetClass);
    } catch (Exception e) {
      throw new RuntimeException("Failed to convert object", e);
    }
  }

}
