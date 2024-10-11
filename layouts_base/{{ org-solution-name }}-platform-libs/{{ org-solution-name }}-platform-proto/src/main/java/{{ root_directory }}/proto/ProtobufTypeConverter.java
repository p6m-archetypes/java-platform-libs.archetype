package {{ root_package }}.proto;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.google.protobuf.Timestamp;
import com.google.type.Money;
import {{ root_package }}.data.status.codes.Immutable{{ SolutionName }}StatusCode;
import {{ root_package }}.data.status.codes.{{ SolutionName }}StatusCode;
import {{ root_package }}.errorhandling.exceptions.InvalidArgumentException;
import {{ root_package }}.v1.types.BigDecimalValue;
import {{ root_package }}.v1.types.CurrencyDto;
import {{ root_package }}.v1.types.NullableBool;
import {{ root_package }}.v1.types.NullableInt;
import {{ root_package }}.v1.types.NullableString;
import {{ root_package }}.v1.types.PercentageValue;
import {{ root_package }}.v1.types.UuidValue;
import {{ root_package }}.v1.types.{{ SolutionName }}StatusCodeValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static {{ root_package }}.v1.types.CurrencyDto.CAD;
import static {{ root_package }}.v1.types.CurrencyDto.USD;


public final class ProtobufTypeConverter {

    public static Optional<BigDecimal> toBigDecimal(final Money money) {
        if (money == null || !money.isInitialized())
            return Optional.empty();

        var bigUnits = BigDecimal.valueOf(money.getUnits());
        var bigNanos = BigDecimal.valueOf(money.getNanos());
        var bigDecimal = bigUnits.add(bigNanos.movePointLeft(9));

        return Optional.of(bigDecimal);
    }

    public static Money toMoney(BigDecimal amount, String currencyCode) {
        try{
            Currency.getInstance(currencyCode);
        }catch(Exception e){
            throw InvalidArgumentException.from("currencyCode",
                "Invalid currency code: " + currencyCode);
        }

        var units = amount.longValue();
        var nanos = amount.subtract(BigDecimal.valueOf(units)).movePointRight(9).intValue();
        return Money.newBuilder()
                    .setCurrencyCode(currencyCode)
                    .setUnits(units)
                    .setNanos(nanos)
                    .build();
    }

    public static Money toMoney(BigDecimal amount) {
        var units = amount.longValue();
        var nanos = amount.subtract(BigDecimal.valueOf(units)).movePointRight(9).intValue();
        return Money.newBuilder()
                    .setUnits(units)
                    .setNanos(nanos)
                    .build();
    }

    public static BigDecimalValue toBigDecimalValue(BigDecimal decimal) {
        if (decimal == null) {
            return BigDecimalValue.getDefaultInstance();
        } else {
            return BigDecimalValue.newBuilder()
                                  .setScale(decimal.scale())
                                  .setPrecision(decimal.precision())
                                  .setValue(ByteString.copyFrom(decimal.unscaledValue().toByteArray()))
                                  .build();
        }
    }

    public static BigDecimalValue toBigDecimalValueThroughBigDecimal(String decimal) {
        BigDecimal bigDecimal = new BigDecimal(decimal);
        return toBigDecimalValue(bigDecimal);
    }

    public static BigDecimal toBigDecimal(BigDecimalValue decimal) {
        if(decimal.equals(BigDecimalValue.getDefaultInstance())) {
            return null;
        } else {
            return new BigDecimal(
                new BigInteger(decimal.getValue().toByteArray()),
                decimal.getScale(),
                new MathContext(decimal.getPrecision())
            );
        }
    }

    public static PercentageValue toPercentageValue(BigDecimal percent) {
        return PercentageValue.newBuilder()
                              .setValue(toBigDecimalValue(percent))
                              .build();
    }

    public static Optional<BigDecimal> toPercentage(PercentageValue percent) {
        return percent.hasValue()
            ? Optional.of(percent.getValue()).map(ProtobufTypeConverter::toBigDecimal)
            : Optional.empty();
    }

    public static String toJavaString(BigDecimalValue decimal) {
        BigDecimal bd = toBigDecimal(decimal);
        if (bd == null) {
            return null;
        }
        return NumberFormat.getNumberInstance(Locale.US).format(bd);
    }

    public static String toJavaString(UuidValue value) {
        if (UuidValue.getDefaultInstance().equals(value)) {
            return null;
        }
        return value.getValue();
    }

    public static UuidValue toUuidValue(UUID value) {
        if(value == null) {
            return UuidValue.getDefaultInstance();
        }
        return UuidValue.newBuilder().setValue(value.toString()).build();
    }

    public static UUID toUuid(UuidValue value) {
        if(UuidValue.getDefaultInstance().equals(value)) {
            return null;
        }
        return UUID.fromString(value.getValue());
    }

    public static UuidValue toUuidValueThroughUuid(String value) {
        return toUuidValue(UUID.fromString(value));
    }

