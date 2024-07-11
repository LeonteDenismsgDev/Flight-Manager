package msg.flight.manager.services.utils;

import java.util.Arrays;
import java.util.List;
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
}
