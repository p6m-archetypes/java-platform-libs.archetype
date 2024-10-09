package {{ root_package }}.proto;

import io.grpc.stub.StreamObserver;

import java.util.function.Function;

public interface GrpcServiceHelper {

  default  <Request, Response> void executeGrpcCall(Request request,
                                                   StreamObserver<Response> responseObserver,
                                                   Function<Request, Response> grpcCall) {
    Response response = grpcCall.apply(request);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

}
