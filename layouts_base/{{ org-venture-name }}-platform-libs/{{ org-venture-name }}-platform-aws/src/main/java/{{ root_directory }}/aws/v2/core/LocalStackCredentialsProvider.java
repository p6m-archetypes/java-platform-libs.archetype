package {{ root_package }}.aws.v2.core;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.util.StringUtils;

public class LocalStackCredentialsProvider implements AWSCredentialsProvider {

    protected static final String DISABLE_LOCALSTACK_CREDENTIALS_PROVIDER_KEY = "DISABLE_LOCALSTACK_CREDENTIALS_PROVIDER";
    protected static final String RUNTIME_ENV_KEY = "RUNTIME_ENV";
    protected static final String LOCAL_ENV = "local";
    protected static final String TILT_ENV = "tilt";
    private static final String LOCALSTACK_USER = "localstack";
    private static final String LOCALSTACK_PASS = "localstack";

    @Override
    public AWSCredentials getCredentials() {
        final String runtimeEnv = System.getenv(RUNTIME_ENV_KEY);

        if (localStackCredentialsDisabled()) {
            throw new SdkClientException(String.format("Unable to load AWS Credentials from "
                    + LocalStackCredentialsProvider.class.getSimpleName()
                    + " when %s environment variable has been set.", DISABLE_LOCALSTACK_CREDENTIALS_PROVIDER_KEY));
        }

        if (!localStackCredentialsAllowedInEnvironment(runtimeEnv)) {
            throw new SdkClientException(String.format("Unable to load AWS Credentials from "
                    + LocalStackCredentialsProvider.class.getSimpleName()
                    + " when running in %s=%s.", RUNTIME_ENV_KEY, runtimeEnv));
        }

        return new BasicAWSCredentials(LOCALSTACK_USER, LOCALSTACK_PASS);
    }

    @Override
    public void refresh() {

    }

    private boolean localStackCredentialsAllowedInEnvironment(String runtimeEnv) {
        return StringUtils.isNullOrEmpty(runtimeEnv)
                || runtimeEnv.equalsIgnoreCase(LOCAL_ENV)
                || runtimeEnv.equalsIgnoreCase(TILT_ENV);
    }

    private boolean localStackCredentialsDisabled() {
        return System.getenv(DISABLE_LOCALSTACK_CREDENTIALS_PROVIDER_KEY) != null;
    }
}
