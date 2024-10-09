package {{ root_package }}.aws.v2;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import io.awspring.cloud.core.config.AmazonWebserviceClientFactoryBean;
import io.awspring.cloud.core.region.RegionProvider;
import {{ root_package }}.aws.v2.core.AwsCoreConfig;
import {{ root_package }}.aws.v2.storage.S3Properties;
import {{ root_package }}.aws.v2.storage.S3StreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Import({AwsCoreConfig.class, S3Properties.class})
public class S3ClientConfig {
  private final S3Properties s3Properties;
  private final AWSCredentialsProvider awsCredentialsProvider;
  private final RegionProvider regionProvider;

  @Autowired
  public S3ClientConfig(
      S3Properties s3Properties,
      AWSCredentialsProvider awsCredentialsProvider,
      RegionProvider regionProvider) {
    this.s3Properties = s3Properties;
    this.awsCredentialsProvider = awsCredentialsProvider;
    this.regionProvider = regionProvider;
  }

  @Lazy
  @Bean(destroyMethod = "shutdown")
  public AmazonS3Client amazonS3() throws Exception {
    AmazonWebserviceClientFactoryBean<AmazonS3Client> clientFactoryBean =
        new AmazonWebserviceClientFactoryBean<>(
            AmazonS3Client.class, this.awsCredentialsProvider, this.regionProvider);

    if (this.isLocalStack()) {
      clientFactoryBean.setCustomEndpoint(this.s3Properties.getEndpointURI());
    }

    clientFactoryBean.afterPropertiesSet();
    return clientFactoryBean.getObject();
  }

  @Lazy
  @Bean
  public S3StreamReader s3StreamReader(AmazonS3 amazonS3) {
    return new S3StreamReader(amazonS3, s3Properties.getBufferSize());
  }


  private boolean isLocalStack() {
    return this.awsCredentialsProvider
        .getCredentials()
        .getAWSAccessKeyId()
        .equalsIgnoreCase("localstack");
  }
}
