package {{ root_package }}.aws.v2.core;

import software.amazon.awssdk.regions.providers.AwsProfileRegionProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProviderChain;
import software.amazon.awssdk.regions.providers.InstanceProfileRegionProvider;
import software.amazon.awssdk.regions.providers.SystemSettingsRegionProvider;

public class {{ SolutionName }}AwsRegionProviderChain extends AwsRegionProviderChain {

  public {{ SolutionName }}AwsRegionProviderChain() {
    super(
        new SystemSettingsRegionProvider(),
        new AwsProfileRegionProvider(),
        new InstanceProfileRegionProvider(),
        new DefaultRegionProvider());
  }
}