    public static List<UuidValue> toUuidValueList(Collection<UUID> values) {
        return values.stream()
                     .map(ProtobufTypeConverter::toUuidValue)
                     .collect(Collectors.toList());
    }

    public static List<UUID> toUuidList(Collection<UuidValue> values) {
        return values.stream()
                     .map(ProtobufTypeConverter::toUuid)
                     .collect(Collectors.toList());
    }

    public static List<UuidValue> toUuidValueThroughUuidList(Collection<String> values) {
        return values.stream()
                     .map(ProtobufTypeConverter::toUuidValueThroughUuid)
                     .collect(Collectors.toList());
    }

    public static List<String> toJavaStringList(Collection<UuidValue> values) {
        return values.stream()
                     .map(ProtobufTypeConverter::toJavaString)
                     .collect(Collectors.toList());
    }

    public static NullableString toNullableString(Optional<String> value) {
        return toNullableString(value.orElse(null));
    }

    public static NullableString toNullableString(String value) {
        if (value == null) {
            return NullableString.getDefaultInstance();
        }
        return NullableString.newBuilder().setValue(value).setSetFlag(true).build();
    }

    public static NullableInt toNullableInt(Optional<Integer> value) {
        return toNullableInt(value.orElse(null));
    }

    public static NullableInt toNullableInt(Integer value) {
        if(value == null) {
            return NullableInt.getDefaultInstance();
        }
        return NullableInt.newBuilder().setValue(value).setSetFlag(true).build();
    }

    public static NullableBool toNullableBool(Boolean value) {
        if(value != null) {
            return NullableBool.newBuilder().setSetFlag(true).setValue(value).build();
        } else {
            return NullableBool.getDefaultInstance();
        }
    }

    public static Boolean toBoolean(NullableBool value) {
        if(value.equals(NullableBool.getDefaultInstance()) || !value.getSetFlag()) {
            return null;
        } else {
            return value.getValue();
        }
    }

    public static StringValue toStringValue(String value) {
        if (value == null) {
            return StringValue.getDefaultInstance();
        }
        return StringValue.newBuilder().setValue(value).build();
    }

    public static String toJavaString(StringValue value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    public static String toJavaString(NullableString value) {
        if(value == null || !value.getSetFlag()) {
            return null;
        }
        return value.getValue();
    }

    public static Optional<String> toJavaStringOpt(NullableString value) {
        if(value == null || !value.getSetFlag()) {
            return Optional.empty();
        }
        return Optional.of(value.getValue());

    }

    public static Integer toInt(NullableInt value) {
        if(value == null || !value.getSetFlag()) {
            return null;
        }
        return value.getValue();
    }

    public static Integer toInt(Int32Value value) {
        if (value == null || value.equals(Int32Value.getDefaultInstance())) {
            return null;
        }
        return value.getValue();
    }


    public static Optional<Integer> toIntOpt(NullableInt value) {
        if (value == null || !value.getSetFlag()) {
            return Optional.empty();
        }
        return Optional.of(value.getValue());
    }

    public static Int32Value toIntValue(int value) {
        return Int32Value.of(value);
    }

    public static Instant toInstant(Timestamp timestamp){
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    public static Timestamp toTimestamp(Instant instant){
        return Timestamp.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .setNanos(instant.getNano())
                        .build();
    }

    public static BoolValue toBoolValue(boolean bool) {
        return BoolValue.newBuilder()
                        .setValue(bool)
                        .build();
    }

    public static boolean toBoolean(BoolValue bool) {
        return bool.getValue();
    }

    public static Currency toCurrency(CurrencyDto currencyDto){
        switch (currencyDto) {
            case USD: return Currency.getInstance(USD.name());
            case CAD: return Currency.getInstance(CAD.name());
            default: throw InvalidArgumentException.from("currencyDto",
                "Unknown currency DTO value: " + currencyDto);
        }
    }

    public static CurrencyDto toCurrencyDto(Currency currency){
        switch(currency.getCurrencyCode()){
            case "USD" : return USD;
            case "CAD" : return CAD;
            default: throw InvalidArgumentException.from("currencyCode",
                "Unknown currency code: " + currency);
        }
    }

    public static {{ SolutionName }}StatusCode to{{ SolutionName }}StatusCode({{ SolutionName }}StatusCodeValue {{ solutionName }}StatusCodeValue){
        return Immutable{{ SolutionName }}StatusCode.builder().addAllTags({{ solutionName }}StatusCodeValue.getTagsList()).build();
    }

    public static {{ SolutionName }}StatusCodeValue to{{ SolutionName }}StatusCodeValue({{ SolutionName }}StatusCode {{ solutionName }}StatusCode){
        return {{ SolutionName }}StatusCodeValue.newBuilder().addAllTags({{ solutionName }}StatusCode.tags()).build();
    }

    private ProtobufTypeConverter() {
        // empty
    }
}
