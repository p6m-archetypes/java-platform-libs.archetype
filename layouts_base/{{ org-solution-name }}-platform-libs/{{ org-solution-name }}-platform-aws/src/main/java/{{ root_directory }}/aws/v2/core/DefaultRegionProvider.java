package {{ root_package }}.aws.v2.core;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;

public class DefaultRegionProvider implements AwsRegionProvider {
  @Override
  public Region getRegion() throws SdkClientException {
    // This is the region that Localstack uses by default
    return Region.US_EAST_1;
  }
}
