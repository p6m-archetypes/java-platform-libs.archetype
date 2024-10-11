package {{ root_package }}.proto;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OneOfUtils {
    private OneOfUtils() {}

    public static <T, U> List<T> findElementsOfType(List<U> list, Predicate<U> filterPredicate, Function<U, T> mappingFunction) {
        return list.stream()
                .filter(filterPredicate)
                .map(mappingFunction)
                .collect(Collectors.toList());
    }

    public static <T, U> T findOneElementOfType(List<U> list, Predicate<U> filterPredicate, Function<U, T> mappingFunction) {
        var found = list.stream()
                .filter(filterPredicate)
                .collect(Collectors.toList());

        if (found.size() != 1) {
            throw new IllegalArgumentException("Expected one instance of given type but found " + found.size());
        }

        return mappingFunction.apply(found.get(0));
    }
}