package {{ root_package }}.validation;

import com.google.common.base.Strings;
import {{ root_package }}.errorhandling.exceptions.InvalidArgumentException;
import {{ root_package }}.proto.ProtobufTypeConverter;
import {{ root_package }}.v1.types.BigDecimalValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Validators {

    private static final Pattern UUID_REGEX = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private static final Pattern US_ZIP_REGEX = Pattern.compile("^[0-9]{5}$");
    private static final Pattern US_PLUS_4_CODE_REGEX = Pattern.compile("^[0-9]{4}$");
    private static final Pattern US_DATE_REGEX = Pattern.compile("^(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}$");
    private static final Pattern COUNTRY_CODE_REGEX = Pattern.compile("^[A-Z]{2}$");
    private static final Pattern US_STATE_CODE_REGEX = Pattern.compile("^[A-Z]{2}$");
    private static final Pattern PHONE_REGEX = Pattern.compile("^\\d{9,11}$");
    private static final Pattern EMAIL_REGEX = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
    private static final Pattern URI_REGEX = Pattern.compile("(?i)^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$");
    private static final Pattern VERSION_REGEX = Pattern.compile("\\d+");

    public static ValidationContext withValidationCtx() {
        return new ValidationContext();
    }

    public static class ValidationContext {

        private final Map<String, String> validationCtx;

        private ValidationContext() {
            this.validationCtx = new HashMap<>();
        }

        public void validate() {
            if (!validationCtx.isEmpty()) {
                throw new InvalidArgumentException(validationCtx);
            }
        }

        public ValidationContext withTrue(String field, boolean valueToCheck) {
            if (!valueToCheck) {
                isInvalid(field);
            }
            return this;
        }

        public ValidationContext withNotEmptyString(String field, String value) {
            if (Strings.isNullOrEmpty(value)) {
                isEmpty(field);
            }
            return this;
        }

        public ValidationContext withValidUuid(String field, String value) {
            if (!UUID_REGEX.matcher(value).matches()) {
                isInvalid(field);
            }
            return this;
        }

        public ValidationContext withValidBigDecimal(String field, BigDecimalValue value) {
            try {
                ProtobufTypeConverter.toBigDecimal(value);
            } catch (Exception e) {
                isInvalid(field);
            }
            return this;
        }

        public ValidationContext withValidUsZip(String field, String value) {
            if (!US_ZIP_REGEX.matcher(value).matches()) {
                isInvalid(field);
            }
            return this;
        }

        public ValidationContext withValidUsPlus4Code(String field, String value) {
            if (!US_PLUS_4_CODE_REGEX.matcher(value).matches()) {
                isInvalid(field);
            }
            return this;
        }

        public ValidationContext withValidDate(String field, String value) {
            if (!US_DATE_REGEX.matcher(value).matches()) {
                isInvalid(field);
            }
            return this;
        }

        public ValidationContext withValidCountryCode(String field, String value) {
            if (!COUNTRY_CODE_REGEX.matcher(value).matches()) {
                isInvalid(field);
            }
            return this;
        }

        public ValidationContext withValidUsStateCode(String field, String value) {
            if (!US_STATE_CODE_REGEX.matcher(value).matches()) {
                isInvalid(field);
            }
            return this;
        }

        public ValidationContext withValidEmail(String field, String value) {
            if (!EMAIL_REGEX.matcher(value).matches()) {
                isInvalid(field);
            }
            return this;
        }

        public ValidationContext withValidPhone(String field, String value) {
            if (!PHONE_REGEX.matcher(value).matches()) {
                isInvalid(field);
            }
            return this;
        }

        public ValidationContext withValidUri(String field, String value) {
            if (!URI_REGEX.matcher(value).matches()) {
                isInvalid(field);
            }
            return this;
        }

        public ValidationContext withValidVersion(String field, String value) {
            if (!VERSION_REGEX.matcher(value).matches()) {
                isInvalid(field);
            }
            return this;
        }

        public ValidationContext withNotEquals(String field, Object value1, Object value2) {
            if (value1.equals(value2)) {
                isInvalid(field);
            }
            return this;
        }

        public ValidationContext withValueInAllowedList(String field, Object value, List<Object> allowedValues) {
            boolean isAllowed = false;
            for (Object allowedValue : allowedValues) {
                if (value.equals(allowedValue)) {
                    isAllowed = true;
                    break;
                }
            }
            if (!isAllowed) {
                isInvalid(field);
            }
            return this;
        }


        private void isEmpty(String field) {
            failValidation(field, "Is Empty");
        }

        private void isInvalid(String field) {
            failValidation(field, "Is Invalid");
        }

        public void failValidation(String field, String message) {
            validationCtx.put(field, message);
        }

    }
}
