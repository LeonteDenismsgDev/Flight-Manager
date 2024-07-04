package msg.flight.manager.services.flights.validation.number.validators.typebased;

import java.util.List;

public class NumberValidator<T extends Number & Comparable<T>> {
    public boolean isLesThan(T value, T reference) {
        return value.compareTo(reference) < 0;
    }

    public boolean isLessOrEqualsWith(T value, T reference) {
        return value.compareTo(reference) <= 0;
    }

    public boolean isGreaterThan(T value, T reference) {
        return value.compareTo(reference) > 0;
    }

    public boolean isGreaterOrEqualWith(T value, T reference) {
        return value.compareTo(reference) >= 0;
    }

    public boolean isEqualWith(T value, T reference) {
        return value.compareTo(reference) == 0;
    }

    boolean isContainedIn(T value, List<T> reference) {
        return reference.contains(value);
    }
}
