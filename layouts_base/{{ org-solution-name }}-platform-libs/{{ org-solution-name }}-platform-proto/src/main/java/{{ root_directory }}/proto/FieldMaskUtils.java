package {{ root_package }}.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.FieldMask;

public class FieldMaskUtils {

    private static final String FIELD_SEPARATOR_REGEX = "\\.";
    private static final int MAX_FIELD_NESTING = 2;

    public static boolean isFieldRequested(FieldMask fieldMask, Descriptors.Descriptor descriptor, int requestedFieldNumber) {
        var fieldName = descriptor
                .findFieldByNumber(requestedFieldNumber)
                .getName();
        return fieldMask.getPathsList().stream()
                .map(path -> path.split(FIELD_SEPARATOR_REGEX, MAX_FIELD_NESTING)[0])
                .anyMatch(fieldName::equals);
    }
}
