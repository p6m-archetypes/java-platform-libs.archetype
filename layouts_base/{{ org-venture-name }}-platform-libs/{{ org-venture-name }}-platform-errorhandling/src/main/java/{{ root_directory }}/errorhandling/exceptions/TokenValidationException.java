package {{ root_package }}.errorhandling.exceptions;

import com.google.rpc.Code;
import com.google.rpc.Status;

public class TokenValidationException extends {{ VentureName }}Exception {

    private static final String ACCESS_DENIED_MESSAGE = "Access is denied";

    @Override
    public Status toStatus() {
        return Status.newBuilder()
                .setCode(Code.UNAUTHENTICATED_VALUE)
                .setMessage(ACCESS_DENIED_MESSAGE)
                .build();
    }

}
