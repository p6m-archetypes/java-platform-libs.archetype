package {{ root_package }}.aws.v2.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import java.util.Set;

public class JacksonMaskUtil {
    private final ObjectMapper mapper;

    private static final int MASK_LENGTH = 4;

    public JacksonMaskUtil(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    public JsonNode mask(JsonNode object, Set<String> maskedKeys) {
        var newObject = new ObjectNode(mapper.getNodeFactory());

        object.fields().forEachRemaining(entry -> {
            var key = entry.getKey();
            var element = entry.getValue();
            if (primitiveNeedsMask(key, element, maskedKeys)) {
                element = maskPrimitive((ValueNode) element);
            } else if (element.isObject()) {
                element = maskJsonObject(element, maskedKeys);
            } else if (element.isArray()) {
                element = maskJsonArray(key, (ArrayNode) element, maskedKeys);
            }
            newObject.set(key, element);
        });
        return newObject;
    }

    private JsonNode maskJsonObject(JsonNode element, Set<String> maskedKeys) {
        return mask(element, maskedKeys);
    }

    private ArrayNode maskJsonArray(String key, ArrayNode jsonArray, Set<String> maskedKeys) {
        var newArray = new ArrayNode(mapper.getNodeFactory());
        jsonArray.elements().forEachRemaining(element -> {
            if (primitiveNeedsMask(key, element, maskedKeys)) {
                element = maskPrimitive((ValueNode) element);
            } else if (element.isObject()) {
                element = maskJsonObject(element, maskedKeys);
            }
            newArray.add(element);
        });
        return newArray;
    }

    public ValueNode maskPrimitive(ValueNode element) {
        var value = element.asText();
        int len;
        if (value.isBlank()) {
            return TextNode.valueOf(value);
        } else if (value.length() > MASK_LENGTH) {
            len = value.length() - MASK_LENGTH;
        } else {
            len = value.length() - 1;
        }
        return TextNode.valueOf(String.format("%s%s", "*".repeat(len), value.substring(len)));
    }

    private boolean primitiveNeedsMask(String key, JsonNode element, Set<String> maskedKeys) {
        return element.isValueNode() && maskedKeys.contains(key);
    }
}
