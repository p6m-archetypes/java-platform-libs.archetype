package {{ root_package }}.aws.v2.client.interaction.serializers;

public interface JsonSerializer<JsonType> {

    JsonType serializeToJsonObject(Object input);

    JsonType serializeToJsonObject(String input);

    String serializeToString(Object input);

    <T> T deserialize(JsonType input, Class<T> type);
}
