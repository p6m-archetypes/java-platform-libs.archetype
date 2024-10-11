package {{ root_package }}.aws.v2.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.utils.StringUtils;

public class LocalStackAwsCredentialsProvider implements AwsCredentialsProvider {

  protected static final String DISABLE_LOCALSTACK_CREDENTIALS_PROVIDER_KEY =
      "DISABLE_LOCALSTACK_CREDENTIALS_PROVIDER";
  protected static final String RUNTIME_ENV_KEY = "RUNTIME_ENV";
  protected static final String LOCAL_ENV = "local";
  protected static final String TILT_ENV = "tilt";
  private static final Logger LOGGER =
      LoggerFactory.getLogger(LocalStackAwsCredentialsProvider.class);
  private static final String LOCALSTACK_USER = "localstack";
  private static final String LOCALSTACK_PASS = "localstack";

  @Override
  public AwsCredentials resolveCredentials() {
    String runtimeEnv = StringUtils.trimToEmpty(System.getenv(RUNTIME_ENV_KEY));
    if (this.localStackCredentialsDisabled()) {
      throw SdkClientException.builder()
          .message(
              String.format(
                  "Unable to load AWS Credentials from "
                      + LocalStackAwsCredentialsProvider.class.getSimpleName()
                      + " when %s environment variable has been set.",
                  DISABLE_LOCALSTACK_CREDENTIALS_PROVIDER_KEY))
          .build();
    }
    if (!this.localStackCredentialsAllowedInEnvironment(runtimeEnv)) {
      throw SdkClientException.builder()
          .message(
              String.format(
                  "Unable to load AWS Credentials from "
                      + LocalStackAwsCredentialsProvider.class.getSimpleName()
                      + " when running in %s=%s.",
                  RUNTIME_ENV_KEY,
                  runtimeEnv))
          .build();
    }
    LOGGER.info("Using localstack credentials");
    return AwsBasicCredentials.create(LOCALSTACK_USER, LOCALSTACK_PASS);
  }

  private boolean localStackCredentialsAllowedInEnvironment(String runtimeEnv) {
    return runtimeEnv.isBlank()
        || runtimeEnv.equalsIgnoreCase(LOCAL_ENV)
        || runtimeEnv.equalsIgnoreCase(TILT_ENV);
  }

  private boolean localStackCredentialsDisabled() {
    return System.getenv(DISABLE_LOCALSTACK_CREDENTIALS_PROVIDER_KEY) != null;
  }
}
