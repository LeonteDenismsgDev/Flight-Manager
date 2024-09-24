package msg.flight.manager.services.flights.validation.template.array.typebased;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ArrayRuleValidator<T> {
    private T contained;
    private Integer max;
    private Integer min;

    public String validate(List<T> attributeValue, String attribute) {
        String errMsg = "";
        if (contained != null) {
            boolean isValid = attributeValue.contains(contained);
            if(!isValid){
                errMsg += attribute + " should contain " +  toJsonString((BsonValue) contained) + "\n";
            }
        }
        if (max != null) {
            boolean isValid = attributeValue.size() < max;
            if(!isValid){
                errMsg += attribute + " length should be less than " + max + "\n";
            }
        }
        if (min != null) {
            boolean isValid = attributeValue.size() > min;
            if(!isValid){
                errMsg += attribute + " length should be greater than " + min + "\n";
            }
        }
        return errMsg;
    }

    public static String toJsonString(BsonValue value) {
        if (value.isString()) {
            return "\"" + value.asString().getValue() + "\"";
        } else if (value.isInt32()) {
            return Integer.toString(value.asInt32().getValue());
        } else if (value.isInt64()) {
            return Long.toString(value.asInt64().getValue());
        } else if (value.isDouble()) {
            return Double.toString(value.asDouble().getValue());
        } else if (value.isDateTime()) {
            return "\"" + formatDate(value.asDateTime().getValue()) + "\"";
        } else if (value.isArray()) {
            return arrayToJsonString(value.asArray());
        } else if (value.isDocument()) {
            return documentToJsonString(value.asDocument());
        }
        return "";
    }

    private static String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return sdf.format(new Date(millis));
    }

    private static String arrayToJsonString(BsonArray array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(toJsonString(array.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String documentToJsonString(BsonDocument document) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (String key : document.keySet()) {
            if (!first) sb.append(", ");
            sb.append("\"").append(key).append("\": ").append(toJsonString(document.get(key)));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
