package msg.flight.manager.persistence.repositories.utils;

import msg.flight.manager.persistence.dtos.flights.attributes.AttributeDTO;
import msg.flight.manager.persistence.dtos.user.update.UpdateUserDto;
import msg.flight.manager.persistence.models.flights.DBAttribute;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AttributeRepositoryUtils {
    public static Map<String, Object> getFieldsValue(AttributeDTO attribute) throws IllegalAccessException {
        Map<String, Object> fieldsValue = new HashMap<>();
        Class<?> clazz = attribute.getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                fieldsValue.put(field.getName(), field.get(attribute));
            }
            clazz = clazz.getSuperclass();
        }
        return fieldsValue;
    }
}
