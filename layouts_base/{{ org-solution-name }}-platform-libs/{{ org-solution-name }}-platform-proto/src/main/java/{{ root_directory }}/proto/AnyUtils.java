package {{ root_package }}.proto;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import java.util.List;
import java.util.stream.Collectors;

public class AnyUtils {
    private AnyUtils() {}

    public static <T extends Message> T findElementOfType(List<Any> list, Class<T> clazz) {
        var found = list.stream()
                .filter(any -> any.is(clazz))
                .collect(Collectors.toList());

        if (found.size() != 1) {
            throw new IllegalArgumentException("Expected one instance of " + clazz + " but found " + found.size());
        }

        return uncheckedUnpack(found.get(0), clazz);
    }

    public static <T extends Message> List<T> findElementsOfType(List<Any> list, Class<T> clazz) {
        return list.stream()
                .filter(any -> any.is(clazz))
                .map(any -> uncheckedUnpack(any, clazz))
                .collect(Collectors.toList());
    }

    public static <T extends Message> T uncheckedUnpack(Any any, Class<T> clazz) {
        try {
            return any.unpack(clazz);
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
