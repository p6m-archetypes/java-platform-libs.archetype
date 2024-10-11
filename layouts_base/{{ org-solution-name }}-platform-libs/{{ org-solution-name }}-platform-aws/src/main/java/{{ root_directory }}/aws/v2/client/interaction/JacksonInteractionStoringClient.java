package {{ root_package }}.aws.v2.client.interaction;

import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import {{ root_package }}.aws.v2.client.S3Client;
import {{ root_package }}.aws.v2.client.interaction.serializers.JacksonSerializer;
import {{ root_package }}.aws.v2.utils.JacksonMaskUtil;

import java.util.Set;

public abstract class JacksonInteractionStoringClient extends InteractionStoringClient<Object, Object, JsonNode> {

    protected final S3Client s3Client;
    protected final ObjectMapper objectMapper;
    protected final JacksonMaskUtil jacksonMaskUtil;

    public JacksonInteractionStoringClient(AmazonS3Client amazonS3, ObjectMapper objectMapper, String bucket) {
        super(amazonS3, bucket, new JacksonSerializer(objectMapper));
        this.objectMapper = objectMapper;
        this.s3Client = new S3Client(amazonS3, bucket);
        this.jacksonMaskUtil = new JacksonMaskUtil(objectMapper);
    }

    protected abstract String buildS3Key(String[] s3KeyVars);

    public JsonNode mask(JsonNode object, Set<String> maskedKeys) {
        return jacksonMaskUtil.mask(object, maskedKeys);
    }
}
