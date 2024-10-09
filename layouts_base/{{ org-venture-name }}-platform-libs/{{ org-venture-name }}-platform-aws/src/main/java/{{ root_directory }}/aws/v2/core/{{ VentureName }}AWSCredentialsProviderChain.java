package {{ root_package }}.aws.v2.core;

import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

public class {{ VentureName }}AWSCredentialsProviderChain extends AWSCredentialsProviderChain {

    private static final {{ VentureName }}AWSCredentialsProviderChain INSTANCE
            = new {{ VentureName }}AWSCredentialsProviderChain();

    public {{ VentureName }}AWSCredentialsProviderChain() {
        super(new EnvironmentVariableCredentialsProvider(),
                new SystemPropertiesCredentialsProvider(),
                WebIdentityTokenCredentialsProvider.create(),
                new ProfileCredentialsProvider(),
                new LocalStackCredentialsProvider()
        );
    }

    public static {{ VentureName }}AWSCredentialsProviderChain getInstance() {
        return INSTANCE;
    }
}
