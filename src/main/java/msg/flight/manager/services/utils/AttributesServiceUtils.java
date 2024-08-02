package msg.flight.manager.services.utils;

import msg.flight.manager.persistence.dtos.flights.enums.AttributesClasses;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttributesServiceUtils {
    public static String createClassAttributeName(String name) {
        String[] words = name.split("[\\s-_]+");
        String attributeName = "";
        for (int wordNumber = 0; wordNumber < words.length; wordNumber++) {
            if (wordNumber == 0) {
                attributeName += words[wordNumber].toLowerCase();
            } else {
                attributeName = attributeName + words[wordNumber].substring(0, 1).toUpperCase() + words[wordNumber].substring(1).toLowerCase();
            }
        }
        return attributeName;
    }

    public static List<String> generateSearchKey(String name, String type) {
        List<String> words = Arrays.stream(name.split("[\\s-_]+"))
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        words.add(type.toLowerCase());
        words.add(name.toLowerCase());
        return words;
    }

    public static void parseDefaultValue(Object value){
        LinkedHashMap<String,Object> map = (LinkedHashMap<String, Object>) value;
        for(Map.Entry<String,Object> field : map.entrySet()){;
            LinkedHashMap<String,Object> _value = (LinkedHashMap<String, Object>) field.getValue();
            if(_value.containsKey("type")&& ((String)_value.get("type")).equals("CUSTOM")){
                parseDefaultValue(_value.get("value"));
            }
            else{
                try{
                    AttributesClasses.valueOf((String)_value.get("type"));
                    if(!(_value.containsKey("value")&&_value.get("value")!=null)){
                        throw new Exception("No value found for type");
                    }
                }catch (Exception ex){
                    throw new RuntimeException(ex);
                }
            }
        }

    }

    public static Map<String,Object> stringJsonToMap(String  json){
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Map<String, Object> map = objectMapper.convertValue(jsonNode, Map.class);
            System.out.println(map);
            return map;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
