package {{ root_package }}.aws.v2.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.net.URI;
import java.util.Map;

@ConfigurationProperties(prefix = "s3.v2")
@ConstructorBinding
public class S3PropertiesV2 {

  private final String endpoint;
  private final long bufferSize;
  private final Map<String, String> buckets;
  private final int maxConcurrency;

  public S3PropertiesV2(
      @DefaultValue("http://localhost.localstack.cloud:4566") String endpoint,
      @DefaultValue("8192") long bufferSize,
      Map<String, String> buckets,
      @DefaultValue("50") int maxConcurrency) {
    this.endpoint = endpoint;
    this.bufferSize = bufferSize;
    this.buckets = buckets;
    this.maxConcurrency = maxConcurrency;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public URI getEndpointURI() {
    return URI.create(getEndpoint());
  }

  public long getBufferSize() {
    return bufferSize;
  }

  public Map<String, String> getBuckets() {
    return buckets;
  }

  public int getMaxConcurrency() {
    return maxConcurrency;
  }
}
