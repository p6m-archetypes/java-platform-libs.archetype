package {{ root_package }}.aws.v2.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;

import java.util.List;

@Configuration
public class AwsV2CoreConfig {
  @Bean
  public AwsCredentialsProvider awsCredentialsProviderV2() {
    return AwsCredentialsProviderChain.builder()
        .credentialsProviders(
            List.of(
                EnvironmentVariableCredentialsProvider.create(),
                SystemPropertyCredentialsProvider.create(),
                WebIdentityTokenFileCredentialsProvider.create(),
                ProfileCredentialsProvider.create(),
                new LocalStackAwsCredentialsProvider()))
        .build();
  }

  @Bean
  public AwsRegionProvider awsRegionProviderV2() {
    return new {{ SolutionName }}AwsRegionProviderChain();
  }
}
