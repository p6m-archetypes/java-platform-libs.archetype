package {{ root_package }}.aws.v2.client.interaction.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerializer implements JsonSerializer<JsonNode> {

    private final ObjectMapper objectMapper;

    public JacksonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonNode serializeToJsonObject(Object input) {
        return objectMapper.valueToTree(input);
    }

    @Override
    public JsonNode serializeToJsonObject(String input) {
        try {
            return objectMapper.readTree(input);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(JsonNode input, Class<T> type) {
        try {
            return objectMapper.readValue(input.toString(), type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String serializeToString(Object input) {
        try {
            return objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
