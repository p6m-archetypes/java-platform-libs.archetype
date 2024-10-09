package {{ root_package }}.aws.v2.core;

import software.amazon.awssdk.regions.providers.AwsProfileRegionProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProviderChain;
import software.amazon.awssdk.regions.providers.InstanceProfileRegionProvider;
import software.amazon.awssdk.regions.providers.SystemSettingsRegionProvider;

public class {{ VentureName }}AwsRegionProviderChain extends AwsRegionProviderChain {

  public {{ VentureName }}AwsRegionProviderChain() {
    super(
        new SystemSettingsRegionProvider(),
        new AwsProfileRegionProvider(),
        new InstanceProfileRegionProvider(),
        new DefaultRegionProvider());
  }
}
