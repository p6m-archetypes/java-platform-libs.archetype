package {{ root_package }}.aws.v2.client.interaction;

import com.amazonaws.services.s3.AmazonS3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import {{ root_package }}.aws.v2.client.S3Client;
import {{ root_package }}.aws.v2.client.interaction.serializers.JsonSerializer;
import {{ root_package }}.aws.v2.error.S3ClientError;
import {{ root_package }}.aws.v2.model.ClientInteraction;

import java.util.Set;

public abstract class InteractionStoringClient<Request, Response, JsonType> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    protected final S3Client s3Client;
    protected final JsonSerializer<JsonType> serializer;

    public InteractionStoringClient(AmazonS3Client amazonS3, String bucket, JsonSerializer<JsonType> serializer) {
        this.s3Client = new S3Client(amazonS3, bucket);
        this.serializer = serializer;
    }

    public String storeAsync(String s3Key, Object object) {
        String content = serializer.serializeToString(object);
        return storeStringAsync(s3Key, content);
    }

    public String storeStringAsync(String s3Key, String content) {
        try {
            s3Client.storeAsync(content, s3Key);
            log.info("Successfully stored interaction at key={}", s3Key);
        } catch (S3ClientError s3ClientError) {
            log.warn("Failed to store interaction at key={} errorType={}", s3Key, s3ClientError.getErrorType(), s3ClientError);
        }
        return s3Key;
    }

    public String saveInteractionAsync(Request request, Response response, String... s3KeyVars) {
        ClientInteraction<Request, Response> interaction = new ClientInteraction<>(request, response);
        return storeRequestResponseAsync(interaction, s3KeyVars);
    }

    public abstract JsonType mask(JsonType object, Set<String> maskedKeys);

    public String saveInteractionAsyncMasked(Request request, JsonType response, Set<String> maskKeys, String... s3KeyVars) {
        JsonType maskedRequest = maskObject(request, maskKeys);
        JsonType maskedResponse = mask(response, maskKeys);
        ClientInteraction<JsonType, JsonType> interaction = new ClientInteraction<>(maskedRequest, maskedResponse);
        return storeJsonTypeAsync(interaction, s3KeyVars);
    }

    public String saveInteractionAsyncMasked(Request request, String response, Set<String> maskKeys, String... s3KeyVars) {
        JsonType jsonResponse = serializer.serializeToJsonObject(response);
        return saveInteractionAsyncMasked(request, jsonResponse, maskKeys, s3KeyVars);
    }

    protected abstract String buildS3Key(String[] s3KeyVars);

    private String storeRequestResponseAsync(ClientInteraction<Request, Response> interaction, String... s3KeyVars) {
        String s3key = buildS3Key(s3KeyVars);
        return storeAsync(s3key, interaction);
    }

    private String storeJsonTypeAsync(ClientInteraction<JsonType, JsonType> interaction, String... s3KeyVars) {
        String s3key = buildS3Key(s3KeyVars);
        return storeAsync(s3key, interaction);
    }

    private JsonType maskObject(Object object, Set<String> maskedKeys) {
        JsonType jsonObject = serializer.serializeToJsonObject(object);
        return mask(jsonObject, maskedKeys);
    }
}
