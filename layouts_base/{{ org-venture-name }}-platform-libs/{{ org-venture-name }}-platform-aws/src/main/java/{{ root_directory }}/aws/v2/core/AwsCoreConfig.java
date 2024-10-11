package {{ root_package }}.aws.v2.core;

import com.amazonaws.auth.AWSCredentialsProvider;
import io.awspring.cloud.core.region.DefaultAwsRegionProviderChainDelegate;
import io.awspring.cloud.core.region.RegionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsCoreConfig {

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        return new {{ SolutionName }}AWSCredentialsProviderChain();
    }

    @Bean
    public RegionProvider regionProvider() {
        return new DefaultAwsRegionProviderChainDelegate();
    }
}
