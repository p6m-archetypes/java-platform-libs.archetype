package {{ root_package }}.aws.v2.core;

import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class {{ SolutionName }}AWSCredentialsProviderChain extends AWSCredentialsProviderChain {

    private static final {{ SolutionName }}AWSCredentialsProviderChain INSTANCE
            = new {{ SolutionName }}AWSCredentialsProviderChain();

    public {{ SolutionName }}AWSCredentialsProviderChain() {
        super(new EnvironmentVariableCredentialsProvider(),
                new SystemPropertiesCredentialsProvider(),
                WebIdentityTokenCredentialsProvider.create(),
                new ProfileCredentialsProvider(),
                new LocalStackCredentialsProvider()
        );
    }

    public static {{ SolutionName }}AWSCredentialsProviderChain getInstance() {
        return INSTANCE;
    }
}
