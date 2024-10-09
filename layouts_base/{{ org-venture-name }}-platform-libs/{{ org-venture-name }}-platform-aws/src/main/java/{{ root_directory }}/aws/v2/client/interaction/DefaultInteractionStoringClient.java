package {{ root_package }}.aws.v2.client.interaction;

import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DefaultInteractionStoringClient extends JacksonInteractionStoringClient {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu/MM/dd").withZone(ZoneOffset.UTC);

    public DefaultInteractionStoringClient(AmazonS3Client amazonS3, ObjectMapper mapper, String bucket) {
        super(amazonS3, mapper, bucket);
    }

    @Override
    protected String buildS3Key(String[] s3KeyVars) {
        if (s3KeyVars.length == 2) {
            return buildS3Key(s3KeyVars[0], s3KeyVars[1]);
        } else {
            return s3Client.buildJsonS3Key(s3KeyVars);
        }
    }

    private String buildS3Key(String userId, String type) {
        Instant now = Instant.now();
        String today = DATE_FORMATTER.format(now);
        String fileName = buildFileName(userId, now);
        return s3Client.buildJsonS3Key(type, today, fileName);
    }

    private String buildFileName(String userId, Instant now) {
        String dateTime = DateTimeFormatter.ISO_INSTANT.format(now);
        return String.join("-", userId, dateTime);
    }
}
