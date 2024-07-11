package msg.flight.manager.persistence.repositories.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectFieldsUtils {
    public static Map<String, Object> getFieldsValue(Object object) throws IllegalAccessException {
        Map<String, Object> fieldsValue = new HashMap<>();
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                fieldsValue.put(field.getName(), field.get(object));
            }
            clazz = clazz.getSuperclass();
        }
        return fieldsValue;
    }
}
