package {{ root_package }}.aws.v2.storage;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "s3")
public class S3Properties {
  private String endpoint = "http://localhost.localstack.cloud:4566";

  private Map<String, String> buckets;

  private long bufferSize = 8192;

  public String getEndpoint() {
    return endpoint;
  }

  public URI getEndpointURI() {
    return URI.create(this.getEndpoint());
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public Map<String, String> getBuckets() {
    return Collections.unmodifiableMap(this.buckets);
  }

  public void setBuckets(Map<String, String> buckets) {
    this.buckets = buckets;
  }

  public long getBufferSize() {
    return bufferSize;
  }

  public void setBufferSize(long bufferSize) {
    this.bufferSize = bufferSize;
  }
}

