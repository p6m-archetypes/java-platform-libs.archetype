package {{ root_package }}.aws.v2.storage;

import {{ root_package }}.aws.v2.AwsV2CoreConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Configuration
@EnableConfigurationProperties(S3PropertiesV2.class)
@Import({
    AwsV2CoreConfig.class,
})
public class S3ClientV2Config {

  @Lazy
  @Bean(destroyMethod = "close")
  @Primary
  public S3AsyncClient s3AsyncClientV2(
      AwsCredentialsProvider awsCredentialsProvider,
      AwsRegionProvider regionProvider,
      S3PropertiesV2 s3Properties) {
    S3AsyncClientBuilder builder =
        S3AsyncClient.builder()
            .httpClientBuilder(
                NettyNioAsyncHttpClient.builder().maxConcurrency(s3Properties.getMaxConcurrency()))
            .credentialsProvider(awsCredentialsProvider)
            .region(regionProvider.getRegion());
    if (this.isLocalStack(awsCredentialsProvider)) {
      builder.endpointOverride(s3Properties.getEndpointURI());
    }
    return builder.build();
  }

  @Lazy
  @Bean(destroyMethod = "close")
  @Primary
  public S3Client s3ClientV2(AwsCredentialsProvider awsCredentialsProvider,
      AwsRegionProvider regionProvider,
      S3PropertiesV2 s3Properties) {
    S3ClientBuilder builder = S3Client.builder()
        .credentialsProvider(awsCredentialsProvider)
        .region(regionProvider.getRegion());
    if (this.isLocalStack(awsCredentialsProvider)) {
      builder.endpointOverride(s3Properties.getEndpointURI());
    }
    return builder.build();
  }

  private boolean isLocalStack(AwsCredentialsProvider awsCredentialsProvider) {
    return awsCredentialsProvider.resolveCredentials().accessKeyId().equalsIgnoreCase("localstack");
  }
}
