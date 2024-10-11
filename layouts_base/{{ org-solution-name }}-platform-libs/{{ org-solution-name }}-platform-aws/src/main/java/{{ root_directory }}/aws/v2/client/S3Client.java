package {{ root_package }}.aws.v2.client;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.StringInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import {{ root_package }}.aws.v2.error.S3ClientError;
import {{ root_package }}.aws.v2.error.S3ErrorType;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static java.nio.charset.StandardCharsets.UTF_8;

public class S3Client {

  private static final String APPLICATION_JSON = "application/json";
  private static final String JSON_EXTENSION = ".json";
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final AmazonS3 amazonS3;
  private final String bucket;

  /**
   * This client is meant to provide a simple interface to store objects in S3.
   *
   * @param amazonS3 Autowired client that is configured by importing
   *     {{ root_package }}.aws.v2.S3ClientConfig.class
   * @param bucket Name of bucket objects will be stored in on S3
   */
  public S3Client(AmazonS3Client amazonS3, String bucket) {
    this.amazonS3 = amazonS3;
    this.bucket = bucket;
  }

  public Future<Void> storeAsync(String body, String s3key) {
    return CompletableFuture.runAsync(() -> store(body, s3key));
  }

  public void store(String content, String s3key) {
    try {
      log.info("Storing content at s3key={}", s3key);
      InputStream inputJsonStream = new StringInputStream(content);
      ObjectMetadata metadata = buildMetadata(content);
      PutObjectRequest request = new PutObjectRequest(bucket, s3key, inputJsonStream, metadata);
      amazonS3.putObject(request);
      log.info("Successfully stored content at s3key={}", s3key);
    } catch (IOException e) {
      log.error(
          "Error creating stream for json with for content error={}",
          e.getMessage(),
          e);
      throw new S3ClientError(S3ErrorType.JSON_STREAMING, e);
    } catch (AmazonServiceException e) {
      log.error(
          "Error uploading payload to S3 bucket:{}: error={}",
          bucket,
          e.getMessage(),
          e);
      throw new S3ClientError(S3ErrorType.AWS, e);
    }
  }

  public String buildJsonS3Key(String... keyComponents) {
    if (keyComponents.length == 0) {
      throw new IllegalArgumentException("Must have more than one pathComponent");
    }
    var builder = new StringBuilder();
    for (String component : keyComponents) {
      builder.append(component);
      builder.append("/");
    }
    builder.deleteCharAt(builder.length() - 1);
    builder.append(JSON_EXTENSION);
    return builder.toString();
  }

  private ObjectMetadata buildMetadata(String content) {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(APPLICATION_JSON);
    metadata.setContentLength(content.getBytes(UTF_8).length);
    return metadata;
  }
}
