package {{root_package}}.test;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import org.grpcmock.GrpcMock;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static io.grpc.ManagedChannelBuilder.forAddress;

/**
 * This is a helper class that replaces underlying channels in client classes with mock channels.
 * Here is an example of how to use it
 * <pre>
 *  GrpcMockClientConfigurer.configure(server.getContext())
 *      .client(AccountServiceClient.class, AccountServiceGrpc.class)
 *      .client(UserServiceClient.class, UserServiceGrpc.class)
 *      .client(RouterServiceClient.class, RouterServiceGrpc.class);
 * </pre>
 * <p>
 * In {@link GrpcMockClientConfigurer#configure} method you need provide Spring application context that defines
 * the clients you want to reconfigure. In {@link GrpcMockClientConfigurer#client} you need to specify two classes:
 * a class of a client you want to mock and its gRPC implementation. E.g., UserServiceClient and UserServiceGrpc.
 * <p>
 * Important: Make sure you have <pre>@ExtendWith(GrpcMockExtension.class)</pre> in the test where you use the mocks.
 * <p>
 * Here is an example of the mock usage:
 * <pre>
 * stubFor(unaryMethod(UserServiceGrpc.getGetUserMethod())
 *      .withRequest(GetUserDto.newBuilder()
 *              .setUserId(userId)
 *              .build())
 *      .willReturn(response(UserDto.newBuilder()
 *              .setUserId(userId)
 *              .setFirstName(randomAlphabetic())
 *              .build())));
 * </pre>
 * <p>
 * You can find more examples here - https://github.com/Fadelis/grpcmock
 */
public class GrpcMockClientConfigurer {
    private final ApplicationContext applicationContext;
    private final ManagedChannel managedChannel;

    private GrpcMockClientConfigurer(ApplicationContext applicationContext, int port) {
        this.applicationContext = applicationContext;
        managedChannel = forAddress("localhost", port == 0 ? GrpcMock.getGlobalPort() : port)
                .usePlaintext()
                .build();
    }

    public static GrpcMockClientConfigurer configure(ApplicationContext context) {
        return new GrpcMockClientConfigurer(context, 0);
    }

    public static GrpcMockClientConfigurer configure(ApplicationContext context, int port) {
        return new GrpcMockClientConfigurer(context, port);
    }

    public GrpcMockClientConfigurer client(Class<?> clientClass, Class<?> grpcClass) {
        reconfigureClient(clientClass, grpcClass);
        return this;
    }

    private void reconfigureClient(Class<?> clientClass, Class<?> grpcClass) {
        Object client = applicationContext.getBean(clientClass);
        try {
            Method newBlockingStubMethod = grpcClass.getDeclaredMethod("newBlockingStub", Channel.class);
            Object newBlockingStub = newBlockingStubMethod.invoke(null, managedChannel);
            ReflectionTestUtils.setField(client, "stub", newBlockingStub);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
